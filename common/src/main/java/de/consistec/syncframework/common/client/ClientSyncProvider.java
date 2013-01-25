package de.consistec.syncframework.common.client;

import static de.consistec.syncframework.common.i18n.MessageReader.read;
import static de.consistec.syncframework.common.util.Preconditions.checkSyncDirectionOfServerChanges;

import de.consistec.syncframework.common.AbstractSyncProvider;
import de.consistec.syncframework.common.IConflictListener;
import de.consistec.syncframework.common.SyncData;
import de.consistec.syncframework.common.SyncDataHolder;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.adapter.DatabaseAdapterFactory;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.syncframework.common.i18n.Errors;
import de.consistec.syncframework.common.util.LoggingUtil;

import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.cal10n.LocLogger;

/**
 * Client synchronization provider.
 * <p>
 * Do not use directly! Instead, use {@link de.consistec.syncframework.common.SyncContext.client())} factory methods.
 * </p>
 * <p/>
 * Thats the most important class for client site of synchronization. It provides all methods to perform
 * synchronization but the logic of synchronization process is included in
 * {@link de.consistec.syncframework.common.client.SyncAgent#synchronize() synchronize()}
 * method of synchronization agent.<br/>
 * <b style="color: red;">Warning!</b> This class should not be used directly in client code. Instead always use
 * {@link de.consistec.syncframework.common.SyncContext.ClientContext}.
 *
 * @author Markus Backes
 * @company Consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public final class ClientSyncProvider extends AbstractSyncProvider implements IClientSyncProvider {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private static final LocLogger LOGGER = LoggingUtil.createLogger(ClientSyncProvider.class.getCanonicalName());
    private IConflictListener conflictListener;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Class constructors " >

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
     * Create a new adapter instance with autocommit disabled for the underlying connection.
     *
     * @return Database adapter instance.
     * @throws DatabaseAdapterInstantiationException
     */
    private IDatabaseAdapter prepareAdapterNoAutoCommit() throws DatabaseAdapterInstantiationException {
        return prepareAdapter(false);
    }

    /**
     * Create a new adapter instance with autocommit enabled for the underlying connection.
     *
     * @return Database adapter instance.
     * @throws DatabaseAdapterInstantiationException
     */
    private IDatabaseAdapter prepareAdapterWithAutoCommit() throws DatabaseAdapterInstantiationException {
        return prepareAdapter(true);
    }

    @Override
    public boolean hasSchema() throws SyncException {

        IDatabaseAdapter adapter = null;

        try {
            adapter = prepareAdapterNoAutoCommit();
            return adapter.hasSchema();
        } catch (DatabaseAdapterException e) {
            throw new SyncException(e);
        } finally {
            closeConnection(adapter);
        }

    }

    @Override
    public void applySchema(Schema schema) throws SyncException {

        IDatabaseAdapter adapter = null;

        try {
            // For some databases (e.g. SQLite) DDL statements have to be committed, thats why here autocommit mode=true.
            adapter = prepareAdapterWithAutoCommit();
            adapter.applySchema(schema);
            adapter.createMDSchema();
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
        IDatabaseAdapter adapter = null;

        try {
            adapter = prepareAdapterNoAutoCommit();
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

        IDatabaseAdapter adapter = null;

        try {
            adapter = prepareAdapterNoAutoCommit();

            if (!isTriggerSupported()) {
                List<Change> detectedChanges = synchronizeClientTables();
                return new SyncData(0, detectedChanges);
            }

            ClientChangesEnumerator changesEnumerator = new ClientChangesEnumerator(adapter, getStrategies());
            return changesEnumerator.getChanges();
        } catch (DatabaseAdapterException e) {
            throw new SyncException(read(Errors.COMMON_CANT_GET_CLIENT_CHANGES), e);
        } finally {
            // changesEnumerator.getChanges() performs only SELECT queries so here we do not need to rollback.
            closeConnection(adapter);
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

        checkSyncDirectionOfServerChanges(serverData.getChanges(), getStrategies());

        IDatabaseAdapter adapter = null;

        try {
            adapter = prepareAdapterNoAutoCommit();
        } catch (DatabaseAdapterInstantiationException ex) {
            throw new SyncException(ex);
        }

        try {
            ClientHashProcessor hashProcessor = new ClientHashProcessor(adapter, getStrategies(), conflictListener);
            return hashProcessor.resolveConflicts(clientData, serverData);
        } catch (Throwable ex) {
            /**
             * no matter what happened, we have to rollback
             */
            rollback(adapter);
            throw new SyncException(read(Errors.COMMON_APPLY_CHANGES_FAILED), ex);
        } finally {
            closeConnection(adapter);
        }
    }

    /**
     * Applies the cleaned (without conflicts) server changes on the client.
     *
     * @param serverData an object which contains the max revision and the changeset from server provider.See
     * {@link de.consistec.syncframework.common.server.IServerSyncProvider#getChanges(int) }.
     * @return int max revision from server changes
     * @throws SyncException
     */
    @Override
    public int applyChanges(SyncData serverData) throws
        SyncException {

        checkSyncDirectionOfServerChanges(serverData.getChanges(), getStrategies());

        IDatabaseAdapter adapter = null;

        try {
            adapter = prepareAdapterNoAutoCommit();
        } catch (DatabaseAdapterInstantiationException ex) {
            throw new SyncException(ex);
        }

        ClientHashProcessor hashProcessor = new ClientHashProcessor(adapter, getStrategies(), conflictListener);

        try {
            hashProcessor.applyChangesFromServerOnClient(serverData.getChanges());
            adapter.getConnection().commit();
        } catch (Throwable ex) {
            /**
             * no matter what happened, we have to rollback
             */
            rollback(adapter);
            throw new SyncException(read(Errors.COMMON_APPLY_CHANGES_FAILED), ex);
        } finally {
            closeConnection(adapter);
        }

        // return always max revision from server independet from changeset
        int maxRev = serverData.getRevision();

        LOGGER.debug("return maxRev {}: ", maxRev);

        return maxRev;

    }

    @Override
    public void updateClientRevision(SyncData clientData) throws SyncException {

        IDatabaseAdapter adapter = null;

        try {
            adapter = prepareAdapterNoAutoCommit();
            ClientHashProcessor hashProcessor = new ClientHashProcessor(adapter, getStrategies(), conflictListener);
            hashProcessor.updateClientRevision(clientData);
            adapter.getConnection().commit();
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

    /**
     * @return
     * @todo implement configuration of trigger support
     */
    private boolean isTriggerSupported() {
        return false;
    }

    /**
     * Calls the <code>ClientTableSynchronizer</code> to looks for new, modified
     * and deleted rows in all client data tables.
     *
     * @return List<Change> the list of client changes.
     * @throws SyncException if the <code>ClientTableSynchronizer</code> cannot do its work
     * and therefore throws an more specific exception.
     */
    private List<Change> synchronizeClientTables() throws SyncException {

        IDatabaseAdapter adapter = null;

        try {
            adapter = prepareAdapterNoAutoCommit();
            ClientTableSynchronizer tableSynchronizer = new ClientTableSynchronizer(adapter);
            List<Change> clientChanges = tableSynchronizer.synchronizeClientTables();
            adapter.getConnection().commit();
            return clientChanges;
        } catch (DatabaseAdapterException e) {

            rollback(adapter);
            throw new SyncException(read(Errors.COMMON_SYNCHRONIZE_CLIENT_TABLE_FAILED), e);
        } catch (SQLException ex) {
            rollback(adapter);
            throw new SyncException(null, ex);
        } catch (RuntimeException ex) {
            // No matter what, do rollback.
            rollback(adapter);
            throw ex;
        } finally {
            closeConnection(adapter);
        }
    }

    /**
     * To improve readability, call {@link prepareAdapterWithAutoCommit()} or {@link prepareAdapterNoAutoCommit()}.
     *
     * @param autocommit Id autocommit mode should be turned off or on in underlying connection.
     * @return Database adapter instance.
     * @throws DatabaseAdapterInstantiationException
     */
    private IDatabaseAdapter prepareAdapter(boolean autocommit) throws DatabaseAdapterInstantiationException {

        IDatabaseAdapter adapter = null;
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
    //</editor-fold>
}
