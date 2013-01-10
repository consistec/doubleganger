package de.consistec.syncframework.common.conflict;

/**
 * This enumeration represents action to undertake in case of merge conflict.
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date unknown
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public enum ConflictStrategy {

    /**
     * The Server wins.
     */
    SERVER_WINS,
    /**
     * The Client wins.
     */
    CLIENT_WINS,
    /**
     * Application throws an Event.
     */
    FIRE_EVENT;
}
