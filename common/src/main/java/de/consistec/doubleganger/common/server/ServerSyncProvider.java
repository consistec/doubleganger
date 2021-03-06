package de.consistec.doubleganger.common.server;

/*
 * #%L
 * Project - doubleganger
 * File - ServerSyncProvider.java
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
import static de.consistec.doubleganger.common.i18n.MessageReader.read;
import static de.consistec.doubleganger.common.util.CollectionsUtil.newHashMap;
import static de.consistec.doubleganger.common.util.CollectionsUtil.newHashSet;
import static de.consistec.doubleganger.common.util.Preconditions.checkSyncDirectionOfClientChanges;
import static de.consistec.doubleganger.common.util.Preconditions.checkSyncState;

import de.consistec.doubleganger.common.AbstractSyncProvider;
import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.SyncData;
import de.consistec.doubleganger.common.SyncSettings;
import de.consistec.doubleganger.common.TableSyncStrategies;
import de.consistec.doubleganger.common.TableSyncStrategy;
import de.consistec.doubleganger.common.adapter.DatabaseAdapterFactory;
import de.consistec.doubleganger.common.adapter.IDatabaseAdapter;
import de.consistec.doubleganger.common.data.Change;
import de.consistec.doubleganger.common.data.schema.Schema;
import de.consistec.doubleganger.common.exception.ServerStatusException;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.doubleganger.common.exception.database_adapter.TransactionAbortedException;
import de.consistec.doubleganger.common.exception.database_adapter.UniqueConstraintException;
import de.consistec.doubleganger.common.i18n.Errors;
import de.consistec.doubleganger.common.i18n.Infos;
import de.consistec.doubleganger.common.i18n.MessageReader;
import de.consistec.doubleganger.common.i18n.Warnings;
import de.consistec.doubleganger.common.util.LoggingUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.slf4j.cal10n.LocLogger;

/**
 * Implementation of server side synchronization provider.
 * <p>
 * Do not use directly! Instead, use {@link de.consistec.doubleganger.common.SyncContext.server()} factory methods.
 * </p><p>
 * This class provides all logic necessary to carry out synchronization process on the server site.
 * Together with database adapter, instance of server provider apply changes received from client to database,
 * and sends changes from this database to client provider.
 * </p><p>
 * If the client is located on a remote machine, one have to provide an implementation of
 * {@link de.consistec.doubleganger.common.server.IServerSyncProvider IServerSyncProvider} interface
 * (called a "server proxy") for the client context, which will be responsible for serializing and transmitting the data
 * through network to server provider. <br/> Then one have to write a code (name it Protocol Processor)
 * which will be able to receive and deserialize those data on server site, and pass it through to the methods
 * of server provider instance.
 * <p/>
 * <p>
 * An example of Http Proxy and Http Protocol Processor can be found in server_sync_providers_proxies jar package of
 * this project.
 * </p>
 * <p><b>Remember!</b> This is <b>not</b> a proxy!</p>
 * <p>Before creating any instance, the {@link }</p>
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public final class ServerSyncProvider extends AbstractSyncProvider implements IServerSyncProvider { //NOSONAR

    private static final LocLogger LOGGER = LoggingUtil.createLogger(ServerSyncProvider.class.getCanonicalName());
    private static final Config CONF = Config.getInstance();
    private static final int NUMBER_OF_SYNC_RETRIES = 3;

    /**
     * Creates provider with its own database connection.
     *
     * @param strategies Special synchronization strategies for tables.
     * @throws DatabaseAdapterException
     */
    public ServerSyncProvider(TableSyncStrategies strategies) throws DatabaseAdapterException {
        super(strategies);
    }

    /**
     * Creates provider which will be using given data source for provide database adapters instances with
     * {@link java.sql.Connection connections}.
     *
     * @param strategies Special synchronization strategies for tables.
     * @param ds External data source.
     * @throws DatabaseAdapterException
     */
    public ServerSyncProvider(TableSyncStrategies strategies, DataSource ds) throws DatabaseAdapterException {
        super(strategies, ds);
    }

    /**
     * Creates provider which will be using the passed database adapter.
     *
     * @param strategies Special synchronization strategies for tables. given
     * @param dbAdapter external db adapter
     * @throws DatabaseAdapterException
     */
    public ServerSyncProvider(TableSyncStrategies strategies, IDatabaseAdapter dbAdapter) throws
        DatabaseAdapterException {
        super(strategies, dbAdapter);
    }

    /**
     * Creates provider which will be using given data source for provide database adapters instances with
     * {@link java.sql.Connection connections}.
     *
     * @param strategies Special synchronization strategies for tables.
     * @param ds External data source.
     * @param dbAdapter external db adapter
     * @throws DatabaseAdapterException
     */
    public ServerSyncProvider(TableSyncStrategies strategies, DataSource ds, IDatabaseAdapter dbAdapter) throws
        DatabaseAdapterException {
        super(strategies, ds, dbAdapter);
    }

    @Override
    public void validate(final SyncSettings clientSettings) throws SyncException {

        SyncSettings serverSettings = new SyncSettings(CONF.getSyncTables(), getStrategies());

        checkSyncState(serverSettings.getSyncTables().containsAll(clientSettings.getSyncTables()),
            Errors.COMMON_SYNCTABLE_SETTINGS_ERROR);

        try {
            for (String clientTable : clientSettings.getSyncTables()) {
                TableSyncStrategy clientSyncStrategy = clientSettings.getStrategy(clientTable);
                TableSyncStrategy serverSyncStrategy = serverSettings.getStrategy(clientTable);
                if (!clientSyncStrategy.equals(serverSyncStrategy)) {
                    throw new SyncException(
                        MessageReader.read(Errors.COMMON_NOT_IDENTICAL_SYNCSTRATEGY, clientSyncStrategy,
                        serverSyncStrategy));
                }

                createMDTableIfNotExists(clientTable);
            }
        } catch (DatabaseAdapterException e) {
            throw new SyncException(e);
        }
    }

    private IDatabaseAdapter prepareServerDbAdapter() throws DatabaseAdapterInstantiationException {
        return prepareDbAdapter(DatabaseAdapterFactory.AdapterPurpose.SERVER, false);
    }

    private void createMDTableIfNotExists(String clientTable) throws SyncException, DatabaseAdapterException {

        IDatabaseAdapter adapter = null;

        try {
            adapter = prepareServerDbAdapter();

            // prepareDbAdapter sets autocommit to false but here we need it set to true
            adapter.getConnection().setAutoCommit(true);

            String mdTable = clientTable + CONF.getMdTableSuffix();

            // in case of multiple clients trying to synchronize, we make sure
            // the schema is not created multiple times
            int retries = NUMBER_OF_SYNC_RETRIES;

            while (!adapter.existsMDTable(clientTable)) {
                try {
                    LOGGER.warn(Warnings.COMMON_RECREATING_SERVER_META_TABLES, mdTable);
                    adapter.createMDTableOnServer(clientTable);
                } catch (DatabaseAdapterException ex) {

                    if (ex instanceof UniqueConstraintException
                        || ex instanceof TransactionAbortedException) {

                        LOGGER.warn(Warnings.COMMON_RECREATING_SERVER_META_TABLES_FAILED, mdTable, ex.getMessage());

                        if (retries == 0) {
                            throw new SyncException(
                                read(Errors.COMMON_CANT_RECREATE_SERVER_META_TABLE_FOR_N_TIMES, mdTable, retries), ex);
                        } else {
                            retries--;
                            LOGGER.info(Infos.COMMON_RETRYING_RECREATE_SERVER_META_TABLES, mdTable, retries);
                        }
                    } else {
                        throw new SyncException(read(Errors.COMMON_APPLY_CHANGES_FAILED), ex);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseAdapterException(e);
        } finally {
            closeConnection(adapter);
        }
    }

    @Override
    public int applyChanges(SyncData clientData) throws SyncException {

        checkSyncDirectionOfClientChanges(clientData.getChanges(), getStrategies());

        int retries = CONF.getRetryNumberOfApplyChangesOnTransactionError();
        IDatabaseAdapter adapter = null;

        try {
            adapter = prepareServerDbAdapter();
            ServerTableSynchronizer tableSynchronizer = new ServerTableSynchronizer(adapter);
            ServerHashProcessor hashProcessor = new ServerHashProcessor(adapter);
            validateChangeList(clientData.getChanges());
            tableSynchronizer.synchronizeServerTables();

            int result = hashProcessor.applyChangesFromClientOnServer(clientData.getChanges(),
                clientData.getRevision());
            adapter.commit();
            LOGGER.info(Infos.COMMON_SENDING_NEW_REVISION_TO_CLIENT, result);

            return result;

        } catch (DatabaseAdapterException ex) {

            rollback(adapter);
            LOGGER.debug("Transaction rolled back!!! \n {}", ex);

            if (ex instanceof UniqueConstraintException) {
                throw new ServerStatusException(ServerStatus.ENTRY_NOT_UNIQUE, ex);
            } else if (ex instanceof TransactionAbortedException) {
                LOGGER.info(Infos.COMMON_TRYING_TO_REAPPLY_CLIENT_CHANGES);

                if (retries == 0) {
                    throw new SyncException(read(Errors.COMMON_CANT_APPLY_CLIENT_CHANGES_FOR_N_TIME, retries), ex);
                }

                int result = 0;

                if (retries > 0) {
                    retries--;
                    LOGGER.info(Infos.COMMON_REMAINING_NUMBER_OF_APPLY_CLIENT_CHANGES_RETRIES, retries);
                    result = applyChanges(clientData);
                }

                return result;
            } else {
                throw new SyncException(read(Errors.COMMON_APPLY_CHANGES_FAILED), ex);
            }
        } catch (ServerStatusException ex) {
            rollback(adapter);
            throw ex;
        } catch (Throwable ex) { //NOSONAR
            /**
             * no matter what happened, we have to rollback
             */
            rollback(adapter);
            LOGGER.debug("Transaction rolled back!!! \n {}", ex);
            throw new SyncException(read(Errors.COMMON_APPLY_CHANGES_FAILED), ex);
        } finally {
            closeConnection(adapter);
        }
    }

    /**
     * Validate list of table and column names against positive list to prevent SQL injections via manipulated
     * table or column names.
     */
    private void validateChangeList(List<Change> changes) throws SyncException {

        Map<String, Set<String>> tableColumnMappingPositive = newHashMap(CONF.getSyncTables().size());
        String tableName;
        IDatabaseAdapter adapter = null;

        try {

            adapter = prepareServerDbAdapter();

            for (Change change : changes) {

                tableName = change.getMdEntry().getTableName();

                if (!CONF.getSyncTables().contains(tableName)) {
                    throw new SyncException(read(Errors.COMMON_TABLE_NOT_INTEND_FOR_SYNCHRONIZING, tableName));
                }
                try {
                    if (!tableColumnMappingPositive.containsKey(tableName)) {
                        tableColumnMappingPositive.put(tableName,
                            newHashSet(adapter.getColumnNamesFromTable(tableName)));
                    }
                } catch (DatabaseAdapterException e) {
                    throw new SyncException(read(Errors.DATA_CANT_LOAD_COLUMNS_FOR_TABLE, tableName), e);
                }
                if (!tableColumnMappingPositive.get(tableName).containsAll(change.getRowData().keySet())) {
                    throw new SyncException(read(Errors.COMMON_CLIENT_COLUMNS_AND_SERVER_COLUMN_FOR_TABLE_DONT_MATCH,
                        tableName));
                }
            }

        } catch (DatabaseAdapterException ex) {
            throw new SyncException(ex);
        } finally {
            closeConnection(adapter);
        }
    }

    @Override
    public SyncData getChanges(int rev) throws SyncException {

        int retries = CONF.getRetryNumberOfGetChangesOnTransactionError();
        IDatabaseAdapter adapter = null;

        try {
            adapter = prepareServerDbAdapter();
            ServerChangesEnumerator changesEnumerator = new ServerChangesEnumerator(adapter, getStrategies());
            ServerTableSynchronizer tableSynchronizer = new ServerTableSynchronizer(adapter);
            tableSynchronizer.synchronizeServerTables();
            return changesEnumerator.getChanges(rev);

        } catch (DatabaseAdapterException e) {

            rollback(adapter);
            LOGGER.debug("Transaction rolled back!!! \n {}", e.getLocalizedMessage());


            if (e instanceof TransactionAbortedException) {
                LOGGER.info(Infos.COMMON_TRYING_TO_GET_SERVER_CHANGES_FOR_N_TIME, retries);

                if (retries == 0) {
                    throw new SyncException(read(Errors.COMMON_CANT_GET_SERVER_CHANGES_FOR_N_TIME, retries), e);
                }

                SyncData resultChanges = null;

                if (retries > 0) {
                    retries--;
                    LOGGER.info(Infos.COMMON_REMAINING_NUMBER_OF_GET_SERVER_CHANGES_RETRIES, retries);
                    resultChanges = getChanges(rev);
                }

                return resultChanges;
            } else {
                throw new SyncException(read(Errors.DATA_GENERIC_ERROR), e);
            }
        } finally {
            closeConnection(adapter);
        }
    }

    @Override
    public Schema getSchema() throws SyncException {
        IDatabaseAdapter adapter = null;
        try {
            adapter = prepareServerDbAdapter();
            return adapter.getSchema();
        } catch (DatabaseAdapterException e) {
            throw new SyncException(e);
        } finally {
            closeConnection(adapter);
        }
    }
}
