package de.consistec.doubleganger.common.client;

/*
 * #%L
 * Project - doppelganger
 * File - ClientHashProcessor.java
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
import static de.consistec.doubleganger.common.MdTableDefaultValues.FLAG_PROCESSED;
import static de.consistec.doubleganger.common.MdTableDefaultValues.MDV_DELETED_VALUE;
import static de.consistec.doubleganger.common.i18n.MessageReader.read;

import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.IConflictListener;
import de.consistec.doubleganger.common.SyncData;
import de.consistec.doubleganger.common.SyncDataHolder;
import de.consistec.doubleganger.common.SyncDirection;
import de.consistec.doubleganger.common.TableSyncStrategies;
import de.consistec.doubleganger.common.TableSyncStrategy;
import de.consistec.doubleganger.common.adapter.DatabaseAdapterCallback;
import de.consistec.doubleganger.common.adapter.IDatabaseAdapter;
import de.consistec.doubleganger.common.conflict.ConflictStrategy;
import de.consistec.doubleganger.common.conflict.ConflictStrategyFactory;
import de.consistec.doubleganger.common.conflict.IConflictStrategy;
import de.consistec.doubleganger.common.conflict.UserDecision;
import de.consistec.doubleganger.common.data.Change;
import de.consistec.doubleganger.common.data.MDEntry;
import de.consistec.doubleganger.common.data.ResolvedChange;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.doubleganger.common.i18n.Errors;
import de.consistec.doubleganger.common.i18n.Warnings;
import de.consistec.doubleganger.common.util.HashCalculator;
import de.consistec.doubleganger.common.util.LoggingUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
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
 * possible conflict types which are defined in {@link de.consistec.doubleganger.common.conflict.ConflictType}
 * <br/>
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public class ClientHashProcessor {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(ClientHashProcessor.class.getCanonicalName());
    private static final Config CONF = Config.getInstance();
    private IConflictListener conflictListener;
    private IDatabaseAdapter adapter;
    private TableSyncStrategies strategies;

    /**
     * Instantiates a new client hash processor.
     *
     * @param adapter Database adapter object.
     * @param conflictListener ConflictListener for resolving conflicts when
     * {@link de.consistec.doubleganger.common.conflict.ConflictStrategy ConflictStrategy} is FIRE_EVENT.
     * @param strategies Special synchronization strategies for tables.
     */
    public ClientHashProcessor(IDatabaseAdapter adapter, TableSyncStrategies strategies,
        IConflictListener conflictListener) {
        this.adapter = adapter;
        this.strategies = strategies;
        this.conflictListener = conflictListener;
        LOGGER.debug("HashProcessor Constructor finished");
    }

    /**
     * Resolves the conflicts between the client changes and the server changes.
     *
     * @param clientData detected client changes to free from conflicts
     * @param serverData detected server changes to free from conflicts
     * @return a sync data holder container which contains cleaned client and server changes.
     * @throws SyncException
     */
    public SyncDataHolder resolveConflicts(SyncData clientData, SyncData serverData) throws SyncException {
        LOGGER.debug("applyChangesFromServerOnClient called");

        clientData.sortChanges();
        serverData.sortChanges();

        SyncData copiedClientSyncData = new SyncData(clientData);
        SyncData copiedServerSyncData = new SyncData(serverData);

        SyncDataHolder dataHolder = new SyncDataHolder(copiedClientSyncData, copiedServerSyncData);
        try {
            for (final Change remoteChange : serverData.getChanges()) {

                final MDEntry remoteEntry = remoteChange.getMdEntry();
                LOGGER.debug("processing: {}", remoteEntry.toString());

                Change clientChange = clientData.getConflictingChange(remoteChange);
                if (clientChange != null) {
                    resolveConflict(remoteChange, clientChange, dataHolder);
                }
            }
        } catch (DatabaseAdapterException e) {
            throw new SyncException(e);
        } catch (SQLException e) {
            throw new SyncException(e);
        }
        LOGGER.debug("applyChangesFromServerOnClient finished");

        return dataHolder;
    }

    /**
     * Apply changes from server on client.
     *
     * @param serverChanges the server changes
     * @throws SyncException the sync exception
     */
    public void applyChangesFromServerOnClient(List<Change> serverChanges) throws SyncException,
        DatabaseAdapterException {

        LOGGER.debug("applyChangesFromServerOnClient called");

        HashCalculator hashCalculator = adapter.getHashCalculator();

        for (final Change remoteChange : serverChanges) {

            final MDEntry remoteEntry = remoteChange.getMdEntry();
            LOGGER.debug("processing: {}", remoteEntry.toString());


            final String hash = hashCalculator.calculateHash(remoteChange, CONF.isSqlTriggerOnClientActivated());
            applyServerChange(remoteChange, hash);
        }
        LOGGER.debug("applyChangesFromServerOnClient finished");
    }

    private void applyServerChange(final Change serverChange, final String hash) throws DatabaseAdapterException {

        final MDEntry remoteEntry = serverChange.getMdEntry();
        final Object pKey = remoteEntry.getPrimaryKey();
        final String tableName = remoteEntry.getTableName();
        final int rev = remoteEntry.getRevision();
        final Map<String, Object> remoteRowData = serverChange.getRowData();

//        // this basically boils down to:
//        if (remoteEntry.dataRowExists()) {
//            // SERVER ADD or MOD
//            adapter.insertOrUpdateDataRow(remoteRowData, pKey, tableName);
//        } else {
//            adapter.deleteRowOrDoNothing(pKey, tableName);
//        }
//        adapter.insertOrUpdateMdRow(rev, FLAG_PROCESSED, pKey, hash, tableName);

        // SERVER ADD, MOD OR DEL
        if (remoteEntry.dataRowExists()) {
            adapter.getRowForPrimaryKey(pKey, tableName, new DatabaseAdapterCallback<ResultSet>() {
                @Override
                public void onSuccess(final ResultSet result) throws DatabaseAdapterException, SQLException {
                    if (result.next()) {
                        // SERVER MOD
                        adapter.updateDataRow(remoteRowData, pKey, tableName);
                        adapter.updateMdRow(rev, FLAG_PROCESSED, pKey, hash, tableName);
                    } else {
                        // SERVER ADD
                        // on initialization everything is a server add, but deleted items no longer need to be added
                        adapter.insertDataRow(remoteRowData, tableName);

                        String mdTableName = tableName + CONF.getMdTableSuffix();
                        adapter.getRowForPrimaryKey(pKey, mdTableName, new DatabaseAdapterCallback<ResultSet>() {
                            @Override
                            public void onSuccess(final ResultSet result) throws DatabaseAdapterException,
                                SQLException {
                                if (result.next()) {
                                    adapter.updateMdRow(rev, FLAG_PROCESSED, pKey, hash, tableName);
                                } else {
                                    adapter.insertMdRow(rev, FLAG_PROCESSED, pKey, hash, tableName);
                                }
                            }
                        });
                    }
                }
            });
        } else {
            // SERVER DEL
            adapter.getRowForPrimaryKey(pKey, tableName, new DatabaseAdapterCallback<ResultSet>() {
                @Override
                public void onSuccess(final ResultSet result) throws DatabaseAdapterException, SQLException {
                    if (result.next()) {
                        adapter.deleteRow(pKey, tableName);
                        adapter.updateMdRow(rev, FLAG_PROCESSED, pKey, MDV_DELETED_VALUE, tableName);
                    } else {
                        String mdTableName = tableName + CONF.getMdTableSuffix();
                        adapter.getRowForPrimaryKey(pKey, mdTableName, new DatabaseAdapterCallback<ResultSet>() {
                            @Override
                            public void onSuccess(final ResultSet result) throws DatabaseAdapterException,
                                SQLException {
                                if (result.next()) {
                                    adapter.updateMdRow(rev, FLAG_PROCESSED, pKey, MDV_DELETED_VALUE, tableName);
                                } else {
                                    adapter.insertMdRow(rev, FLAG_PROCESSED, pKey, MDV_DELETED_VALUE, tableName);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void resolveConflict(final Change serverChange, final Change clientChange, SyncDataHolder dataHolder)
        throws DatabaseAdapterException, SQLException, SyncException {

        MDEntry remoteEntry = serverChange.getMdEntry();
        TableSyncStrategy tableSyncStrategy = strategies.getSyncStrategyForTable(remoteEntry.getTableName());

        ConflictStrategy conflictStrategy = tableSyncStrategy.getConflictStrategy();
        SyncDirection syncDirection = tableSyncStrategy.getDirection();
        LOGGER.info("Conflict Action: {}. Sync Direction: {}", conflictStrategy.name(), syncDirection.name());

        IConflictStrategy conflictHandlingStrategy = ConflictStrategyFactory.newInstance(syncDirection);

        ConflictHandlingData data = new ConflictHandlingData(clientChange, serverChange);

        switch (conflictStrategy) {
            case CLIENT_WINS:
                conflictHandlingStrategy.resolveByClientWinsStrategy(adapter, data);
                dataHolder.getServerSyncData().removeChange(serverChange);
                break;
            case SERVER_WINS:
                conflictHandlingStrategy.resolveByServerWinsStrategy(adapter, data);
                // remove client change from change list if syncdirection is server_to_client
                dataHolder.getClientSyncData().removeChange(clientChange);
                dataHolder.getServerSyncData().removeChange(serverChange);
                break;
            case FIRE_EVENT:
                ResolvedChange resolvedChange = resolveConflictsFireEvent(data, conflictHandlingStrategy);
                // exchange client change with resolved change to apply to server db
                dataHolder.getClientSyncData().removeChange(clientChange);

                // only add to client sync data if the user selected to use user change
                // if the user selected to use server change than we don't need to apply to server
                if (resolvedChange.getDecision() != UserDecision.SERVER_CHANGE) {
                    resolvedChange.setMdEntry(clientChange.getMdEntry());
                    dataHolder.getClientSyncData().addChange(resolvedChange);
                }

                // delete server change it is not needed anymore
                dataHolder.getServerSyncData().removeChange(serverChange);
                break;
            default:
                throw new IllegalStateException(String.format("Unknown conflict strategy %s", conflictStrategy.name()));
        }
    }

    /**
     * Update client revision.
     *
     * @param clientData data which contains the server revision after sync and the client changes applied on server.
     * @throws DatabaseAdapterException When update fails.
     */
    public void updateClientRevision(SyncData clientData) throws DatabaseAdapterException {

        LOGGER.debug("Updating client revisions on hashtable");
        for (Change change : clientData.getChanges()) {
            MDEntry tmpMDEntry = change.getMdEntry();
            int revision = clientData.getRevision();
            String mdTableName = tmpMDEntry.getTableName() + CONF.getMdTableSuffix();
            int result = adapter.updateRevision(revision, mdTableName, tmpMDEntry.getPrimaryKey());
            if (result != 1) {
                LOGGER.warn(read(Warnings.COMMON_CANT_UPDATE_CLIENT_REV));
            }
        }

    }

    private ResolvedChange resolveConflictsFireEvent(ConflictHandlingData data,
        IConflictStrategy conflictHandlingStrategy) throws SQLException, SyncException, DatabaseAdapterException {

        if (null == conflictListener) {
            throw new SyncException(read(Errors.COMMON_NO_CONFLICT_LISTENER_FOUND));
        } else {
            return conflictHandlingStrategy.resolveByFireEvent(adapter, data, data.getLocalData(), conflictListener);
        }
    }
}
