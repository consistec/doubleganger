package de.consistec.doubleganger.common.conflict;

/**
 * Describes the users decision after his conflict handling.
 * <p/>
 * If a conflict happends and the ConflictStrategy is set to FireEvent,
 * then the server will notify the client to resolve this conflict.
 * After resolving the syncframework can decide at the basis on this enum
 * how the user resolved the conflict.
 * <p/>
 * <ul>
 * <li>CLIENT_CHANGE - the user decided to hold the values from client change</li>
 * <li>SERVER_CHANGE - the user decided to take the values from server change</li>
 * <li>USER-EDIT - the user decided neither to hold the client change values nor
 * to take the server change values but he edited the changes</li>
 * </ul>
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 19.03.13 16:18
 */
public enum UserDecision {

    /**
     * The user decided to hold the values from client change.
     */
    CLIENT_CHANGE,
    /**
     * The user decided to take the values from server change.
     */
    SERVER_CHANGE,
    /**
     * The user decided neither to hold the client change values nor
     * to take the server change values but he edited the changes.
     */
    USER_EDIT,
    /**
     * The user wants edit the client change and then use it.
     */
    EDIT_CLIENT_CHANGE,
    /**
     * The user wants edit the server change and then use it.
     */
    EDIT_SERVER_CHANGE;
}
