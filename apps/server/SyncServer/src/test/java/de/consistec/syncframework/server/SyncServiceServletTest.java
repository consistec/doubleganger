package de.consistec.syncframework.server;

import static de.consistec.syncframework.common.util.CollectionsUtil.newArrayList;
import static de.consistec.syncframework.common.util.CollectionsUtil.newHashMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.startsWith;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.SyncData;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.MDEntry;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.syncframework.common.i18n.MessageReader;
import de.consistec.syncframework.impl.commands.GetChangesCommand;
import de.consistec.syncframework.impl.i18n.DBAdapterErrors;
import de.consistec.syncframework.impl.proxy.http_servlet.HttpServerSyncProxy;
import de.consistec.syncframework.impl.proxy.http_servlet.SyncAction;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpException;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 24.01.13 10:27
 */
public class SyncServiceServletTest {

    private static final String TEST_TABLE_NAME = "TestTable";
    private static final String TEST_COLUMN1 = "TestColumn1";
    private static final String TEST_COLUMN2 = "TestColumn2";
    private static final String TEST_MDV = "7686876786sd9876786876";

    private static EmbeddedSyncServiceServer server;

    private HttpServerSyncProxy proxy;


    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @BeforeClass
    public static void setUpClass() throws Exception {

        server = new EmbeddedSyncServiceServer();
        server.init();
        server.start();
    }

    /**
     * Stops the Jetty container.
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
        server.stop();
    }

    @Before
    public void setUp() throws IOException, URISyntaxException {
        // initialize logging framework
        DOMConfigurator.configure(ClassLoader.getSystemResource("log4j.xml"));

        proxy = new HttpServerSyncProxy(server.getServerURI());
    }

    @Test
    public void testEncodingDecoding() throws HttpException, SyncException, URISyntaxException {

        List<Change> expectedChangeList = newArrayList();
        SyncData expectedSyncData = new SyncData(0, expectedChangeList);

        MDEntry entryOne = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);
        Map<String, Object> rowDataOne = newHashMap();
        rowDataOne.put(TEST_COLUMN1, 1);
        rowDataOne.put(TEST_COLUMN2, "ääüüöö");
        expectedChangeList.add(new Change(entryOne, rowDataOne));

        server.addRequest(SyncAction.GET_CHANGES, new GetChangesCommandMock(expectedSyncData));

        SyncData retrievedSyncData = proxy.getChanges(0);

        String expectedString = expectedSyncData.getChanges().get(0).getRowData().get(TEST_COLUMN2).toString();
        String retrievedString = retrievedSyncData.getChanges().get(0).getRowData().get(TEST_COLUMN2).toString();

        assertEquals(expectedString, retrievedString);
        assertEquals("ääüüöö", retrievedString);
    }

    @Test
    public void testExceptionSerialization() throws Throwable {
        thrown.expect(DatabaseAdapterInstantiationException.class);
        thrown.expectMessage(startsWith(MessageReader.read(DBAdapterErrors.CANT_CREATE_ADAPTER_INSTANCE)));

        // use original command to try to get db connection
        server.addRequest(SyncAction.GET_CHANGES, new GetChangesCommand());

        // provocate instantiation exception during db adapter instantiation
        Config.getInstance().getServerDatabaseProperties().setProperty("host", "localhost_failed");

        try {
            // should provoke SyncException with DatabaseAdapterInstantiationException cause from server
            proxy.getChanges(0);
        } catch (SyncException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testExceptionMessageSending() throws Throwable {
        thrown.expect(SyncException.class);
        thrown.expectMessage(startsWith(MessageReader.read(DBAdapterErrors.CANT_CREATE_ADAPTER_INSTANCE)));

        // use original command to try to get db connection
        server.addRequest(SyncAction.GET_CHANGES, new GetChangesCommand());

        // debug is default to true configured in server.properties file
        server.setDebugEnabled(false);

        // provocates instantiation exception during db adapter instantiation
        Config.getInstance().getServerDatabaseProperties().setProperty("host", "localhost_failed");
        // should provoke SyncException from client with no cause
        proxy.getChanges(0);
        // reset property
        Config.getInstance().getServerDatabaseProperties().setProperty("host", "localhost");
    }
}
