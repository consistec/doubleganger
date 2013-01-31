package de.consistec.syncframework.impl.commands;

import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.SyncSettings;
import de.consistec.syncframework.common.exception.SerializationException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.util.LoggingUtil;
import de.consistec.syncframework.impl.i18n.Errors;
import de.consistec.syncframework.impl.proxy.http_servlet.HttpRequestParamValues;

import org.slf4j.cal10n.LocLogger;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 16.01.13 16:26
 */
public class ValidateSettingsCommand implements RequestCommand {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private static final LocLogger LOGGER = LoggingUtil.createLogger(ValidateSettingsCommand.class.getCanonicalName());
//</editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class methods " >

    /**
     * Parses the request, invokes
     * {@link de.consistec.syncframework.common.SyncContext.ServerContext validate() }
     * and returns the result.
     *
     * @param paramValues values transfered through the http request parameter
     * @return the result of the server operation getSchema().
     * @throws SyncException
     * @throws SerializationException
     */
    @Override
    public String execute(final HttpRequestParamValues paramValues
    ) throws SyncException, SerializationException {
        try {

            if (paramValues.getClientSettings() != null) {

                try {
                    SyncSettings deserializedSettings = paramValues.getSerializationAdapter().deserializeSettings(
                        paramValues.getClientSettings());
                    paramValues.getCtx().validate(deserializedSettings);
                } catch (NumberFormatException ex) {
                    LOGGER.error(read(Errors.CANT_PARSE_CLIENT_REVISION), ex);
                    throw new SyncException(ex.getLocalizedMessage(), ex);
                }
            } else {
                LOGGER.error(Errors.CANT_GETCHANGES_NO_CLIENT_REVISION);
                throw new SyncException(read(Errors.CANT_GETCHANGES_NO_CLIENT_REVISION));
            }

            return "";

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
