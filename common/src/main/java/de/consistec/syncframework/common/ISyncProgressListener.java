package de.consistec.syncframework.common;

/**
 * The listener interface for receiving ISyncProgress events.
 * The class that is interested in processing a ISyncProgress event implements this interface, and the object created
 * with that class is registered with a component using the component's
 * <code>addISyncProgressListener</code> method. <br/>
 * When the ISyncProgress event occurs, then object's appropriate method is invoked.
 *
 * @company Consistec Engineering and Consulting GmbH
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public interface ISyncProgressListener {

    /**
     * Progress update.
     *
     * @param message the message
     */
    void progressUpdate(String message);

    /**
     * Sync finished.
     */
    void syncFinished();
}
