package de.consistec.doubleganger.common.data;

import de.consistec.doubleganger.common.conflict.UserDecision;

/**
 * Represents a Change after the user has resolved his conflicts.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 19.03.13 16:31
 */
public class ResolvedChange extends Change {

    private UserDecision decision;

    /**
     * Constructor of ResolvedChange which takes an UserDecision.
     *
     * @param decision - the users decision after conflict resolving.
     */
    public ResolvedChange(UserDecision decision) {
        this.decision = decision;
    }

    /**
     * The users decision after conflict resolving.
     *
     * @return UserDecision - enum for the users decision.
     */
    public UserDecision getDecision() {
        return decision;
    }
}
