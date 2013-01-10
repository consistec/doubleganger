package de.consistec.syncframework.common.util;

import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.i18n.Errors;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 13.12.12 15:39
 */
public final class SyncStatePreconditions {

//<editor-fold defaultstate="expanded" desc=" Class fields " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class constructors " >

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class methods " >

//</editor-fold>

    private SyncStatePreconditions() {
        throw new AssertionError("No instances allowed");
    }

    /**
     * Validates the state of sync direction and conflict strategy.
     * <p/>
     * Invalid states are the following:
     * <ul>
     * <li>
     * the conflict strategies SERVER_WINS and FIRE_EVENT in combination with the CLIENT_TO_SERVER direction and
     * </li>
     * <li>
     * the conflict strategies CLIENT_WINS and FIRE_EVENT in combination with the SERVER_TO_CLIENT direction
     * </li>
     * </ul>
     *
     * @param direction - sync direction to validate
     * @param strategy - conflict strategy to validate
     */
    public static void checkSyncDirectionAndConflictStrategy(SyncDirection direction, ConflictStrategy strategy
    ) {
        if ((strategy == ConflictStrategy.SERVER_WINS || strategy == ConflictStrategy.FIRE_EVENT)
            && direction == SyncDirection.CLIENT_TO_SERVER
            || (strategy == ConflictStrategy.CLIENT_WINS || strategy == ConflictStrategy.FIRE_EVENT)
            && direction == SyncDirection.SERVER_TO_CLIENT) {
            throw new IllegalStateException(
                read(Errors.NOT_SUPPORTED_CONFLICT_STRATEGY, strategy.name(), direction.name()));
        }
    }
}
