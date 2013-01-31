package de.consistec.syncframework.common;

/**
 * Enumeration with possibles synchronization directions.
 * <p/>
 * Synchronization direction specifies which site of synchronization has priority over the other site.
 *
 * @author Marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 30.10.12 15:46
 * @since 0.0.1-SNAPSHOT
 */
public enum SyncDirection {

    /**
     * Only from client to server.
     */
    CLIENT_TO_SERVER,
    /**
     * Only from server to client.
     */
    SERVER_TO_CLIENT,
    /**
     * both directions, client to server and server to client.
     */
    BIDIRECTIONAL;
}
