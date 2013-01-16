package de.consistec.syncframework.common.server;

import static de.consistec.syncframework.common.util.CollectionsUtil.newArrayList;
import static de.consistec.syncframework.common.util.CollectionsUtil.newHashMap;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.Tuple;
import de.consistec.syncframework.common.adapter.DatabaseAdapterCallback;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.MDEntry;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.i18n.Infos;
import de.consistec.syncframework.common.util.LoggingUtil;
import de.consistec.syncframework.common.util.StringUtil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.slf4j.cal10n.LocLogger;

/**
 * The {@code ServerChangesEnumerator} is responsible for creating a list
 * of {@code Change} objects which represents all changes on server tables to sync.
 * <br/>
 * For all inserted, modified or deleted rows that revision in the server md-table
 * is greater then the passed one, the {@code ServerChangesEnumerator} creates a {@code Change}
 * object. This object consists of meta data values {@code MDEntry} and
 * row data values. The row data values are represented as Map<String, Object>, where the columnname
 * the key and Object the values in the data row are.
 *
 * @author Markus Backes
 * @company Consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public class ServerChangesEnumerator {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private static final LocLogger LOGGER = LoggingUtil.createLogger(ServerChangesEnumerator.class.getCanonicalName());
    private static final transient Config CONF = Config.getInstance();
    private static final int METADATA_COLUMN_COUNT = 4;
    private IDatabaseAdapter adapter = null;
    private TableSyncStrategies tableSyncStrategies;
    //</editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class constructors " >

    /**
     * Instantiates a new server changes enumerator.
     *
     * @param adapter Database adapter
     * @param tableSyncStrategies The configured sync strategies for tables
     */
    public ServerChangesEnumerator(IDatabaseAdapter adapter, TableSyncStrategies tableSyncStrategies) {
        this.adapter = adapter;
        this.tableSyncStrategies = tableSyncStrategies;
        LOGGER.debug("ChangesEnumerator Constructor finished");
    }

    //</editor-fold>
    //<editor-fold defaultstate="expanded" desc=" Class methods" >

    /**
     * Creates the list of {@code Change} objects for all inserted, modified or deleted data rows
     * which revision is greater than {@code rev}.
     *
     * @param rev the revision
     * @return the changes
     * @throws DatabaseAdapterException the adapter exception
     */
    public Tuple<Integer, List<Change>> getChanges(int rev) throws DatabaseAdapterException {

        LOGGER.debug("getServerChanges called");

        final List<Change> list = newArrayList();
        int revision = adapter.getLastRevision();
        final Tuple<Integer, List<Change>> revisionChangesetTuple = new Tuple<Integer, List<Change>>(revision, list);

        for (final String syncTable : CONF.getSyncTables()) {

            adapter.getChangesForRevision(rev, syncTable, new DatabaseAdapterCallback<ResultSet>() {
                @Override
                public void onSuccess(ResultSet resultSet) throws DatabaseAdapterException {
                    try {
                        while (resultSet.next()) {

                            Change tmpChange = new Change();

                            MDEntry tmpEntry = getMetadata(resultSet);
                            tmpChange.setMdEntry(tmpEntry);

                            Map<String, Object> rowData = getRowData(resultSet);
                            tmpChange.setRowData(rowData);

                            SyncDirection syncDirection = tableSyncStrategies.getSyncStrategyForTable(
                                syncTable).getDirection();
                            if (syncDirection != SyncDirection.CLIENT_TO_SERVER) {
                                revisionChangesetTuple.getValue2().add(tmpChange);
                            }

                            int revision = tmpChange.getMdEntry().getRevision();
                            if (revisionChangesetTuple.getValue1() < revision) {
                                revisionChangesetTuple.setValue1(revision);
                            }
                            LOGGER.info(Infos.COMMON_ADDED_SERVER_CHANGE_TO_CHANGE_SET, tmpChange.toString());
                        }
                    } catch (SQLException e) {
                        throw new DatabaseAdapterException(e);
                    }
                }

                private MDEntry getMetadata(ResultSet resultSet) throws SQLException {
                    ResultSetMetaData meta = resultSet.getMetaData();

                    MDEntry tmpEntry = new MDEntry();
                    tmpEntry.setTableName(syncTable);

                    String columnName;

                    for (int i = 1; i <= METADATA_COLUMN_COUNT; i++) {
                        columnName = meta.getColumnName(i);

                        if ("rev".equalsIgnoreCase(columnName)) {
                            tmpEntry.setRevision(resultSet.getInt(i));
                        } else if ("pk".equalsIgnoreCase(columnName)) {
                            tmpEntry.setPrimaryKey(resultSet.getObject(i));
                        } else if ("mdv".equalsIgnoreCase(columnName)) {
                            String mdv = resultSet.getString(i);
                            if (StringUtil.isNullOrEmpty(mdv)) {
                                tmpEntry.setDeleted();
                            } else {
                                tmpEntry.setModified();
                            }
                        } else if ("f".equalsIgnoreCase(columnName)) {
                            // do nothing, we don't want to sync the f flag
                            continue;
                        }
                    }
                    return tmpEntry;
                }

                private Map<String, Object> getRowData(ResultSet resultSet) throws SQLException {
                    Map<String, Object> rowData = newHashMap();
                    ResultSetMetaData meta = resultSet.getMetaData();

                    int columnCount = meta.getColumnCount();

                    for (int i = METADATA_COLUMN_COUNT + 1; i <= columnCount; i++) {
                        rowData.put(meta.getColumnName(i), resultSet.getObject(i));
                    }
                    return rowData;
                }
            });
        }
        LOGGER.debug("getServerChanges finished");
        return revisionChangesetTuple;
    }
    //</editor-fold>
}
