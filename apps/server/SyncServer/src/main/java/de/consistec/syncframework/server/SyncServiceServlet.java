package de.consistec.syncframework.server;

/*
 * #%L
 * doppelganger
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

import de.consistec.syncframework.common.exception.SerializationException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.impl.proxy.http_servlet.HttpServletProcessor;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * This Servlet provides the service functionality for the ServerSyncProviderProxy
 * <p/>
 * <ul>
 * <li><b>Company:</b> consistec Engineering and Consulting GmbH</li>
 * </ul>
 * <p/>
 *
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class SyncServiceServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncServiceServlet.class.getCanonicalName());

    private static AtomicInteger requestNumber = new AtomicInteger(1);

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     * <p/>
     *
     * @param req Servlet request
     * @param resp Servlet response
     * @throws ServletException if a Servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        try {

            HttpSession session = req.getSession();
            requestNumber.compareAndSet(1000, 1);
            int requestId = requestNumber.getAndIncrement();
            MDC.put("session-id", Integer.valueOf(requestId).toString());

            ServletContext ctx = session.getServletContext();
            HttpServletProcessor processor = (HttpServletProcessor) ctx.getAttribute(
                ContextListener.HTTP_PROCESSOR_CTX_ATTR);
            processor.execute(req, resp);

        } catch (DatabaseAdapterException ex) {
            throw new ServletException(ex);
        } catch (SerializationException ex) {
            throw new ServletException(ex);
        } finally {
            req.getSession().invalidate();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods.">

    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        processRequest(req, resp);
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    public String getServletInfo() {
        return "Syncronisation Servlet";
    }

    private static class UniqueThreadIdGenerator {

        private static final AtomicInteger UNIQUE_ID = new AtomicInteger(0);

        private static final ThreadLocal<Integer> UNIQUE_NUM =
            new ThreadLocal<Integer>() {
                @Override
                protected Integer initialValue() {
                    return UNIQUE_ID.getAndIncrement();
                }
            };

        public static int getCurrentThreadId() {
            return UNIQUE_ID.get();
        }
    } // UniqueThreadIdGenerator
    //</editor-fold>
}
