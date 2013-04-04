package de.consistec.doubleganger.impl.proxy.http_servlet;

/*
 * #%L
 * Project - doubleganger
 * File - HttpRequestParamValues.java
 * %%
 * Copyright (C) 2011 - 2013 consistec GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import de.consistec.doubleganger.common.SyncContext;
import de.consistec.doubleganger.impl.adapter.ISerializationAdapter;

/**
 * This class contains the parameter values containing in the HttpRequest.
 *
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 16.01.13 17:05
 */
public class HttpRequestParamValues {

    private SyncContext.ServerContext ctx;
    private ISerializationAdapter serializationAdapter;
    private String clientRevision;
    private String clientChanges;
    private String clientSettings;

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
        final String clientSettings) {
        this.ctx = ctx;
        this.serializationAdapter = serializationAdapter;
        this.clientRevision = clientRevision;
        this.clientChanges = clientChanges;
        this.clientSettings = clientSettings;
    }

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
}
