package de.consistec.syncframework.impl.commands;

import de.consistec.syncframework.common.exception.SerializationException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.impl.proxy.http_servlet.HttpRequestParamValues;

/**
 * This interface represents any server method call through http requests.
 *
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 11.01.13 09:26
 */
public interface RequestCommand {

//<editor-fold defaultstate="expanded" desc=" Class methods " >

    /**
     * Parses the request, invokes
     * {@link de.consistec.syncframework.common.SyncContext.ServerContext any method }
     * and returns the result.
     *
     * @param paramValues values transfered through the http request parameter
     * @return json serialized response from server
     * @throws SyncException
     * @throws SerializationException
     */
    String execute(HttpRequestParamValues paramValues) throws SyncException,
        SerializationException;

//</editor-fold>

}
