package de.consistec.syncframework.common.client;

import de.consistec.syncframework.common.IConflictListener;
import de.consistec.syncframework.common.SyncData;
import de.consistec.syncframework.common.SyncDataHolder;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterInstantiationException;

/**
 * This interface defines the behavior of the synchronization provider on the client's side.
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public interface IClientSyncProvider {

    /**
     * Resolves conflicts between client and server changes.
     *
     * @param serverData detected changes from server
     * @param clientData detected changes from client
     * @return holder object whicht contains cleaned (without conflicts) client and server sync data
     * @throws SyncException
     */
    SyncDataHolder resolveConflicts(SyncData serverData, SyncData clientData) throws SyncException;

    /**
     * Applies the cleaned (without conflicts) server changes on the client.
     *
     * @param serverData an object which contains the max revision and the changeset from server provider.See
     * {@link de.consistec.syncframework.common.server.IServerSyncProvider#getChanges(int) }.
     * @return new client revision.
     * @throws SyncException
     */
    int applyChanges(SyncData serverData) throws
        SyncException;

    /**
     * Gets the client changes.
     * See {@link de.consistec.syncframework.common.server.IServerSyncProvider#applyChanges(SyncData) }.
     *
     * @return the changes
     * @throws SyncException the sync exception
     */
    SyncData getChanges() throws SyncException;

    /**
     * Gets the last revision.
     *
     * @return Last revision
     * @throws SyncException
     */
    int getLastRevision() throws SyncException;

    /**
     * Update client revision of the changes which have been sent to the server.
     *
     * @param serverData server data which contains the new max revision from server and the changes to applied on client.
     * @throws SyncException the sync exception
     */
    void updateClientRevision(SyncData serverData) throws SyncException;

    /**
     * Checks whether schema has been initialized on client side.
     * Check should include also md tables.
     *
     * @return true, if schema is initialized
     * @throws SyncException
     */
    boolean hasSchema() throws SyncException;

    /**
     * Apply schema returned from server.
     * See {@link de.consistec.syncframework.common.server.IServerSyncProvider#getSchema() }.
     *
     * @param schema the schema
     * @throws SyncException the sync exception
     */
    void applySchema(Schema schema) throws SyncException;

    /**
     * Sets the conflict listener.
     *
     * @param listener the new conflict listener
     */
    void setConflictListener(IConflictListener listener);

    /**
     * Gets the conflict listener.
     *
     * @return the conflict listener
     */
    IConflictListener getConflictListener();

    /**
     * Creates the database adapter with autocommit set to false.
     */
    void beginTransaction() throws DatabaseAdapterInstantiationException;

    /**
     * Commits the connection created in beginTransaction.
     */
    void commit() throws DatabaseAdapterException;

}
