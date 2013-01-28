package de.consistec.syncframework.server;

import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.SyncData;
import de.consistec.syncframework.common.exception.SerializationException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.util.LoggingUtil;
import de.consistec.syncframework.impl.commands.RequestCommand;
import de.consistec.syncframework.impl.i18n.Errors;
import de.consistec.syncframework.impl.proxy.http_servlet.HttpRequestParamValues;

import org.slf4j.cal10n.LocLogger;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 25.01.13 10:09
 */
public class GetChangesCommandMock implements RequestCommand {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(
        GetChangesCommandMock.class.getCanonicalName());

    private final SyncData expectedSyncData;


    public GetChangesCommandMock(SyncData expectedSyncData) {
        this.expectedSyncData = expectedSyncData;
    }

    @Override
    public String execute(final HttpRequestParamValues paramValues) throws SyncException, SerializationException {
        try {
            return paramValues.getSerializationAdapter().serializeChangeList(expectedSyncData).toString();
        } catch (SerializationException e) {
            LOGGER.error(read(Errors.CANT_GET_SERVER_CHANGES), e);
            throw e;
        }
    }
}
