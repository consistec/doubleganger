package de.consistec.doubleganger.common.client;

/*
 * #%L
 * Project - doppelganger
 * File - ClientSyncProvider.java
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
import static de.consistec.doubleganger.common.util.Preconditions.checkSyncDirectionOfServerChanges;

import de.consistec.doubleganger.common.AbstractSyncProvider;
import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.IConflictListener;
import de.consistec.doubleganger.common.SyncData;
import de.consistec.doubleganger.common.SyncDataHolder;
import de.consistec.doubleganger.common.TableSyncStrategies;
import de.consistec.doubleganger.common.adapter.DatabaseAdapterFactory;
import de.consistec.doubleganger.common.adapter.IDatabaseAdapter;
import de.consistec.doubleganger.common.data.schema.Schema;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.doubleganger.common.i18n.Errors;
import de.consistec.doubleganger.common.util.LoggingUtil;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.cal10n.LocLogger;

/**
 * Client synchronization provider.
 * <p>
 * Do not use directly! Instead, use {@link de.consistec.doubleganger.common.SyncContext.client())} factory methods.
 * </p>
 * <p/>
 * Thats the most important class for client site of synchronization. It provides all methods to perform
 * synchronization but the logic of synchronization process is included in
 * {@link de.consistec.doubleganger.common.client.SyncAgent#synchronize() synchronize()}
 * method of synchronization agent.<br/>
 * <b style="color: red;">Warning!</b> This class should not be used directly in client code. Instead always use
 * {@link de.consistec.doubleganger.common.SyncContext.ClientContext}.
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public final class ClientSyncProvider extends AbstractSyncProvider implements IClientSyncProvider {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(ClientSyncProvider.class.getCanonicalName());
    private IConflictListener conflictListener;
    private IDatabaseAdapter adapter = null;

    /**
     * Creates new provider instance which will be using its own database connection.
     *
     * @param strategies The configured sync strategies for tables
     * @throws DatabaseAdapterInstantiationException
     */
    public ClientSyncProvider(TableSyncStrategies strategies) throws DatabaseAdapterInstantiationException {
        super(strategies);
    }

    /**
     * Creates provider which will be using given data source for provide database adapters instances with
     * {@link java.sql.Connection connections}.
     *
     * @param strategies Special synchronization strategies for tables.
     * @param ds External data source.
     * @throws DatabaseAdapterInstantiationException
     */
    public ClientSyncProvider(TableSyncStrategies strategies, DataSource ds) throws
        DatabaseAdapterInstantiationException {
        super(strategies, ds);
    }

    /**
     * Creates provider which will be using given data source for provide database adapters instances with
     * {@link java.sql.Connection connections}.
     *
     * @param strategies Special synchronization strategies for tables.
     * @param dbAdapter the used databaseadatper
     * @throws DatabaseAdapterInstantiationException
     */
    public ClientSyncProvider(TableSyncStrategies strategies, IDatabaseAdapter dbAdapter) throws
        DatabaseAdapterInstantiationException {
        super(strategies, dbAdapter);
    }

    /**
     * Creates provider which will be using given data source for provide database adapters instances with
     * {@link java.sql.Connection connections}.
     *
     * @param strategies Special synchronization strategies for tables.
     * @param ds External data source.
     * @param dbAdapter the used databaseadatper
     * @throws DatabaseAdapterInstantiationException
     */
    public ClientSyncProvider(TableSyncStrategies strategies, DataSource ds, IDatabaseAdapter dbAdapter) throws
        DatabaseAdapterInstantiationException {
        super(strategies, ds, dbAdapter);
    }

    /**
     * Create a new adapter instance with autocommit disabled for the underlying connection.
     *
     * @return Database adapter instance.
     * @throws DatabaseAdapterInstantiationException
     */
    private IDatabaseAdapter prepareAdapterNoAutoCommit() throws DatabaseAdapterInstantiationException {
        return prepareClientAdapter(false);
    }

    /**
     * Create a new adapter instance with autocommit enabled for the underlying connection.
     *
     * @return Database adapter instance.
     * @throws DatabaseAdapterInstantiationException
     */
    private IDatabaseAdapter prepareAdapterWithAutoCommit() throws DatabaseAdapterInstantiationException {
        return prepareClientAdapter(true);
    }

    private IDatabaseAdapter prepareClientAdapter(boolean autocommit) throws DatabaseAdapterInstantiationException {
        return prepareDbAdapter(DatabaseAdapterFactory.AdapterPurpose.CLIENT, autocommit);
    }

    @Override
    public boolean hasSchema() throws SyncException {

        try {
            adapter = prepareAdapterWithAutoCommit();
            return adapter.hasSchema();
        } catch (DatabaseAdapterException e) {
            throw new SyncException(e);
        } finally {
            closeConnection(adapter);
        }

    }

    @Override
    public void applySchema(Schema schema) throws SyncException {

        try {
            // For some databases (e.g. SQLite) DDL statements have to be committed, thats why here autocommit mode=true.
            adapter = prepareAdapterWithAutoCommit();
            adapter.applySchema(schema);
            adapter.createMDSchemaOnClient();
        } catch (DatabaseAdapterException e) {
            throw new SyncException(e);
        } finally {
            // For DDL statements calling rollback is not necessary (and not always supported),
            // so here we only need to close the connection.
            closeConnection(adapter);
        }
    }

    @Override
    public int getLastRevision() throws SyncException {

        int rev = 0;

        try {
            adapter = prepareAdapterWithAutoCommit();
            rev = adapter.getLastRevision();
        } catch (DatabaseAdapterException e) {
            throw new SyncException(e);
        } finally {
            // Same as in getChanges - only SELECT queries so no rollback needed.
            closeConnection(adapter);
        }

        return rev;
    }

    @Override
    public SyncData getChanges() throws SyncException {

        if (adapter == null) {
            throw new IllegalStateException(read(Errors.DATA_NULLABLE_DATABASEADAPTER));
        }

        try {

            if (!Config.getInstance().isSqlTriggerOnClientActivated()) {
                ClientTableSynchronizer synchronizer = new ClientTableSynchronizer(adapter);
                synchronizer.synchronizeClientTables();
            }

            ClientChangesEnumerator changesEnumerator = new ClientChangesEnumerator(adapter, getStrategies());
            return changesEnumerator.getChanges();
        } catch (DatabaseAdapterException e) {
            throw new SyncException(read(Errors.COMMON_CANT_GET_CLIENT_CHANGES), e);
        }
    }

    /**
     * Resolves the conflicts between client and server changes.
     *
     * @param serverData detected changes from server
     * @param clientData detected changes from client
     * @return a sync data holder container which contains the cleaned (without conflicts) client and server datas.
     * @throws SyncException
     */
    @Override
    public SyncDataHolder resolveConflicts(SyncData serverData, SyncData clientData) throws
        SyncException {

        if (adapter == null) {
            throw new IllegalStateException(read(Errors.DATA_NULLABLE_DATABASEADAPTER));
        }

        checkSyncDirectionOfServerChanges(serverData.getChanges(), getStrategies());

        try {
            ClientHashProcessor hashProcessor = new ClientHashProcessor(adapter, getStrategies(), conflictListener);
            return hashProcessor.resolveConflicts(clientData, serverData);
        } catch (Throwable ex) {
            /**
             * no matter what happened, we have to rollback
             */
            rollback(adapter);
            throw new SyncException(read(Errors.COMMON_APPLY_CHANGES_FAILED), ex);
        }
    }

    /**
     * Applies the cleaned (without conflicts) server changes on the client.
     *
     * @param serverData an object which contains the max revision and the changeset from server provider.See
     * {@link de.consistec.doubleganger.common.server.IServerSyncProvider#getChanges(int) }.
     * @return int max revision from server changes
     * @throws SyncException
     */
    @Override
    public int applyChanges(SyncData serverData) throws
        SyncException {

        if (adapter == null) {
            throw new IllegalStateException(read(Errors.DATA_NULLABLE_DATABASEADAPTER));
        }

        checkSyncDirectionOfServerChanges(serverData.getChanges(), getStrategies());

        ClientHashProcessor hashProcessor = new ClientHashProcessor(adapter, getStrategies(), conflictListener);

        try {
            hashProcessor.applyChangesFromServerOnClient(serverData.getChanges());
        } catch (Throwable ex) {
            /**
             * no matter what happened, we have to rollback
             */
            rollback(adapter);
            throw new SyncException(read(Errors.COMMON_APPLY_CHANGES_FAILED), ex);
        }

        // return always max revision from server independent from changeset
        int maxRev = serverData.getRevision();

        LOGGER.debug("return maxRev {}: ", maxRev);

        return maxRev;

    }

    @Override
    public void updateClientRevision(SyncData clientData) throws SyncException {

        try {
            adapter = prepareAdapterWithAutoCommit();
            ClientHashProcessor hashProcessor = new ClientHashProcessor(adapter, getStrategies(), conflictListener);
            hashProcessor.updateClientRevision(clientData);
        } catch (Throwable e) {
            rollback(adapter);
            throw new SyncException(read(Errors.COMMON_CANT_UPDATE_CLIENT_REVISIONS), e);
        } finally {
            closeConnection(adapter);
        }

    }

    @Override
    public void setConflictListener(IConflictListener listener) {
        this.conflictListener = listener;
    }

    @Override
    public IConflictListener getConflictListener() {
        return this.conflictListener;
    }

    @Override
    public void beginTransaction() throws DatabaseAdapterInstantiationException {
        adapter = prepareAdapterNoAutoCommit();
    }

    @Override
    public void commit() throws DatabaseAdapterException {
        if (adapter == null) {
            throw new IllegalStateException(read(Errors.DATA_NULLABLE_DATABASEADAPTER));
        }

        try {
            adapter.commit();
        } catch (DatabaseAdapterException e) {
            LOGGER.error(Errors.DATA_PROBLEMS_WITH_TRANSACTION, e);
            try {
                adapter.getConnection().rollback();
            } catch (SQLException e1) {
                throw new DatabaseAdapterException(read(Errors.DATA_TRANSACTION_ROLLBACK_FAILED), e1);
            }
        } finally {
            try {
                adapter.getConnection().close();
            } catch (SQLException e) {
                LOGGER.error(Errors.DATA_CLOSE_CONNECTION_FAILED);
            }
        }
    }

    /**
     * @return
     * @todo implement configuration of trigger support
     */
    private boolean isTriggerSupported() {
        return false;
    }

    /**
     * To improve readability, call {@link prepareAdapterWithAutoCommit()} or {@link prepareAdapterNoAutoCommit()}.
     *
     * @param autocommit Id autocommit mode should be turned off or on in underlying connection.
     * @return Database adapter instance.
     * @throws DatabaseAdapterInstantiationException
     */
    private IDatabaseAdapter prepareAdapter(boolean autocommit) throws DatabaseAdapterInstantiationException {

        if (dbAdapter != null) {
            return dbAdapter;
        }

        try {
            if (getDs() == null) {
                adapter = DatabaseAdapterFactory.newInstance(DatabaseAdapterFactory.AdapterPurpose.CLIENT);
            } else {
                adapter = DatabaseAdapterFactory.newInstance(DatabaseAdapterFactory.AdapterPurpose.SERVER,
                    getDs().getConnection());
            }
            adapter.getConnection().setAutoCommit(autocommit);
        } catch (SQLException ex) {
            throw new DatabaseAdapterInstantiationException(ex);
        }

        return adapter;
    }
}
