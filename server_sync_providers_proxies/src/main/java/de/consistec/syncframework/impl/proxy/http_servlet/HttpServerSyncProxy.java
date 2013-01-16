package de.consistec.syncframework.impl.proxy.http_servlet;

import static de.consistec.syncframework.common.i18n.MessageReader.read;
import static de.consistec.syncframework.common.util.CollectionsUtil.newArrayList;
import static de.consistec.syncframework.impl.proxy.http_servlet.SyncRequestHttpParams.ACTION;
import static de.consistec.syncframework.impl.proxy.http_servlet.SyncRequestHttpParams.CHANGES;
import static de.consistec.syncframework.impl.proxy.http_servlet.SyncRequestHttpParams.REVISION;
import static de.consistec.syncframework.impl.proxy.http_servlet.SyncRequestHttpParams.SETTINGS;
import static de.consistec.syncframework.impl.proxy.http_servlet.SyncRequestHttpParams.THREAD_ID;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.SyncSettings;
import de.consistec.syncframework.common.Tuple;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.exception.SerializationException;
import de.consistec.syncframework.common.exception.ServerStatusException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.server.IServerSyncProvider;
import de.consistec.syncframework.common.server.ServerStatus;
import de.consistec.syncframework.common.util.LoggingUtil;
import de.consistec.syncframework.common.util.StringUtil;
import de.consistec.syncframework.impl.adapter.ISerializationAdapter;
import de.consistec.syncframework.impl.adapter.JSONSerializationAdapter;
import de.consistec.syncframework.impl.i18n.Errors;
import de.consistec.syncframework.impl.i18n.Infos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
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
 * has to be specified in framework configuration. See {@link de.consistec.syncframework.common.Config Config class}.
 *
 * @author Markus Backes
 * @version 0.0.1-SNAPSHOT
 * @company Consistec Engineering and Consulting GmbH
 * @date 12.04.12 11:30
 */
public class HttpServerSyncProxy implements IServerSyncProvider {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    /**
     * Header name in which server exception message will be stored.
     */
    protected static final String HEADER_NAME_SERVER_EXCEPTION = "server_exception";
    private static final String PROPS_SERVER_URL = "url";
    private static final String PROPS_USERNAME = "username";
    private static final String PROPS_PASSWORD = "password";
    private static final LocLogger LOGGER = LoggingUtil.createLogger(HttpServerSyncProxy.class.getCanonicalName());
    private URI host;
    private Credentials credentials;
    private ISerializationAdapter serializationAdapter;
    //    private long threadId;
    private String threadId;

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Class constructors " >

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

    //</editor-fold>
    //<editor-fold defaultstate="expanded" desc=" Class methods " >
    private void initializeDefaultSerializationAdapter() {
        this.serializationAdapter = new JSONSerializationAdapter();
    }

    @Override
    public void validateClientSettings(final SyncSettings clientSettings) throws SyncException {
        try {
            List<NameValuePair> data = newArrayList();
            data.add(new BasicNameValuePair(THREAD_ID.name(), threadId));
            data.add(new BasicNameValuePair(SETTINGS.name(),
                serializationAdapter.serializeSettings(clientSettings).toString()));

            request(data);
        } catch (SerializationException e) {
            throw new SyncException(read(Errors.CANT_APPLY_CHANGES_SERIALIZATION_FAILURE), e);
        }
    }

    @Override
    public int applyChanges(List<Change> changes, int clientRevision) throws SyncException {

//        threadId = Thread.currentThread().getId();
        threadId = ManagementFactory.getRuntimeMXBean().getName();

        try {
            List<NameValuePair> data = newArrayList();
            data.add(new BasicNameValuePair(THREAD_ID.name(), threadId));
            data.add(new BasicNameValuePair(ACTION.name(), SyncAction.APPLY_CHANGES.getStringName()));
            data.add(new BasicNameValuePair(CHANGES.name(),
                serializationAdapter.serializeChangeList(changes).toString()));
            data.add(new BasicNameValuePair(REVISION.name(), String.valueOf(clientRevision)));
            return Integer.parseInt(request(data));
        } catch (SerializationException e) {
            throw new SyncException(read(Errors.CANT_APPLY_CHANGES_SERIALIZATION_FAILURE), e);
        }
    }

    @Override
    public Tuple<Integer, List<Change>> getChanges(int rev) throws SyncException {
        LOGGER.warn("--------------------------------------   Proxy called - get chages");

//        threadId = Thread.currentThread().getId();
        threadId = ManagementFactory.getRuntimeMXBean().getName();

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

//        threadId = Thread.currentThread().getId();
        threadId = ManagementFactory.getRuntimeMXBean().getName();

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
     * Do nothing.
     */
//    @Override
//    public void close() {
//        LOGGER.debug("Close called");
//    }

    /**
     * Sends the http Request to server and returns its response as a JSON String.
     *
     * @param data Data to send
     * @return Server response as a JSON String
     * @throws SyncException When synchronization fails.
     */
    private String request(List<NameValuePair> data) throws SyncException {

        StringBuilder sb;
        HttpPost post;
        DefaultHttpClient client;
        HttpResponse response;
        BufferedReader in = null;

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
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            sb = new StringBuilder("");
            String line;

            while ((line = in.readLine()) != null) {
                sb.append(line);
            }

        } catch (ClientProtocolException e) {
            throw new SyncException(e.getLocalizedMessage(), e);
        } catch (IllegalStateException e) {
            throw new SyncException(e.getLocalizedMessage(), e);
        } catch (IOException e) {
            throw new SyncException(e.getLocalizedMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
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
    //</editor-fold>
}
