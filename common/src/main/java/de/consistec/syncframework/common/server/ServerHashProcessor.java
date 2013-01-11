package de.consistec.syncframework.common.server;

import static de.consistec.syncframework.common.MdTableDefaultValues.SERVER_FLAG;
import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.adapter.DatabaseAdapterCallback;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.MDEntry;
import de.consistec.syncframework.common.exception.ServerStatusException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.i18n.Errors;
import de.consistec.syncframework.common.i18n.Infos;
import de.consistec.syncframework.common.i18n.Warnings;
import de.consistec.syncframework.common.util.LoggingUtil;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.slf4j.cal10n.LocLogger;

/**
 * The class {@code ServerHashProcessor} is responsible for applying the changes from client
 * on the server.
 * On the basis of the passed client revision the server determines if the client is up to date.
 * If the client is not up to date then a {@code ServerStatusException} is thrown so that the
 * client can retry the synchronization. Then the {@code ServerHashProcessor} looks for each client change
 * in the server's meta and data table and does the following operations depends on the result:
 * <br/>
 * <ul>
 * <li>If no entry related to client change exists in meta table => CLIENT-ADD</li>
 * <li>If an entry exists in meta table:</li>
 * <ul>
 * <li>if client change is marked as deleted => CLIENT-DEL,</li>
 * <li>if client change is not marked as deleted => CLIENT-MOD,</li>
 * <li>if the revision of client change is 0 => illegal state (throws IllegalStateException) and</li>
 * <li>if the revision of client change is not equal the revision of server's md entry =>
 * illegal state (throws IllegalStateException)</li>
 * </ul>
 * </ul>
 * <p/>
 * <br/>
 * Which operation the {@code ServerHashProcessor} will proceed depends on the above listed the states.
 * <p/>
 * <table>
 * <tr><th>State</th><th>Operation</th></tr>
 * <tr><td>CLIENT-ADD</td><td>insertMDRow and insertDataRow</td></tr>
 * <tr><td>CLIENT-DEL</td><td>deleteDataRow and updateMDRow</td></tr>
 * <tr><td>CLIENT-MOD</td><td>insertDataRow or updateDataRow and updateMDRow</td></tr>
 * <tr><td>ADD-ADD-Conflict (remote entry revision equals 0)</td>
 * <td>That shouldn't happen! There will be thrown an IllegalStateException.</td></tr>
 * <tr><td>OUT-OF-DATE (remote entry revision not equal server entry revision)</td>
 * <td>That shouldn't happen! There will be thrown an IllegalStateException.</td></tr>
 * </table>
 *
 * @author Markus Backes
 * @company Consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public class ServerHashProcessor {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private static final LocLogger LOGGER = LoggingUtil.createLogger(ServerHashProcessor.class.getCanonicalName());
    private static final Config CONF = Config.getInstance();
    private IDatabaseAdapter adapter;

    //</editor-fold>

    /**
     * Instantiates a new server hash processor.
     *
     * @param adapter Database adapter.
     */
    public ServerHashProcessor(IDatabaseAdapter adapter) {
        this.adapter = adapter;
        LOGGER.debug("HashProcessor Constructor finished");
    }

    /**
     * Apply changes from client on server.
     *
     * @param clientChanges Client changes.
     * @param clientRevision Client revision.
     * @return New revision.
     * @throws DatabaseAdapterException the adapter exception
     * @throws ServerStatusException
     */
    public int applyChangesFromClientOnServer(List<Change> clientChanges, int clientRevision) throws
        DatabaseAdapterException, ServerStatusException {

        LOGGER.debug("applyChangesFromClientOnServer called");

        final int nextRev = adapter.getNextRevision();
        LOGGER.info(Infos.COMMON_NEW_SERVER_REVISION, nextRev);

        LOGGER.debug("compare client revision with current server revision {} : {}", clientRevision, (nextRev - 1));

        if (clientRevision != (nextRev - 1)) {
            LOGGER.warn(Warnings.COMMON_CANT_APLY_CLIENT_CHANGES_ON_SERVER);
            throw new ServerStatusException(ServerStatus.CLIENT_NOT_UPTODATE, read(Errors.COMMON_UPDATE_NECESSARY));
        }

        for (final Change remoteChange : clientChanges) {

            final MDEntry remoteEntry = remoteChange.getMdEntry();
            final Map<String, Object> remoteRowData = remoteChange.getRowData();
            LOGGER.debug("processing: {}", remoteEntry.toString());

            adapter.getRowForPrimaryKey(remoteEntry.getPrimaryKey(),
                remoteEntry.getTableName() + CONF.getMdTableSuffix(),
                new DatabaseAdapterCallback<ResultSet>() {
                    @Override
                    public void onSuccess(final ResultSet hashRst) throws DatabaseAdapterException {
                        adapter.getRowForPrimaryKey(remoteEntry.getPrimaryKey(), remoteEntry.getTableName(),
                            new DatabaseAdapterCallback<ResultSet>() {
                                @Override
                                public void onSuccess(final ResultSet dataRst) throws DatabaseAdapterException {
                                    LOGGER.debug("call processResultSets ...");
                                    try {
                                        processResultSets(hashRst, dataRst, nextRev, remoteChange, remoteEntry,
                                            remoteRowData);
                                    } catch (SQLException e) {
                                        throw new DatabaseAdapterException(e);
                                    } catch (NoSuchAlgorithmException e) {
                                        throw new DatabaseAdapterException(e);
                                    }
                                }
                            });
                    }
                });

        }
        LOGGER.debug("applyChangesFromClientOnServer called");
        return nextRev;
    }

    private void processResultSets(ResultSet hashRst, ResultSet data, int nextRev, Change remoteChange,
                                   MDEntry remoteEntry,
                                   Map<String, Object> remoteRowData
    ) throws SQLException, DatabaseAdapterException, NoSuchAlgorithmException {

        LOGGER.debug("processResultSets called");
        if (hashRst.next()) {
//            int localRev = hashRst.getInt("rev");

            // normally the add add conflict cannot happen because the client refreshes his revision through server change set.
            // also normally the out of date conflict cannot happen because the client revision is checked before
            // server wants to apply the client changeset.
//            if (remoteEntry.getRevision() == 0) {
//
//                // ADD-ADD Conflict
//                LOGGER.error(Errors.COMMON_ADD_ADD_CONFLICT_SHOULDNT_OCCUR);
//                throw new IllegalStateException(read(Errors.COMMON_ADD_ADD_CONFLICT_SHOULDNT_OCCUR));
//
//            } else if (remoteEntry.getRevision() != localRev) {
//
//                // OUT OF DATE
//                LOGGER.error(Errors.COMMON_OUT_OF_DATE_SHOULDNT_OCCUR);
//                throw new IllegalStateException(read(Errors.COMMON_OUT_OF_DATE_SHOULDNT_OCCUR));
//
//            }
            if (!remoteEntry.isExists()) {

                // CLIENT DEL
                LOGGER.info(Infos.COMMON_CLIENT_DELETED_CASE_DETECTED);
                adapter.deleteRow(remoteEntry.getPrimaryKey(), remoteEntry.getTableName());
                adapter.updateMdRow(nextRev, SERVER_FLAG, remoteEntry.getPrimaryKey(), null,
                    remoteEntry.getTableName());

            } else {

                // CLIENT MOD
                LOGGER.info(Infos.COMMON_CLIENT_MODIFIED_CASE_DETECTED);

                if (!data.next()) {
                    adapter.insertDataRow(remoteRowData, remoteEntry.getTableName());
                } else {
                    adapter.updateDataRow(remoteRowData, remoteEntry.getPrimaryKey(), remoteEntry.getTableName());
                }

                adapter.updateMdRow(nextRev, SERVER_FLAG, remoteEntry.getPrimaryKey(), remoteChange.calculateHash(),
                    remoteEntry.getTableName());
            }
        } else {

            // CLIENT ADD
            LOGGER.info(Infos.COMMON_CLIENT_ADDED_CASE_DETECTED);
            LOGGER.debug("insert md row with rev: {} and client pk: {}", nextRev, remoteEntry.getPrimaryKey());
            // insert hash
            adapter.insertMdRow(nextRev, SERVER_FLAG, remoteEntry.getPrimaryKey(), remoteChange.calculateHash(),
                remoteEntry.getTableName());
            // insert data
            adapter.insertDataRow(remoteRowData, remoteEntry.getTableName());
        }
    }
}
