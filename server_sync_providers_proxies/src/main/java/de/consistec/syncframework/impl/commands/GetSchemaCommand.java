package de.consistec.syncframework.impl.commands;

import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.exception.SerializationException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.util.LoggingUtil;
import de.consistec.syncframework.impl.i18n.Errors;
import de.consistec.syncframework.impl.proxy.http_servlet.HttpRequestParamValues;

import org.slf4j.cal10n.LocLogger;

/**
 * Concrete class of RequestCommand that represents the server method call to getSchema.
 *
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 11.01.13 09:27
 */
public class GetSchemaCommand implements RequestCommand {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private static final LocLogger LOGGER = LoggingUtil.createLogger(GetSchemaCommand.class.getCanonicalName());
//</editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class methods " >

    /**
     * Parses the request, invokes
     * {@link de.consistec.syncframework.common.SyncContext.ServerContext getSchema() }
     * and returns the result.
     *
     * @param paramValues values transfered through the http request parameter
     * @return the result of the server operation getSchema().
     * @throws SyncException
     * @throws SerializationException
     */
    @Override
    public String execute(final HttpRequestParamValues paramValues
    ) throws
        SyncException, SerializationException {
        try {
            return paramValues.getSerializationAdapter().serializeSchema(paramValues.getCtx().getSchema()).toString();
        } catch (SyncException e) {
            LOGGER.error(read(Errors.CANT_GET_CREATE_DB_SCHEMA), e);
            throw e;
        } catch (SerializationException e) {
            LOGGER.error(read(Errors.CANT_GET_CREATE_DB_SCHEMA), e);
            throw e;
        }
    }
//</editor-fold>

}
