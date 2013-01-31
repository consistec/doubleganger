package de.consistec.syncframework.common;

import java.util.Map;

/**
 * The listener interface for receiving IConflict events.
 * The class that is interested in processing a IConflict event
 * implements this interface, and the object created with that class is registered with a component using the
 * component's
 * <code>addIConflictListener</code> method. When
 * the IConflict event occurs, that object's appropriate
 * method is invoked.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 03.07.12 11:36
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public interface IConflictListener {

    /**
     * Resolves the merge conflict.
     * <p/>
     * @param serverData the server data
     * @param clientData the client data
     * @return data after client has resolved the conflict
     */
    Map<String, Object> resolve(Map<String, Object> serverData, Map<String, Object> clientData);
}
