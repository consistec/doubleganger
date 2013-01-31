package de.consistec.syncframework.impl.commands;

import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.SyncData;
import de.consistec.syncframework.common.exception.SerializationException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.util.LoggingUtil;
import de.consistec.syncframework.common.util.StringUtil;
import de.consistec.syncframework.impl.i18n.Errors;
import de.consistec.syncframework.impl.proxy.http_servlet.HttpRequestParamValues;

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

    //<editor-fold defaultstate="expanded" desc=" Class methods " >

    /**
     * Parses the request, invokes
     * {@link de.consistec.syncframework.common.SyncContext.ServerContext getChanges() }
     * and returns the result.
     *
     * @param paramValues values transfered through the http request parameter
     * @return the result of the server operation getChanges().
     * @throws SyncException
     * @throws SerializationException
     */
    @Override
    public String execute(final HttpRequestParamValues paramValues
    ) throws
        SyncException,
        SerializationException {

        try {
            SyncData serverData;

            if (!StringUtil.isNullOrEmpty(paramValues.getClientRevision())) {

                try {
                    serverData = paramValues.getCtx().getChanges(Integer.parseInt(paramValues.getClientRevision()));
                } catch (NumberFormatException ex) {
                    LOGGER.error(read(Errors.CANT_PARSE_CLIENT_REVISION), ex);
                    throw new SyncException(ex.getLocalizedMessage(), ex);
                }
            } else {
                LOGGER.error(Errors.CANT_GETCHANGES_NO_CLIENT_REVISION);
                throw new SyncException(read(Errors.CANT_GETCHANGES_NO_CLIENT_REVISION));
            }

            return paramValues.getSerializationAdapter().serializeChangeList(serverData).toString();

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
