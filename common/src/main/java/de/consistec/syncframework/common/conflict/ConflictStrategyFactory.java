package de.consistec.syncframework.common.conflict;

import static de.consistec.syncframework.common.i18n.MessageReader.read;
import static de.consistec.syncframework.common.util.Preconditions.checkNotNull;

import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.syncframework.common.i18n.Errors;
import de.consistec.syncframework.common.util.LoggingUtil;

import org.slf4j.cal10n.LocLogger;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 12.12.12 15:02
 */
public final class ConflictStrategyFactory {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(ConflictStrategyFactory.class.getCanonicalName());


    private ConflictStrategyFactory() {
        throw new AssertionError("Instances not allowed");
    }

    /**
     * Creates a class of type IConflictStrategy depends on the configured sync direction.
     * <p/>
     *
     * @param syncDirection {@link SyncDirection}
     * @return IConflictStrategy New instance of {@link de.consistec.syncframework.common.conflict.IConflictStrategy}
     *         implementation.
     */
    public static IConflictStrategy newInstance(SyncDirection syncDirection) throws
        DatabaseAdapterInstantiationException {

        checkNotNull(read(Errors.COMMON_SYNC_DIRECTION_CANT_BE_NULL));

        IConflictStrategy conflictHandlingStrategy = null;

        switch (syncDirection) {
            case CLIENT_TO_SERVER:
                conflictHandlingStrategy = new ClientToServerConflictStrategy();
                break;
            case SERVER_TO_CLIENT:
                conflictHandlingStrategy = new ServerToClientConflictStrategy();
                break;
            case BIDIRECTIONAL:
            default:
                conflictHandlingStrategy = new DefaultConflictStrategy();
        }

        return conflictHandlingStrategy;
    }
}
