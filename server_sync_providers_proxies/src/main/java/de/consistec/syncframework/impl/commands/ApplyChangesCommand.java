package de.consistec.syncframework.impl.commands;

import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.SyncContext;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.exception.SerializationException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.util.LoggingUtil;
import de.consistec.syncframework.common.util.StringUtil;
import de.consistec.syncframework.impl.adapter.ISerializationAdapter;
import de.consistec.syncframework.impl.i18n.Errors;
import de.consistec.syncframework.impl.i18n.Infos;

import java.util.List;
import org.slf4j.cal10n.LocLogger;

/**
 * Concrete class of RequestCommand that represents the server method call to applyChanges.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 11.01.13 09:28
 */
public class ApplyChangesCommand implements RequestCommand {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private static final LocLogger LOGGER = LoggingUtil.createLogger(ApplyChangesCommand.class.getCanonicalName());
//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class constructors " >

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >

//</editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class methods " >

    /**
     * Parses the request, invokes
     * {@link de.consistec.syncframework.common.SyncContext.ServerContext applyChanges() }
     * and returns the result.
     *
     * @param ctx serverContext
     * @param serializationAdapter adapter for serialize the request parameter
     * @param revision the client revision
     * @param clientChanges the client changes
     * @return the result of the server operation applyChanges().
     * @throws SyncException
     * @throws SerializationException
     */
    @Override
    public String execute(final SyncContext.ServerContext ctx, final ISerializationAdapter serializationAdapter,
                          final String revision, final String clientChanges
    ) throws
        SyncException,
        SerializationException {

        if (!StringUtil.isNullOrEmpty(clientChanges) && !StringUtil.isNullOrEmpty(revision)) {
            try {
                final int clientRevision = Integer.valueOf(revision);

                List<Change> deserializedChanges = serializationAdapter.deserializeChangeList(
                    clientChanges);
                LOGGER.debug("deserialized Changes:");
                LOGGER.debug("<{}>", deserializedChanges);
                int nextServerRevisionSendToClient = ctx.applyChanges(deserializedChanges, clientRevision);
                LOGGER.info(Infos.NEW_SERVER_REVISION, nextServerRevisionSendToClient);

                return String.valueOf(nextServerRevisionSendToClient);
            } catch (SyncException e) {
                LOGGER.error(read(Errors.CANT_APPLY_CHANGES, e.getLocalizedMessage()));
                throw e;
            }
        }

        return null;
    }
//</editor-fold>

}
