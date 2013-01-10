package de.consistec.syncframework.common.client;

import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.IConflictListener;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.TableSyncStrategy;
import de.consistec.syncframework.common.adapter.DatabaseAdapterCallback;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.conflict.ConflictStrategyFactory;
import de.consistec.syncframework.common.conflict.IConflictStrategy;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.MDEntry;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.i18n.Errors;
import de.consistec.syncframework.common.i18n.Warnings;
import de.consistec.syncframework.common.util.LoggingUtil;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.cal10n.LocLogger;

/**
 * The class {@code ClientHashProcessor} is responsible for applying changes from server
 * on the client. Additionally this class updates the revision for each server change
 * in the meta table.
 * <br/>
 * <p/>
 * To apply the server changes the {@code ClientHashProcessor} looks for each server change
 * in the client's meta and data table and inserts, modifies or deletes the corresponding client entries,
 * depending on the configured conflict strategies CLIENT_WINS or SERVER_WINS and detected
 * possible conflict types which are defined in {@link de.consistec.syncframework.common.conflict.ConflictType}
 * <br/>
 *
 * @author Markus Backes
 * @company Consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public class ClientHashProcessor {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private static final LocLogger LOGGER = LoggingUtil.createLogger(ClientHashProcessor.class.getCanonicalName());
    private static final Config CONF = Config.getInstance();
    private IConflictListener conflictListener;
    private IDatabaseAdapter adapter;
    private TableSyncStrategies strategies;

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Class constructors " >

    /**
     * Instantiates a new client hash processor.
     *
     * @param adapter Database adapter object.
     * @param conflictListener ConflictListener for resolving conflicts when
     * @param strategies Special synchronization strategies for tables.
     * {@link de.consistec.syncframework.common.conflict.ConflictStrategy ConflictStrategy} is FIRE_EVENT.
     */
    public ClientHashProcessor(IDatabaseAdapter adapter, TableSyncStrategies strategies,
                               IConflictListener conflictListener
    ) {

        this.adapter = adapter;
        this.strategies = strategies;
        this.conflictListener = conflictListener;
        LOGGER.debug("HashProcessor Constructor finished");

    }

    //</editor-fold>
    //<editor-fold defaultstate="expanded" desc=" Class methods " >

    /**
     * Apply changes from server on client.
     *
     * @param serverChanges the server changes
     * @param clientChanges the client changes
     * @throws SyncException the sync exception
     */
    public void applyChangesFromServerOnClient(List<Change> serverChanges, List<Change> clientChanges)
        throws SyncException, DatabaseAdapterException, NoSuchAlgorithmException {

        LOGGER.debug("applyChangesFromServerOnClient called");

        Collections.sort(serverChanges, Change.getPrimaryKeyComparator());
        Collections.sort(clientChanges, Change.getPrimaryKeyComparator());

        for (final Change remoteChange : serverChanges) {

            int foundIndex = Collections.binarySearch(clientChanges, remoteChange, Change.getPrimaryKeyComparator());

            final MDEntry remoteEntry = remoteChange.getMdEntry();
            LOGGER.debug("processing: {}", remoteEntry.toString());

            if (isConflict(foundIndex)) {
                Change clientChange = clientChanges.get(foundIndex);
                try {
                    resolveConflict(remoteChange, clientChange);
                } catch (SQLException e) {
                    throw new DatabaseAdapterException(e);
                }
            } else {
                applyServerChange(remoteChange);
            }

//            adapter.getRowForPrimaryKey(remoteEntry.getPrimaryKey(),
//                remoteEntry.getTableName() + CONF.getMdTableSuffix(),
//                new DatabaseAdapterCallback<ResultSet>() {
//                    @Override
//                    public void onSuccess(final ResultSet localHashResultSet) throws DatabaseAdapterException {
//
//                        adapter.getRowForPrimaryKey(remoteEntry.getPrimaryKey(), remoteEntry.getTableName(),
//                            new DatabaseAdapterCallback<ResultSet>() {
//                                @Override
//                                public void onSuccess(final ResultSet localDataResultSet) throws
//                                    DatabaseAdapterException {
//                                    try {
//                                        processServerChange(localHashResultSet, localDataResultSet, remoteChange);
//                                    } catch (SQLException e) {
//                                        throw new DatabaseAdapterException(e);
//                                    } catch (NoSuchAlgorithmException e) {
//                                        throw new DatabaseAdapterException(e);
//                                    } catch (SyncException e) {
//                                        throw new DatabaseAdapterException(e);
//                                    }
//                                }
//                            });
//                    }
//                });
        }
        LOGGER.debug("applyChangesFromServerOnClient finished");
    }

    private void applyServerChange(final Change serverChange) throws DatabaseAdapterException,
        NoSuchAlgorithmException {

        final MDEntry remoteEntry = serverChange.getMdEntry();
        final Map<String, Object> remoteRowData = serverChange.getRowData();

        // SERVER ADD, MOD OR DEL
        if (serverChange.getMdEntry().isExists()) {
            adapter.getRowForPrimaryKey(serverChange.getMdEntry().getPrimaryKey(), remoteEntry.getTableName(),
                new DatabaseAdapterCallback<ResultSet>() {
                    @Override
                    public void onSuccess(final ResultSet result) throws DatabaseAdapterException, SQLException {
                        String hash = null;
                        try {
                            hash = serverChange.calculateHash();
                        } catch (NoSuchAlgorithmException e) {
                            throw new DatabaseAdapterException(e);
                        }
                        if (result.next()) {
                            // SERVER MOD
                            adapter.updateDataRow(serverChange.getRowData(), remoteEntry.getPrimaryKey(),
                                remoteEntry.getTableName());
                            adapter.updateMdRow(remoteEntry.getRevision(), 0, remoteEntry.getPrimaryKey(),
                                hash, remoteEntry.getTableName());
                        } else {
                            // SERVER ADD
                            // on initialization everything is a server add, but deleted items no longer need to be added
                            adapter.insertDataRow(remoteRowData, remoteEntry.getTableName());
                            adapter.insertMdRow(remoteEntry.getRevision(), 0, remoteEntry.getPrimaryKey(),
                                hash, remoteEntry.getTableName());
                        }
                    }
                });
        } else {
            // SERVER DEL
            adapter.getRowForPrimaryKey(serverChange.getMdEntry().getPrimaryKey(), remoteEntry.getTableName(),
                new DatabaseAdapterCallback<ResultSet>() {
                    @Override
                    public void onSuccess(final ResultSet result) throws DatabaseAdapterException, SQLException {
                        String hash = null;
                        try {
                            hash = serverChange.calculateHash();
                        } catch (NoSuchAlgorithmException e) {
                            throw new DatabaseAdapterException(e);
                        }
                        if (result.next()) {
                            adapter.deleteRow(remoteEntry.getPrimaryKey(), remoteEntry.getTableName());
                            adapter.updateMdRow(remoteEntry.getRevision(), 0, remoteEntry.getPrimaryKey(),
                                null, remoteEntry.getTableName());
                        } else {
                            adapter.insertMdRow(remoteEntry.getRevision(), 0, remoteEntry.getPrimaryKey(),
                                null, remoteEntry.getTableName());
                        }
                    }
                });
        }
    }

    private boolean isConflict(int foundIndex) {
        return foundIndex != -1;
    }

//    private void processServerChange(ResultSet localHashResultSet, ResultSet localDataResultSet, Change remoteChange)
//        throws SQLException, DatabaseAdapterException, NoSuchAlgorithmException, SyncException {
//
//        MDEntry remoteEntry = remoteChange.getMdEntry();
//        Map<String, Object> remoteRowData = remoteChange.getRowData();
//
//        if (localHashResultSet.next()) {
//            resolveConflicts(remoteChange, localDataResultSet, localHashResultSet);
//        } else {
//
//            // SERVER ADD
//            if (remoteEntry.isExists()) {
//
//                // on initialization everything is a server add, but deleted items no longer need to be added
//                adapter.insertDataRow(remoteRowData, remoteEntry.getTableName());
//                adapter.insertMdRow(remoteEntry.getRevision(), 0, remoteEntry.getPrimaryKey(),
//                    remoteChange.calculateHash(), remoteEntry.getTableName());
//
//            } else {
//                adapter.insertMdRow(remoteEntry.getRevision(), 0, remoteEntry.getPrimaryKey(), null,
//                    remoteEntry.getTableName());
//            }
//        }
//    }

    private void logConflictInfo(final String conflict, final Change remoteChange, final int localRev,
                                 final int localFlag
    ) {

        StringBuilder builder = new StringBuilder("\n/*---------------------  Conflict info   ---------------------");
        builder.append("\n * Conflict: ");
        builder.append(conflict);
        builder.append("\n * Server Change: ");
        builder.append(remoteChange);
        builder.append("\n * Client localRev: ");
        builder.append(localRev);
        builder.append(", localFlag: ");
        builder.append(localFlag);
        builder.append("\n * ---------------------  Conflict info   -------------------*/\n");

        LOGGER.debug(builder.toString());
    }

    private void resolveConflict(final Change serverChange, final Change clientChange)
        throws DatabaseAdapterException, NoSuchAlgorithmException, SQLException, SyncException {

        MDEntry remoteEntry = serverChange.getMdEntry();
        Map<String, Object> remoteRowData = serverChange.getRowData();

        ConflictHandlingData data = new ConflictHandlingData(clientChange, serverChange);

        TableSyncStrategy tableSyncStrategy = strategies.getSyncStrategyForTable(
            remoteEntry.getTableName());

        ConflictStrategy conflictStrategy = tableSyncStrategy.getConflictStrategy();
        LOGGER.info("Conflict Action: {}", conflictStrategy.name());

        SyncDirection syncDirection = tableSyncStrategy.getDirection();
        LOGGER.info("Sync Direction: {}", syncDirection.name());

        IConflictStrategy conflictHandlingStrategy = ConflictStrategyFactory.newInstance(syncDirection);

        switch (conflictStrategy) {
            case CLIENT_WINS:
                conflictHandlingStrategy.resolveByClientWinsStrategy(adapter, data);
                break;
            case SERVER_WINS:
                conflictHandlingStrategy.resolveByServerWinsStrategy(adapter, data);
                break;
            case FIRE_EVENT:
                resolveConflictsFireEvent(data, conflictHandlingStrategy);
                break;
            default:
                throw new IllegalStateException(
                    String.format("not allowed conflict strategy %s configured!", conflictStrategy.name()));
//            default:
//                LOGGER.debug("ClientHashProcessor#resolveConflicts-default");
//                if (ConflictType.SERVER_DEL.isTheCase(localRev, localFlag, localMdv, remoteEntry)) {
//                    // SERVER Del
//                    LOGGER.debug("Server DEL");
//                } else {
//                    // SERVER MOD
//                    LOGGER.debug("Server MOD");
//                }
//
//                if (!localDataResultSet.next()) {
//                    adapter.insertDataRow(remoteRowData, remoteEntry.getTableName());
//                } else {
//                    adapter.updateDataRow(remoteRowData, remoteEntry.getPrimaryKey(), remoteEntry.getTableName());
//                }
//                adapter.updateMdRow(remoteEntry.getRevision(), 0, remoteEntry.getPrimaryKey(),
//                    remoteChange.calculateHash(), remoteEntry.getTableName());
        }
    }

    private boolean rowHasData(Map<String, Object> clientData) {
        if (clientData.size() > 0) {
            for (Object obj : clientData.values()) {
                if (obj != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Update client revision.
     *
     * @param clientChanges Client changes.
     * @param rev Client revision.
     * @throws DatabaseAdapterException When update fails.
     */
    public void updateClientRevision(List<Change> clientChanges, int rev)
        throws DatabaseAdapterException {

        LOGGER.debug("Updating client revisions on hashtable");
        MDEntry tmpMDEntry;
        for (Change change : clientChanges) {
            tmpMDEntry = change.getMdEntry();
            int result = adapter.updateRevision(rev, tmpMDEntry.getTableName() + CONF.getMdTableSuffix(),
                tmpMDEntry.getPrimaryKey());
            if (result != 1) {
                LOGGER.warn(read(Warnings.COMMON_CANT_UPDATE_CLIENT_REV));
            }
        }

    }

    private void resolveConflictsFireEvent(ConflictHandlingData data,
                                           IConflictStrategy conflictHandlingStrategy
    )
        throws SQLException, SyncException, NoSuchAlgorithmException, DatabaseAdapterException {

        if (null == conflictListener) {
            throw new SyncException(read(Errors.COMMON_NO_CONFLICT_LISTENER_FOUND));
        } else {
            conflictHandlingStrategy.resolveByFireEvent(adapter, data, data.getLocalData(), conflictListener);
        }
    }

//    private void resolveConflictsFireEvent(ConflictHandlingData data,
//                                           IConflictStrategy conflictHandlingStrategy
//    )
//        throws SQLException, SyncException, NoSuchAlgorithmException, DatabaseAdapterException {
//
//        if (null == conflictListener) {
//            throw new SyncException(read(Errors.COMMON_NO_CONFLICT_LISTENER_FOUND));
//        } else {
//
//            Map<String, Object> clientData = newHashMap();
//            if (localDataResultSet.next()) {
//                ResultSetMetaData meta = localDataResultSet.getMetaData();
//                int columnCount = meta.getColumnCount();
//                for (int i = 1; i <= columnCount; i++) {
//                    clientData.put(meta.getColumnName(i),
//                        localDataResultSet.getObject(i));
//                }
//            }
//
//            conflictHandlingStrategy.resolveByFireEvent(adapter, data, clientData, conflictListener);
//        }
//    }

    //</editor-fold>
}
