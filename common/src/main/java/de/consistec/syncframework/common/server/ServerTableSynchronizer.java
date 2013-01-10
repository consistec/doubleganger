package de.consistec.syncframework.common.server;

import static de.consistec.syncframework.common.MdTableDefaultValues.SERVER_FLAG;
import static de.consistec.syncframework.common.util.CollectionsUtil.newHashMap;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.adapter.DatabaseAdapterCallback;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.i18n.Infos;
import de.consistec.syncframework.common.util.HashCalculator;
import de.consistec.syncframework.common.util.LoggingUtil;
import de.consistec.syncframework.common.util.StringUtil;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.slf4j.cal10n.LocLogger;

/**
 * Provides methods for performing synchronization on monitored tables on server site.
 * <p/>
 * The {@code ServerTableSynchronizer} provides methods for performing synchronization
 * on monitored tables on server site.
 * <br/>
 * <p>If changed rows are detected then the {@code ServerTableSynchronizer } calculates
 * a new hash entry for each of them and updates it in the md-table. For new rows the
 * {@code ServerTableSynchronizer } creates a new md table entry. If deleted rows are
 * detected and an md table entry exists then the {@code ServerTableSynchronizer }
 * updates the md table entry with an empty hash value and marks it so as deleted.</p>
 *
 * @author Markus Backes
 * @company Consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public class ServerTableSynchronizer {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(ServerTableSynchronizer.class.getCanonicalName());
    private static final transient Config CONF = Config.getInstance();
    private static final String MDV_COLUMN_NAME = "mdv";
    private IDatabaseAdapter adapter;

    /**
     * Instantiates a new server table synchronizer.
     *
     * @param adapter the database adapter
     */
    public ServerTableSynchronizer(IDatabaseAdapter adapter) {
        this.adapter = adapter;
        LOGGER.debug("ServerTableSynchronizer Constructor finished");
    }

    /**
     * Synchronize server tables.
     * <p/>
     * Iterates through monitored tables and process/delete rows.
     *
     * @throws DatabaseAdapterException the adapter exception
     */
    public void synchronizeServerTables() throws DatabaseAdapterException {

        LOGGER.debug("synchronizeServerTables called");

        final int rev = adapter.getNextRevision();
        LOGGER.debug("next revision number is {}", rev);
        LOGGER.debug("searching for changed rows");

        for (final String table : CONF.getSyncTables()) {

            LOGGER.debug("Processing table {}", table);
            searchAndProcessChangedRows(rev, table);
            searchAndProcessDeletedRows(rev, table);

        }

        LOGGER.debug("synchronizeServerTables finished");
    }

    private void searchAndProcessChangedRows(final int rev, final String table) throws DatabaseAdapterException {

        final List<String> columns = adapter.getColumns(table);
        adapter.getAllRowsFromTable(table, new DatabaseAdapterCallback<ResultSet>() {
            @Override
            public void onSuccess(ResultSet allRows) throws DatabaseAdapterException {
                try {
                    String mdTable = table + CONF.getMdTableSuffix();
                    Map<String, Object> rowData = newHashMap();
                    while (allRows.next()) {
                        for (String s : columns) {
                            rowData.put(s, allRows.getObject(s));
                        }
                        final Object primaryKey = allRows.getObject(adapter.getPrimaryKeyColumn(table).getName());

                        LOGGER.debug("calculate hash value from folling row data: <{}>", rowData);
                        final String hash = new HashCalculator().getHash(rowData);

                        adapter.getRowForPrimaryKey(primaryKey, mdTable, new DatabaseAdapterCallback<ResultSet>() {
                            @Override
                            public void onSuccess(ResultSet result) throws DatabaseAdapterException {

                                try {
                                    if (result.next()) {
                                        // compare hashes
                                        String mdTableHash = result.getString(MDV_COLUMN_NAME);
                                        if (mdTableHash == null || !mdTableHash.equalsIgnoreCase(hash)) {
                                            LOGGER.info(Infos.COMMON_UPDATING_SERVER_HASH_ENTRY);
                                            adapter.updateMdRow(rev, SERVER_FLAG, primaryKey, hash, table);
                                        }
                                    } else {
                                        // create new entry
                                        LOGGER.info(Infos.COMMON_CREATING_NEW_SERVER_HASH_ENTRY);
                                        adapter.insertMdRow(rev, SERVER_FLAG, primaryKey, hash, table);
                                    }
                                } catch (SQLException e) {
                                    throw new DatabaseAdapterException(e);
                                }
                            }
                        });

                    }
                } catch (SQLException e) {
                    throw new DatabaseAdapterException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new DatabaseAdapterException(e);
                }
            }
        });
    }

    private void searchAndProcessDeletedRows(final int rev, final String table) throws DatabaseAdapterException {

        LOGGER.debug("searching for deleted rows");

        // Update deleted rows
        adapter.getDeletedRowsForTable(table, new DatabaseAdapterCallback<ResultSet>() {
            @Override
            public void onSuccess(ResultSet deletedRows) throws DatabaseAdapterException {
                try {
                    while (deletedRows.next()) {
                        if (StringUtil.isNullOrEmpty(deletedRows.getString(MDV_COLUMN_NAME))) {
                            // this row has already been deleted
                            continue;
                        }
                        LOGGER.info(Infos.COMMON_FOUND_DELETED_ROW_ON_SERVER);
                        adapter.updateMdRow(rev, SERVER_FLAG, deletedRows.getObject("pk"), "", table);
                    }
                } catch (SQLException e) {
                    throw new DatabaseAdapterException(e);
                }
            }
        });
    }
}
