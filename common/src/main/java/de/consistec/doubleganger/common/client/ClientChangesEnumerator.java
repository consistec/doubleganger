package de.consistec.doubleganger.common.client;

/*
 * #%L
 * Project - doppelganger
 * File - ClientChangesEnumerator.java
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

import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.SyncData;
import de.consistec.doubleganger.common.SyncDirection;
import de.consistec.doubleganger.common.TableSyncStrategies;
import de.consistec.doubleganger.common.TableSyncStrategy;
import de.consistec.doubleganger.common.adapter.DatabaseAdapterCallback;
import de.consistec.doubleganger.common.adapter.IDatabaseAdapter;
import de.consistec.doubleganger.common.data.Change;
import de.consistec.doubleganger.common.data.MDEntry;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.doubleganger.common.i18n.Infos;
import de.consistec.doubleganger.common.util.DBMapperUtil;
import de.consistec.doubleganger.common.util.LoggingUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.slf4j.cal10n.LocLogger;

/**
 * The {@code ClientChangesEnumerator} is responsible for creating a list
 * of {@link Change} objects which represents all changes on client tables to sync.
 * <br/>
 * For all inserted, modified or deleted rows, which are marked in the database
 * client md-table with flag = 1, the {@link ClientChangeEnumerator} creates a {@link Change}
 * object. This object consists of meta data values {@link MDEntry} and
 * row data values. The row data values are represented as Map<String, Object>, where the columnname
 * the key and Object is the values in the data row are.
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public class ClientChangesEnumerator {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(ClientChangesEnumerator.class.getCanonicalName());
    private static final transient Config CONF = Config.getInstance();
    private IDatabaseAdapter adapter;
    private TableSyncStrategies tableSyncStrategies;

    /**
     * Instantiates a new client changes enumerator.
     *
     * @param adapter The database adapter
     * @param tableSyncStrategies The configured sync strategies for tables
     */
    public ClientChangesEnumerator(IDatabaseAdapter adapter, TableSyncStrategies tableSyncStrategies) {
        this.adapter = adapter;
        this.tableSyncStrategies = tableSyncStrategies;
        LOGGER.debug("ClientChangesEnumerator Constructor finished");
    }

    /**
     * Creates a list of {@link Change}s, a {@link SyncData}, for all inserted, modified or deleted
     * data rows in the client tables to sync.
     *
     * @return The changes
     * @throws DatabaseAdapterException if database operations fail
     */
    public SyncData getChanges() throws DatabaseAdapterException {

        LOGGER.debug("getClientChanges called");

        final SyncData allChanges = new SyncData();

        for (final String tableName : CONF.getSyncTables()) {

            LOGGER.debug("processing table {}", tableName);

            adapter.getChanges(tableName, new DatabaseAdapterCallback<ResultSet>() {
                @Override
                public void onSuccess(ResultSet resultSet) throws DatabaseAdapterException {
                    try {
                        TableSyncStrategy syncStrategy = tableSyncStrategies.getSyncStrategyForTable(tableName);
                        SyncDirection syncDirection = syncStrategy.getDirection();

                        if (syncDirection != SyncDirection.SERVER_TO_CLIENT) {

                            while (resultSet.next()) {

                                Map<String, Object> rowData = DBMapperUtil.getRowData(resultSet);
                                MDEntry mdEntry = DBMapperUtil.getMetadata(resultSet, tableName);
                                mdEntry.setDataRowExists(DBMapperUtil.dataRowHasValues(rowData));

                                Change change = new Change(mdEntry, rowData);
                                allChanges.addChange(change);

                                LOGGER.info(Infos.COMMON_ADDED_CLIENT_CHANGE_TO_CHANGE_SET, change);
                            }
                        }
                    } catch (SQLException e) {
                        throw new DatabaseAdapterException(e);
                    }
                }
            });
        }
        LOGGER.debug("getClientChanges finished");
        return allChanges;
    }
}
