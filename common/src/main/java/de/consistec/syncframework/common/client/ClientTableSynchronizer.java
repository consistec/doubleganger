package de.consistec.syncframework.common.client;

import static de.consistec.syncframework.common.MdTableDefaultValues.CLIENT_INIT_REVISION;
import static de.consistec.syncframework.common.MdTableDefaultValues.FLAG_MODIFIED;
import static de.consistec.syncframework.common.util.CollectionsUtil.newArrayList;
import static de.consistec.syncframework.common.util.CollectionsUtil.newHashMap;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.adapter.DatabaseAdapterCallback;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.MDEntry;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.i18n.Infos;
import de.consistec.syncframework.common.util.DBMapperUtil;
import de.consistec.syncframework.common.util.HashCalculator;
import de.consistec.syncframework.common.util.LoggingUtil;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.cal10n.LocLogger;

/**
 * The {@code ClientTableSynchronizer} class looks for changed / deleted data rows for each
 * sync table defined in the configuration file.
 * <br/>
 * <p>If changed rows are detected then the {@code ClientTableSynchronizer } calculates
 * a new hash entry for each of them and updates it in the md-table. For new rows the
 * {@code ClientTableSynchronizer } creates a new md table entry. If deleted rows are
 * detected and an md table entry exists then the {@code ClientTableSynchronizer }
 * updates the md table entry with an empty hash value and marks it so as deleted.</p>
 *
 * @author Markus
 * @company Consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public class ClientTableSynchronizer {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(ClientTableSynchronizer.class.getCanonicalName());
    private static final Config CONF = Config.getInstance();
    private IDatabaseAdapter adapter;

    /**
     * Instantiates a new client table synchronizer.
     *
     * @param adapter Database adapter
     */
    public ClientTableSynchronizer(IDatabaseAdapter adapter) {
        this.adapter = adapter;
        LOGGER.debug("ClientTableSynchronizer Constructor finished");
    }

    /**
     * Synchronize client tables.
     *
     * @return List<Change> the client changes.
     * @throws DatabaseAdapterException
     * @throws SQLException
     */
    public List<Change> synchronizeClientTables() throws DatabaseAdapterException {

        LOGGER.debug("synchronizeClientTables called");

        final List<Change> changeList = newArrayList();

        LOGGER.debug("Searching for modifications and updating metadata accordingly.");

        for (final String table : CONF.getSyncTables()) {
            LOGGER.debug("Processing table {}", table);
            changeList.addAll(searchAndProcessChangedRows(table));
            changeList.addAll(searchAndProcessDeletedRows(table));
        }

        LOGGER.debug("synchronizeClientTables finished");

        return changeList;
    }

    private List<Change> searchAndProcessChangedRows(final String table) throws DatabaseAdapterException {

        final List<Change> changeList = newArrayList();
        final List<String> columns = adapter.getColumnNamesFromTable(table);
        Collections.sort(columns);

        adapter.getAllRowsFromTable(table, new DatabaseAdapterCallback<ResultSet>() {
            @Override
            public void onSuccess(ResultSet allRows) throws DatabaseAdapterException {

                try {
                    String mdTable = table + CONF.getMdTableSuffix();

                    while (allRows.next()) {

                        final Map<String, Object> rowData = newHashMap();

                        for (String s : columns) {
                            rowData.put(s, allRows.getObject(s));
                        }

                        final Object primaryKey = allRows.getObject(adapter.getPrimaryKeyColumn(table).getName());
                        final String hash = new HashCalculator().getHash(rowData);

                        adapter.getRowForPrimaryKey(primaryKey, mdTable, new DatabaseAdapterCallback<ResultSet>() {
                            @Override
                            public void onSuccess(ResultSet result) throws DatabaseAdapterException {
                                Change change = new Change();
                                try {
                                    if (result.next()) {
                                        if (!DBMapperUtil.rowHasSameHash(result, hash)) {
                                            LOGGER.info(Infos.COMMON_UPDATING_CLIENT_HASH_ENTRY);
                                            adapter.updateMdRow(result.getInt("rev"), FLAG_MODIFIED, primaryKey, hash,
                                                table);
                                            MDEntry mdEntry = DBMapperUtil.getMetadata(result, table);
                                            mdEntry.setDataRowExists(DBMapperUtil.dataRowExists(rowData));
                                            change.setMdEntry(mdEntry);
                                            change.setRowData(rowData);
                                            changeList.add(change);
                                        } else if (result.getInt("f") == FLAG_MODIFIED) {
                                            // if synchronization is repeated then previous changes or inserts are
                                            // marked with th CLIENT_FLAG
                                            MDEntry mdEntry = DBMapperUtil.getMetadata(result, table);
                                            change.setMdEntry(mdEntry);
                                            change.setRowData(rowData);
                                            changeList.add(change);
                                        }
                                    } else {
                                        // create new entry
                                        LOGGER.info(Infos.COMMON_CREATING_NEW_CLIENT_HASH_ENTRY);
                                        adapter.insertMdRow(CLIENT_INIT_REVISION, FLAG_MODIFIED, primaryKey, hash,
                                            table);

                                        MDEntry mdEntry = new MDEntry(primaryKey, true, CLIENT_INIT_REVISION, table,
                                            hash);
                                        change.setMdEntry(mdEntry);
                                        change.setRowData(rowData);
                                        changeList.add(change);
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

        return changeList;
    }

    private List<Change> searchAndProcessDeletedRows(final String table) throws DatabaseAdapterException {

        LOGGER.debug("searching for deleted rows");

        final List<Change> changeList = newArrayList();
        // Update deleted rows
        // http://www.cryer.co.uk/brian/sql/sql_crib_sheet.htm
        adapter.getDeletedRowsForTable(table, new DatabaseAdapterCallback<ResultSet>() {
            @Override
            public void onSuccess(ResultSet deletedRows) throws DatabaseAdapterException {
                try {
                    while (deletedRows.next()) {
                        Change change = new Change();
                        if (DBMapperUtil.rowIsAlreadyDeleted(deletedRows)) {
                            if (deletedRows.getInt("f") == FLAG_MODIFIED) {
                                // if synchronization is repeated then previous changes or inserts are
                                // marked with th CLIENT_FLAG
                                MDEntry mdEntry = DBMapperUtil.getMetadata(deletedRows, table);
                                change.setMdEntry(mdEntry);
                                Map<String, Object> rowData = newHashMap();
                                change.setRowData(rowData);
                                changeList.add(change);
                            } else {
                                continue;
                            }
                        }

                        LOGGER.info(Infos.COMMON_FOUND_DELETED_ROW_ON_CLIENT);
                        adapter.updateMdRow(deletedRows.getInt("rev"), FLAG_MODIFIED, deletedRows.getObject("pk"), "",
                            table);

                        MDEntry mdEntry = DBMapperUtil.getMetadata(deletedRows, table);
                        mdEntry.setDataRowDeleted();
                        change.setMdEntry(mdEntry);
                        changeList.add(change);
                    }
                } catch (SQLException e) {
                    throw new DatabaseAdapterException(e);
                }
            }
        });
        return changeList;
    }
}
