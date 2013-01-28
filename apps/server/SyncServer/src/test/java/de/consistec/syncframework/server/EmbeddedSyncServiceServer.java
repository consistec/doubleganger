package de.consistec.syncframework.server;

import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.impl.commands.RequestCommand;
import de.consistec.syncframework.impl.proxy.http_servlet.SyncAction;

import java.net.URI;
import java.net.URISyntaxException;
import org.mortbay.jetty.testing.ServletTester;

/**
 * This represents an instance of the Jetty
 * servlet container so that we can start and stop it.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 25.01.13 13:21
 */
public class EmbeddedSyncServiceServer {

    public static final String CONFIG_FILE = "/test_syncframework.properties";


    private static ServletTester tester;
    private static String baseUrl;
    private boolean debugEnabled;
    private ContextListenerMock listener;

    public void init() throws Exception {
        tester = new ServletTester();
        tester.setContextPath("/");
//        tester.setResourceBase("./apps/server/SyncServer/src/test/resources/");
        tester.setResourceBase("./target/test-classes/server-tests");
        System.out.println("+++++++++++++++++++++++");
        System.out.println(tester.getResourceBase());
        System.out.println("+++++++++++++++++++++++");
        listener = new ContextListenerMock(CONFIG_FILE);
//        tester.setAttribute("debug.listener", listener);
        tester.addEventListener(listener);
        tester.addServlet(SyncServiceServlet.class, "/SyncServer/SyncService");
        baseUrl = tester.createSocketConnector(true);
    }

    public void start() throws Exception {
        tester.start();
    }

    public void stop() throws Exception {
        tester.stop();
    }

    public URI getServerURI() throws URISyntaxException {
        return new URI(baseUrl + "/SyncServer/SyncService");
    }

    public void addRequest(SyncAction action, RequestCommand request) {
        listener.addRequest(action, request);
    }

    public void setDebugEnabled(final boolean debugEnabled) {
        listener.setDebugEnabled(debugEnabled);
    }

    public void setTableSyncStrategies(TableSyncStrategies tableSyncStrategies) {
        listener.setTableSyncStrategies(tableSyncStrategies);
    }
}
