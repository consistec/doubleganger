package de.consistec.syncframework.impl.proxy.http_servlet;

/**
 * Enumeration of Http request parameters to be send with synchronization request.
 *
 * @author Piotr wieczorek
 * @company Consistec Engineering and Consulting GmbH
 * @date 19.11.2012 16:38:29
 * @since 0.0.1-SNAPSHOT
 */
public enum SyncRequestHttpParams {
    /**
     * Header which holds the thread id of the client.
     */
    THREAD_ID,
    /**
     * Header which holds the action name of the server provider.
     * The value for this header will be name value of one of {@link SyncAction} entries .
     */
    ACTION,
    /**
     * Header which hold clients revision.
     */
    REVISION,
    /**
     * Header which holds the JSON String with the data.
     */
    CHANGES;
}
