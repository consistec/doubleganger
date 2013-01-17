package de.consistec.syncframework.impl.proxy.http_servlet;

import de.consistec.syncframework.common.SyncContext;
import de.consistec.syncframework.impl.adapter.ISerializationAdapter;

/**
 * This class contains the parameter values containing in the HttpRequest.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 16.01.13 17:05
 */
public class HttpRequestParamValues {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private SyncContext.ServerContext ctx;
    private ISerializationAdapter serializationAdapter;
    private String clientRevision;
    private String clientChanges;
    private String clientSettings;
//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class constructors " >

    /**
     * Constructor.
     *
     * @param ctx server context
     * @param serializationAdapter adapter for serialization
     * @param clientRevision clients revision
     * @param clientChanges clients changes
     * @param clientSettings clients settings
     */
    public HttpRequestParamValues(final SyncContext.ServerContext ctx,
                                  final ISerializationAdapter serializationAdapter,
                                  final String clientRevision,
                                  final String clientChanges,
                                  final String clientSettings
    ) {
        this.ctx = ctx;
        this.serializationAdapter = serializationAdapter;
        this.clientRevision = clientRevision;
        this.clientChanges = clientChanges;
        this.clientSettings = clientSettings;
    }

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >

    /**
     * The servers context.
     *
     * @return server context
     */
    public SyncContext.ServerContext getCtx() {
        return ctx;
    }

    /**
     * The adapter to serialize request and deserialize responses.
     *
     * @return serialization adapter
     */
    public ISerializationAdapter getSerializationAdapter() {
        return serializationAdapter;
    }

    /**
     * The clients revision in http request.
     *
     * @return clients revision
     */
    public String getClientRevision() {
        return clientRevision;
    }

    /**
     * The clients changes in http request.
     *
     * @return client changes
     */
    public String getClientChanges() {
        return clientChanges;
    }

    /**
     * The clients settings in http request.
     *
     * @return clients settings
     */
    public String getClientSettings() {
        return clientSettings;
    }

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class methods " >

//</editor-fold>

}
