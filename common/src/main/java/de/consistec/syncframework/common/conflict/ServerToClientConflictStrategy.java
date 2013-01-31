package de.consistec.syncframework.common.conflict;

import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.IConflictListener;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.client.ConflictHandlingData;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.i18n.Errors;
import de.consistec.syncframework.common.util.LoggingUtil;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import org.slf4j.cal10n.LocLogger;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 12.12.12 15:22
 */
public class ServerToClientConflictStrategy extends DefaultConflictStrategy {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(
        ServerToClientConflictStrategy.class.getCanonicalName());

    @Override
    public void resolveByClientWinsStrategy(final IDatabaseAdapter adapter, final ConflictHandlingData data
    ) throws DatabaseAdapterException {
        throw new IllegalStateException(
            read(Errors.NOT_SUPPORTED_CONFLICT_STRATEGY, ConflictStrategy.SERVER_WINS.name(),
                SyncDirection.CLIENT_TO_SERVER.name()));
    }

    @Override
    public void resolveByServerWinsStrategy(final IDatabaseAdapter adapter, final ConflictHandlingData data
    ) throws DatabaseAdapterException, NoSuchAlgorithmException {

        super.resolveByServerWinsStrategy(adapter, data);
    }

    @Override
    public void resolveByFireEvent(final IDatabaseAdapter adapter, final ConflictHandlingData data,
                                   final Map<String, Object> clientData, final IConflictListener conflictListener
    ) throws SyncException, DatabaseAdapterException, NoSuchAlgorithmException {
        throw new IllegalStateException(
            read(Errors.NOT_SUPPORTED_CONFLICT_STRATEGY, ConflictStrategy.FIRE_EVENT.name(),
                SyncDirection.SERVER_TO_CLIENT.name()));
    }
}
