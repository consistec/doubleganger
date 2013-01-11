package de.consistec.syncframework.impl.commands;

import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.SyncContext;
import de.consistec.syncframework.common.Tuple;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.exception.SerializationException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.util.LoggingUtil;
import de.consistec.syncframework.common.util.StringUtil;
import de.consistec.syncframework.impl.adapter.ISerializationAdapter;
import de.consistec.syncframework.impl.i18n.Errors;

import java.util.List;
import org.slf4j.cal10n.LocLogger;

/**
 * Concrete class of RequestCommand that represents the server method call to getChanges.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 11.01.13 09:27
 */
public class GetChangesCommand implements RequestCommand {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private static final LocLogger LOGGER = LoggingUtil.createLogger(GetChangesCommand.class.getCanonicalName());
//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class constructors " >

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >

//</editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class methods " >

    /**
     * Parses the request, invokes
     * {@link de.consistec.syncframework.common.SyncContext.ServerContext getChanges() }
     * and returns the result.
     *
     * @param ctx serverContext
     * @param serializationAdapter adapter for serialize the request parameter
     * @param clientRevision the client revision
     * @param clientChanges the client changes
     * @return the result of the server operation getChanges().
     * @throws SyncException
     * @throws SerializationException
     */
    @Override
    public String execute(final SyncContext.ServerContext ctx, final ISerializationAdapter serializationAdapter,
                          final String clientRevision, final String clientChanges
    ) throws
        SyncException,
        SerializationException {

        try {
            Tuple<Integer, List<Change>> changesTuple;

            if (!StringUtil.isNullOrEmpty(clientRevision)) {

                try {
                    changesTuple = ctx.getChanges(Integer.parseInt(clientRevision));
                } catch (NumberFormatException ex) {
                    LOGGER.error(read(Errors.CANT_PARSE_CLIENT_REVISION), ex);
                    throw new SyncException(ex.getLocalizedMessage(), ex);
                }
            } else {
                LOGGER.error(Errors.CANT_GETCHANGES_NO_CLIENT_REVISION);
                throw new SyncException(read(Errors.CANT_GETCHANGES_NO_CLIENT_REVISION));
            }

            return serializationAdapter.serializeChangeList(changesTuple).toString();

        } catch (SyncException e) {
            LOGGER.error(read(Errors.CANT_GET_SERVER_CHANGES), e);
            throw e;
        } catch (SerializationException e) {
            LOGGER.error(read(Errors.CANT_GET_SERVER_CHANGES), e);
            throw e;
        }
    }
//</editor-fold>

}
