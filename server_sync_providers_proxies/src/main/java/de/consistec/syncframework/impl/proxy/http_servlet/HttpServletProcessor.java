package de.consistec.syncframework.impl.proxy.http_servlet;

import static de.consistec.syncframework.common.i18n.MessageReader.read;
import static de.consistec.syncframework.impl.proxy.http_servlet.SyncRequestHttpParams.ACTION;
import static de.consistec.syncframework.impl.proxy.http_servlet.SyncRequestHttpParams.CHANGES;
import static de.consistec.syncframework.impl.proxy.http_servlet.SyncRequestHttpParams.REVISION;
import static de.consistec.syncframework.impl.proxy.http_servlet.SyncRequestHttpParams.THREAD_ID;

import de.consistec.syncframework.common.SyncContext;
import de.consistec.syncframework.common.Tuple;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SerializationException;
import de.consistec.syncframework.common.exception.ServerStatusException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.server.ServerStatus;
import de.consistec.syncframework.common.util.LoggingUtil;
import de.consistec.syncframework.common.util.StringUtil;
import de.consistec.syncframework.impl.adapter.ISerializationAdapter;
import de.consistec.syncframework.impl.adapter.JSONSerializationAdapter;
import de.consistec.syncframework.impl.i18n.Errors;
import de.consistec.syncframework.impl.i18n.Infos;
import de.consistec.syncframework.impl.i18n.Warnings;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.slf4j.MDC;
import org.slf4j.cal10n.LocLogger;

/**
 * Servlet processor takes received http servlet request and pass the control farther to server sync provider.
 * <p/>
 * In it {@link #execute() } method, the requests parameters are read, parsed and then appropriate method of
 * ServerContext is invoked. The result is parsed JSON String, append to servlet response object and returned to client.
 *
 * @author Markus Backes
 * @company Consistec Engineering and Consulting GmbH
 * @date 23.07.12 13:15
 * @since 0.0.1-SNAPSHOT
 */
public class HttpServletProcessor {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private static final LocLogger LOGGER = LoggingUtil.createLogger(HttpServletProcessor.class.getCanonicalName());
    private ISerializationAdapter serializationAdapter;
    private final SyncContext.ServerContext serverContext;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Class constructor " >

    /**
     * Creates new instance of servlet processor.
     * <p/>
     * This instance will be using
     * {@link de.consistec.syncframework.common.SyncContext.ServerContext synchronization context}
     * with it's internal database connection.
     *
     * @throws ContextException
     */
    public HttpServletProcessor() throws ContextException {
        serverContext = SyncContext.server();
        serializationAdapter = new JSONSerializationAdapter();
    }

    /**
     * Creates new instance of servlet processor.
     * <p/>
     * This instance will be using
     * {@link de.consistec.syncframework.common.SyncContext.ServerContext synchronization context}
     * with provided database connection (e.g. from server pool).
     *
     * @param ds External sql data source.
     * @throws IOException
     * @throws ContextException
     */
    public HttpServletProcessor(DataSource ds) throws ContextException {
        serverContext = SyncContext.server(ds);
        serializationAdapter = new JSONSerializationAdapter();
    }

    //</editor-fold>
    //<editor-fold defaultstate="expanded" desc=" Class fmethods " >

    /**
     * Parses the request, invokes
     * {@link de.consistec.syncframework.common.SyncContext.ServerContext synchronization context}
     * and returns http response to client.
     *
     * @param req Http request received from client.
     * @param resp Http response from invoking servlet.
     * @throws java.io.IOException Signals that an exception has occurred while getting the response writer.
     * @throws DatabaseAdapterException
     * @throws SerializationException
     */
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DatabaseAdapterException,
        SerializationException {

        if (!StringUtil.isNullOrEmpty(req.getParameter(THREAD_ID.name()))) {
            MDC.put("thread-id", req.getParameter(THREAD_ID.name()));
        }

        if (!StringUtil.isNullOrEmpty(req.getParameter(ACTION.name()))) {

            SyncAction action = SyncAction.fromStringName(req.getParameter(ACTION.name()));

            if (action != null) {

                switch (action) {
                    case GET_SCHEMA:
                        executeGetSchema(resp);
                        break;
                    case GET_CHANGES:
                        executeGetChanges(req, resp);
                        break;

                    case APPLY_CHANGES:
                        executeApplyChanges(req, resp);
                        break;
                    default:
                        throw new UnsupportedOperationException(read(Errors.SERVER_UNSUPPORTED_ACTION));
                }
            }
        }
    }

    private void executeApplyChanges(HttpServletRequest req, HttpServletResponse resp) throws IOException,
        SerializationException {

        final String changes = req.getParameter(CHANGES.name());
        final String revision = req.getParameter(REVISION.name());

        if (!StringUtil.isNullOrEmpty(changes) && !StringUtil.isNullOrEmpty(revision)) {
            try {
                final int clientRevision = Integer.valueOf(revision);

                List<Change> deserializedChanges = serializationAdapter.deserializeChangeList(
                    changes);
                LOGGER.debug("deserialized Changes:");
                LOGGER.debug("<{}>", deserializedChanges);
                int nextServerRevisionSendToClient = serverContext.applyChanges(deserializedChanges, clientRevision);
                LOGGER.info(Infos.NEW_SERVER_REVISION, nextServerRevisionSendToClient);

                resp.getWriter().print(String.valueOf(nextServerRevisionSendToClient));
                resp.getWriter().flush();
            } catch (SyncException e) {
                if (e instanceof ServerStatusException) {
                    ServerStatusException ex = (ServerStatusException) e;
                    if (ex.getStatus().equals(ServerStatus.CLIENT_NOT_UPTODATE)) {
                        LOGGER.warn(read(Warnings.CANT_APPLY_CHANGES_CLIENT_NOT_UP_TO_DATE));
                        resp.addHeader(HttpServerSyncProxy.HEADER_NAME_SERVER_EXCEPTION,
                            String.valueOf(ServerStatus.CLIENT_NOT_UPTODATE.getCode()));
                    } else {
                        LOGGER.warn(read(Errors.CANT_APPLY_CHANGES), e);
                    }
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
                }
            }
        }
    }

    private void executeGetChanges(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            final String tmpClientRev = req.getParameter(REVISION.name());
            Tuple<Integer, List<Change>> changesTuple;

            if (!StringUtil.isNullOrEmpty(tmpClientRev)) {

                try {
                    changesTuple = serverContext.getChanges(Integer.parseInt(tmpClientRev));
                } catch (NumberFormatException ex) {
                    LOGGER.error(read(Errors.CANT_PARSE_CLIENT_REVISION), ex);
                    throw new SyncException(ex.getLocalizedMessage(), ex);
                }
            } else {
                LOGGER.error(Errors.CANT_GETCHANGES_NO_CLIENT_REVISION);
                throw new SyncException(read(Errors.CANT_GETCHANGES_NO_CLIENT_REVISION));
            }

            resp.getWriter().print(serializationAdapter.serializeChangeList(changesTuple).toString());
            resp.getWriter().flush();

        } catch (SyncException e) {
            LOGGER.error(read(Errors.CANT_GET_SERVER_CHANGES), e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.error(read(Errors.CANT_GET_SERVER_CHANGES), e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        } catch (SerializationException e) {
            LOGGER.error(read(Errors.CANT_GET_SERVER_CHANGES), e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        }
    }

    private void executeGetSchema(HttpServletResponse resp) throws IOException {
        try {
            resp.getWriter().print(
                serializationAdapter.serializeSchema(serverContext.getSchema()).toString());
            resp.getWriter().flush();
        } catch (SyncException e) {
            LOGGER.error(read(Errors.CANT_GET_CREATE_DB_SCHEMA), e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        } catch (SerializationException e) {
            LOGGER.error(read(Errors.CANT_GET_CREATE_DB_SCHEMA), e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.error(read(Errors.CANT_GET_CREATE_DB_SCHEMA), e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        }

    }
    //</editor-fold>
}
