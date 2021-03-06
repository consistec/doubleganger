package de.consistec.doubleganger.impl.proxy.http_servlet;

/*
 * #%L
 * Project - doubleganger
 * File - HttpServerSyncProxy.java
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
import static de.consistec.doubleganger.common.i18n.MessageReader.read;
import static de.consistec.doubleganger.common.util.CollectionsUtil.newArrayList;
import static de.consistec.doubleganger.impl.proxy.http_servlet.SyncRequestHttpParams.ACTION;
import static de.consistec.doubleganger.impl.proxy.http_servlet.SyncRequestHttpParams.CHANGES;
import static de.consistec.doubleganger.impl.proxy.http_servlet.SyncRequestHttpParams.REVISION;
import static de.consistec.doubleganger.impl.proxy.http_servlet.SyncRequestHttpParams.SETTINGS;
import static de.consistec.doubleganger.impl.proxy.http_servlet.SyncRequestHttpParams.THREAD_ID;

import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.SyncData;
import de.consistec.doubleganger.common.SyncSettings;
import de.consistec.doubleganger.common.data.schema.Schema;
import de.consistec.doubleganger.common.exception.SerializationException;
import de.consistec.doubleganger.common.exception.ServerStatusException;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.server.IServerSyncProvider;
import de.consistec.doubleganger.common.server.ServerStatus;
import de.consistec.doubleganger.common.util.LoggingUtil;
import de.consistec.doubleganger.common.util.StringUtil;
import de.consistec.doubleganger.impl.adapter.ISerializationAdapter;
import de.consistec.doubleganger.impl.adapter.JSONSerializationAdapter;
import de.consistec.doubleganger.impl.i18n.Errors;
import de.consistec.doubleganger.impl.i18n.Infos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Properties;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.cal10n.LocLogger;

/**
 * IServerSyncProvider implementation for HTTP transport layer.
 * <p/>
 * This proxy invokes methods on remote synchronization server provider through http protocol.
 * It uses the {@link JSONSerializationAdapter serializer} to transform data to and from String.<br/>
 * Requests and responses are sended/received with use of org.apache.httpcomponents library.
 * <p/>
 * Objects of this class should <b>not</b> be created directly with {@code new} keyword. Instead, a canonical name
 * has to be specified in framework configuration. See {@link de.consistec.doubleganger.common.Config Config class}.
 *
 * @author Markus Backes
 * @version 0.0.1-SNAPSHOT
 * @company consistec Engineering and Consulting GmbH
 * @date 12.04.12 11:30
 */
public class HttpServerSyncProxy implements IServerSyncProvider {

    /**
     * Header name in which server exception message will be stored.
     */
    protected static final String HEADER_NAME_SERVER_EXCEPTION = "server_exception";
    /**
     * Header name in which a flag is stored to tell the client that
     * an serialized server exception is in the http response.
     */
    protected static final String HEADER_NAME_EXCEPTION = "exception";
    private static final String PROPS_SERVER_URL = "url";
    private static final String PROPS_USERNAME = "username";
    private static final String PROPS_PASSWORD = "password";
    private static final LocLogger LOGGER = LoggingUtil.createLogger(HttpServerSyncProxy.class.getCanonicalName());
    private URI host;
    private Credentials credentials;
    private ISerializationAdapter serializationAdapter;
    private String threadId;

    /**
     * Instantiates a new server sync provider proxy.
     * <p/>
     * Do not instantiate this class directly! Framework will make it!.
     * <br/> Instead specify canonical name of this class in framework configuration as server proxy class.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    public HttpServerSyncProxy() throws URISyntaxException {
        LOGGER.warn("---------------------------------------  HttpProxy constructor ");
        initializeDefaultSerializationAdapter();

        Properties syncServerProperties = Config.getInstance().getServerProxyProviderProperties();

        host = new URI(syncServerProperties.getProperty(PROPS_SERVER_URL));
        String username = syncServerProperties.getProperty(PROPS_USERNAME);
        String password = syncServerProperties.getProperty(PROPS_PASSWORD);

        if (!StringUtil.isNullOrEmpty(username)) {
            credentials = new UsernamePasswordCredentials(username, password);
        }
    }

    /**
     * Instantiates a new server sync provider proxy.
     * <p/>
     * Do not instantiate this class directly! Framework will make it!.
     * <br/> Instead specify canonical name of this class in framework configuration as server proxy class.
     *
     * @param uri the requested uri
     * @throws java.net.URISyntaxException the uRI syntax exception
     */
    public HttpServerSyncProxy(URI uri) throws URISyntaxException {
        LOGGER.warn("---------------------------------------  HttpProxy constructor ");

        host = uri;
        if (host == null) {
            Properties syncServerProperties = Config.getInstance().getServerProxyProviderProperties();
            host = new URI(syncServerProperties.getProperty(PROPS_SERVER_URL));
        }

        initializeDefaultSerializationAdapter();

        Properties syncServerProperties = Config.getInstance().getServerProxyProviderProperties();

        String username = syncServerProperties.getProperty(PROPS_USERNAME);
        String password = syncServerProperties.getProperty(PROPS_PASSWORD);

        if (!StringUtil.isNullOrEmpty(username)) {
            credentials = new UsernamePasswordCredentials(username, password);
        }
    }

    private void initializeDefaultSerializationAdapter() {
        this.serializationAdapter = new JSONSerializationAdapter();
    }

    @Override
    public void validate(final SyncSettings clientSettings) throws SyncException {
        try {
            List<NameValuePair> data = newArrayList();
            data.add(new BasicNameValuePair(THREAD_ID.name(), threadId));
            data.add(new BasicNameValuePair(ACTION.name(), SyncAction.VALIDATE_SETTINGS.getStringName()));
            data.add(new BasicNameValuePair(SETTINGS.name(),
                serializationAdapter.serializeSettings(clientSettings).toString()));

            request(data);
        } catch (SerializationException e) {
            throw new SyncException(read(Errors.CANT_APPLY_CHANGES_SERIALIZATION_FAILURE), e);
        }
    }

    @Override
    public int applyChanges(SyncData clientData) throws SyncException {

        threadId = "1";

        try {
            List<NameValuePair> data = newArrayList();
            data.add(new BasicNameValuePair(THREAD_ID.name(), threadId));
            data.add(new BasicNameValuePair(ACTION.name(), SyncAction.APPLY_CHANGES.getStringName()));
            data.add(new BasicNameValuePair(CHANGES.name(),
                serializationAdapter.serializeChangeList(clientData.getChanges()).toString()));
            data.add(new BasicNameValuePair(REVISION.name(), String.valueOf(clientData.getRevision())));
            return Integer.parseInt(request(data));
        } catch (SerializationException e) {
            throw new SyncException(read(Errors.CANT_APPLY_CHANGES_SERIALIZATION_FAILURE), e);
        }
    }

    @Override
    public SyncData getChanges(int rev) throws SyncException {
        LOGGER.warn("--------------------------------------   Proxy called - get chages");

        threadId = "1";

        try {
            List<NameValuePair> data = newArrayList();
            data.add(new BasicNameValuePair(THREAD_ID.name(), threadId));
            data.add(new BasicNameValuePair(ACTION.name(), SyncAction.GET_CHANGES.getStringName()));
            data.add(new BasicNameValuePair(REVISION.name(), String.valueOf(rev)));
            String serializedResponse = request(data);
            return serializationAdapter.deserializeMaxRevisionAndChangeList(serializedResponse);
        } catch (SerializationException e) {
            throw new SyncException(read(Errors.CANT_GET_CHANGES_SERIALIZATION_FAILURE), e);
        }
    }

    @Override
    public Schema getSchema() throws SyncException {

        threadId = "1";

        try {
            List<NameValuePair> data = newArrayList();
            data.add(new BasicNameValuePair(THREAD_ID.name(), threadId));
            data.add(new BasicNameValuePair(ACTION.name(), SyncAction.GET_SCHEMA.getStringName()));
            String serializedResponse = request(data);
            return serializationAdapter.deserializeSchema(serializedResponse);
        } catch (SerializationException e) {
            throw new SyncException(read(Errors.CANT_GET_SCHEMA_SERIALIZATION_FAILURE), e);
        }
    }

    /**
     * Sends the http Request to server and returns its response as a JSON String.
     *
     * @param data Data to send
     * @return Server response as a JSON String
     * @throws SyncException When synchronization fails.
     */
    private String request(List<NameValuePair> data) throws SyncException {

        StringBuilder sb = new StringBuilder("");
        HttpPost post;
        DefaultHttpClient client;
        HttpResponse response;
        BufferedReader in = null;
        ObjectInputStream oIn = null;

        try {

            client = new DefaultHttpClient();

            if (null != credentials) {
                client.getCredentialsProvider().setCredentials(AuthScope.ANY,
                    credentials);
            }

            post = new HttpPost(host);
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data);
            post.setEntity(entity);

            response = client.execute(post);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {

                LOGGER.info(Infos.HEADER_WITH_SERVER_EXCEPTION, response.getFirstHeader(HEADER_NAME_SERVER_EXCEPTION));
                Header headerException = response.getFirstHeader(HEADER_NAME_SERVER_EXCEPTION);

                if (headerException == null) {


                    throw new SyncException(read(Errors.SERVER_EXCEPTION_RECEIVED,
                        response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
                } else {
                    ServerStatus serverStatus = ServerStatus.fromCode(Integer.parseInt(headerException.getValue()));
                    LOGGER.info(Infos.SERVER_STATUS_CODE, serverStatus);
                    throw new ServerStatusException(serverStatus, read(Errors.SERVER_EXCEPTION_RECEIVED,
                        response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
                }
            }

            Header serverException = response.getFirstHeader(HEADER_NAME_EXCEPTION);
            if (serverException != null) {
                boolean isException = Boolean.valueOf(serverException.getValue());
                if (isException) {
                    oIn = new ObjectInputStream(response.getEntity().getContent());

                    SyncException exception = (SyncException) oIn.readObject();
                    LOGGER.error(Errors.SERVER_EXCEPTION_RECEIVED, exception);
                    throw exception;
                }
            } else {
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line;

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
            }

        } catch (ClientProtocolException e) {
            throw new SyncException(e.getLocalizedMessage(), e);
        } catch (IllegalStateException e) {
            throw new SyncException(e.getLocalizedMessage(), e);
        } catch (IOException e) {
            throw new SyncException(e.getLocalizedMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new SyncException(e.getLocalizedMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new SyncException(e.getLocalizedMessage(), e);
                }
            }

            if (oIn != null) {
                try {
                    oIn.close();
                } catch (IOException e) {
                    throw new SyncException(e.getLocalizedMessage(), e);
                }
            }
        }
        String decodedResponse;
        try {
            decodedResponse = URLDecoder.decode(sb.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new SyncException(e.getLocalizedMessage(), e);
        }
        LOGGER.debug("Server response size (in bytes): {}", decodedResponse.getBytes());
        return decodedResponse;
    }
}
