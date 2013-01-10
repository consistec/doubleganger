package de.consistec.syncframework.common.server;

import de.consistec.syncframework.common.Tuple;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.exception.SyncException;

import java.util.List;

/**
 * This interface defines behavior which must be implemented by classes which should act as a proxy
 * between client and server side synchronization.
 * If you want to communicate with the remote server, you have to implement this interface in a class,
 * that will be then specified in framework configuration as proxy provider class
 * {@link de.consistec.syncframework.common.Config#setServerProxy(java.lang.Class)}
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date unknown
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public interface IServerSyncProvider {

    /**
     * Apply changes.
     *
     * @param changes Client changes.
     * @param clientRevision Client revision.
     * @return New revision
     * @throws SyncException the sync exception
     */
    int applyChanges(List<Change> changes, int clientRevision) throws SyncException;

    /**
     * Gets the changes.
     *
     *
     * @param rev the rev
     * @return the changes
     * @throws SyncException the sync exception
     */
    Tuple<Integer, List<Change>> getChanges(int rev) throws SyncException;

    /**
     * Returns the server schema.
     * Schema should consists only from monitored data tables (no md tables);
     *
     * @return the schema
     * @throws SyncException the sync exception
     */
    Schema getSchema() throws SyncException;

}
