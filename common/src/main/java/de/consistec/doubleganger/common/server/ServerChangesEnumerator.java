package de.consistec.doubleganger.common.server;

/*
 * #%L
 * Project - doppelganger
 * File - ServerChangesEnumerator.java
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
import static de.consistec.doubleganger.common.util.CollectionsUtil.newArrayList;

import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.SyncData;
import de.consistec.doubleganger.common.SyncDirection;
import de.consistec.doubleganger.common.TableSyncStrategies;
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
 * @company consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public class ServerChangesEnumerator {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(ServerChangesEnumerator.class.getCanonicalName());
    private static final transient Config CONF = Config.getInstance();
    private IDatabaseAdapter adapter = null;
    private TableSyncStrategies tableSyncStrategies;

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

    /**
     * Creates the list of {@code Change} objects for all inserted, modified or deleted data rows
     * which revision is greater than {@code rev}.
     *
     * @param rev the revision
     * @return the changes
     * @throws DatabaseAdapterException the adapter exception
     */
    public SyncData getChanges(int rev) throws DatabaseAdapterException {

        LOGGER.debug("getServerChanges called");

        final List<Change> list = newArrayList();
        int revision = adapter.getLastRevision();
        final SyncData serverChangeSet = new SyncData(revision, list);

        for (final String syncTable : CONF.getSyncTables()) {

            adapter.getChangesForRevision(rev, syncTable, new DatabaseAdapterCallback<ResultSet>() {
                @Override
                public void onSuccess(ResultSet resultSet) throws DatabaseAdapterException {
                    try {
                        while (resultSet.next()) {

                            Change tmpChange = new Change();

                            Map<String, Object> rowData = DBMapperUtil.getRowData(resultSet);
                            tmpChange.setRowData(rowData);

                            MDEntry mdEntry = DBMapperUtil.getMetadata(resultSet, syncTable);
                            mdEntry.setDataRowExists(DBMapperUtil.dataRowExists(rowData));
                            tmpChange.setMdEntry(mdEntry);

                            SyncDirection syncDirection = tableSyncStrategies.getSyncStrategyForTable(
                                syncTable).getDirection();
                            if (syncDirection != SyncDirection.CLIENT_TO_SERVER) {
                                serverChangeSet.getChanges().add(tmpChange);
                            }

                            int revision = tmpChange.getMdEntry().getRevision();
                            if (serverChangeSet.getRevision() < revision) {
                                serverChangeSet.setRevision(revision);
                            }
                            LOGGER.info(Infos.COMMON_ADDED_SERVER_CHANGE_TO_CHANGE_SET, tmpChange.toString());
                        }
                    } catch (SQLException e) {
                        throw new DatabaseAdapterException(e);
                    }
                }
            });
        }
        LOGGER.debug("getServerChanges finished");
        return serverChangeSet;
    }
}
