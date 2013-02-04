package de.consistec.syncframework.common.server;

/*
 * #%L
 * Project - doppelganger
 * File - ServerTableSynchronizer.java
 * %%
 * Copyright (C) 2011 - 2013 consistec GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import static de.consistec.syncframework.common.MdTableDefaultValues.FLAG_PROCESSED;
import static de.consistec.syncframework.common.MdTableDefaultValues.MDV_DELETED_VALUE;
import static de.consistec.syncframework.common.MdTableDefaultValues.MDV_MODIFIED_VALUE;
import static de.consistec.syncframework.common.MdTableDefaultValues.PK_COLUMN_NAME;
import static de.consistec.syncframework.common.util.CollectionsUtil.newHashMap;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.adapter.DatabaseAdapterCallback;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.i18n.Infos;
import de.consistec.syncframework.common.util.DBMapperUtil;
import de.consistec.syncframework.common.util.HashCalculator;
import de.consistec.syncframework.common.util.LoggingUtil;

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
 * @company consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public class ServerTableSynchronizer {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(ServerTableSynchronizer.class.getCanonicalName());
    private static final transient Config CONF = Config.getInstance();
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
     * Iterates through monitored tables and updates the metadata entry for each rows.
     *
     * @throws DatabaseAdapterException the adapter exception
     */
    public void synchronizeServerTables() throws DatabaseAdapterException {
        LOGGER.debug("synchronizeServerTables called");

        final int revision = adapter.getNextRevision();

        for (final String tableName : CONF.getSyncTables()) {
            if (CONF.isSqlTriggerActivated()) {
                LOGGER.debug("Triggers are activated. Incrementing revision for changed rows in {}", tableName);
                updateRevisionOnChangedRows(revision, tableName);
            } else {
                LOGGER.debug("Triggers are deactivated. Searching for changes & updating the metadata in {}", tableName);
                searchAndProcessChangedRows(revision, tableName);
                searchAndProcessDeletedRows(revision, tableName);
            }
        }

        LOGGER.debug("synchronizeServerTables finished");
    }

    private void updateRevisionOnChangedRows(final int revision, final String tableName) throws DatabaseAdapterException {
        final String mdTableName = tableName + CONF.getMdTableSuffix();
        adapter.getChangesByFlag(tableName, new DatabaseAdapterCallback<ResultSet>() {
            @Override
            public void onSuccess(ResultSet allChangedRows) throws DatabaseAdapterException {
                try {
                    while (allChangedRows.next()) {
                        String pkName = adapter.getPrimaryKeyColumn(mdTableName).getName();
                        final Object primaryKey = allChangedRows.getObject(pkName);
                        if (primaryKey != null) {
                            adapter.updateMdRow(revision, FLAG_PROCESSED, primaryKey, MDV_MODIFIED_VALUE, tableName);
                        }
                    }
                } catch (SQLException ex) {
                    throw new DatabaseAdapterException(ex);
                }
            }
        });
    }

    private void searchAndProcessChangedRows(final int rev, final String table) throws DatabaseAdapterException {
        final String mdTable = table + CONF.getMdTableSuffix();

        final List<String> columns = adapter.getColumnNamesFromTable(table);
        adapter.getAllRowsFromTable(table, new DatabaseAdapterCallback<ResultSet>() {
            @Override
            public void onSuccess(ResultSet allRows) throws DatabaseAdapterException {
                try {
                    Map<String, Object> rowData = newHashMap();
                    while (allRows.next()) {
                        for (String s : columns) {
                            rowData.put(s, allRows.getObject(s));
                        }
                        final Object primaryKey = allRows.getObject(adapter.getPrimaryKeyColumn(table).getName());

                        LOGGER.debug("calculate hash value from following row data: <{}>", rowData);
                        final String hash = new HashCalculator().getHash(rowData);

                        adapter.getRowForPrimaryKey(primaryKey, mdTable, new DatabaseAdapterCallback<ResultSet>() {
                            @Override
                            public void onSuccess(ResultSet result) throws DatabaseAdapterException {

                                try {
                                    if (result.next()) {
                                        if (!DBMapperUtil.rowHasSameHash(result, hash)) {
                                            LOGGER.info(Infos.COMMON_UPDATING_SERVER_HASH_ENTRY);
                                            adapter.updateMdRow(rev, FLAG_PROCESSED, primaryKey, hash, table);
                                        }
                                    } else {
                                        // create new entry
                                        LOGGER.info(Infos.COMMON_CREATING_NEW_SERVER_HASH_ENTRY);
                                        adapter.insertMdRow(rev, FLAG_PROCESSED, primaryKey, hash, table);
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
                        if (DBMapperUtil.rowIsAlreadyDeleted(deletedRows)) {
                            continue;
                        }
                        LOGGER.info(Infos.COMMON_FOUND_DELETED_ROW_ON_SERVER);
                        adapter.updateMdRow(rev, FLAG_PROCESSED, deletedRows.getObject(PK_COLUMN_NAME),
                            MDV_DELETED_VALUE, table);
                    }
                } catch (SQLException e) {
                    throw new DatabaseAdapterException(e);
                }
            }
        });
    }
}
