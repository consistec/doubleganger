package de.consistec.syncframework.common.client;

import de.consistec.syncframework.common.IConflictListener;
import de.consistec.syncframework.common.SyncData;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.exception.SyncException;

import java.util.List;

/**
 * This interface defines the behavior of the synchronization provider on the client's side.
 *
 * @author Markus Backes
 * @company Consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public interface IClientSyncProvider {

    /**
     * Apply changes.
     *
     * @param serverData an object which contains the max revision and the changeset from server provider.See
     * {@link de.consistec.syncframework.common.server.IServerSyncProvider#getChanges(int) }.
     * @param clientData the changes data from client side.
     * @return new client revision.
     * @throws SyncException
     */
    SyncData applyChanges(SyncData serverData, SyncData clientData) throws
        SyncException;

    /**
     * Gets the client changes.
     * See {@link de.consistec.syncframework.common.server.IServerSyncProvider#applyChanges(java.util.List, int) }.
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
     * Calls the <code>ClientTableSynchronizer</code> to looks for new, modified
     * and deleted rows in all client data tables.
     *
     * @return List<Change> the list of client changes.
     * @throws SyncException if the <code>ClientTableSynchronizer</code> cannot do its work
     * and therefore throws an more specific exception.
     */
    List<Change> synchronizeClientTables() throws SyncException;
}
