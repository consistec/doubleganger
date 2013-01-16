package de.consistec.syncframework.impl.proxy.http_servlet;

import static de.consistec.syncframework.common.util.CollectionsUtil.newArrayList;
import static de.consistec.syncframework.common.util.CollectionsUtil.newHashMap;
import static de.consistec.syncframework.impl.proxy.http_servlet.SyncAction.APPLY_CHANGES;
import static de.consistec.syncframework.impl.proxy.http_servlet.SyncAction.GET_CHANGES;
import static de.consistec.syncframework.impl.proxy.http_servlet.SyncAction.GET_SCHEMA;
import static de.consistec.syncframework.impl.proxy.http_servlet.SyncRequestHttpParams.ACTION;
import static de.consistec.syncframework.impl.proxy.http_servlet.SyncRequestHttpParams.CHANGES;
import static de.consistec.syncframework.impl.proxy.http_servlet.SyncRequestHttpParams.REVISION;
import static org.junit.Assert.assertEquals;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.TestUtil;
import de.consistec.syncframework.common.Tuple;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.MDEntry;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SerializationException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.server.IServerSyncProvider;
import de.consistec.syncframework.impl.adapter.GenericDatabaseAdapter;
import de.consistec.syncframework.impl.adapter.JSONSerializationAdapter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

/**
 * Test for servlet processor.
 * <b style="color: red;">Warning!</b>
 * <br/> If you launch this test directly from your IDE and it fails with message similar to <br/>
 * <code>Absent Code attribute in method that is not native or abstract in class file javax/servlet/http/Cookie</code><br/>
 * make sure the
 * <code>javax:javaee-web-api</code> dependency is specified under the
 * <code>org.glassfish:javax.servlet</code> in pom file.
 * <br/>
 * <ul> <li><b>Company:</b> consistec Engineering and Consulting GmbH</li>
 * <li>Date:</li> 23.07.12 11:38 </ul>
 * <p/>
 *
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class ServletProcessorTest {

    private static final int MOCK_LENGTH = 1337;
    private static final String TEST_TABLE_NAME = "TestTable";
    private static final String TEST_COLUMN1 = "TestColumn1";
    private static final String TEST_COLUMN2 = "TestColumn2";
    private static final String TEST_MDV = "7686876786sd9876786876";

    @BeforeClass
    public static void setUpClass() throws IOException {
        Config config = Config.getInstance();
        // preparing framework to use sqlite database.
        config.setClientDatabaseAdapter(de.consistec.syncframework.impl.adapter.GenericDatabaseAdapter.class);
        config.setServerDatabaseAdapter(de.consistec.syncframework.impl.adapter.GenericDatabaseAdapter.class);
        config.getClientDatabaseProperties().put(GenericDatabaseAdapter.PROPS_DRIVER_NAME, "org.sqlite.JDBC");
        config.getClientDatabaseProperties().put(GenericDatabaseAdapter.PROPS_URL, "jdbc:sqlite:target/client.sqlite");
        config.getServerDatabaseProperties().put(GenericDatabaseAdapter.PROPS_DRIVER_NAME, "org.sqlite.JDBC");
        config.getServerDatabaseProperties().put(GenericDatabaseAdapter.PROPS_URL, "jdbc:sqlite:target/server.sqlite");
    }

    @Test
    public void testGetSchema() throws IOException, SerializationException, SyncException, DatabaseAdapterException,
        ContextException, IllegalArgumentException, IllegalAccessException {

        HttpServletRequest requestMock = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse responseMock = Mockito.mock(HttpServletResponse.class);
        IServerSyncProvider providerMock = Mockito.mock(IServerSyncProvider.class);
        Schema schema = TestUtil.getSchema();
        Mockito.when(requestMock.getParameter(ACTION.name())).thenReturn(GET_SCHEMA.getStringName());
        Mockito.when(requestMock.getContentLength()).thenReturn(MOCK_LENGTH);
        Mockito.when(providerMock.getSchema()).thenReturn(schema);

        StringWriter writer = new StringWriter();
        Mockito.when(responseMock.getWriter()).thenReturn(new PrintWriter(writer));

        HttpServletProcessor processor = new HttpServletProcessor();
        Field contextField = Whitebox.getField(HttpServletProcessor.class, "serverContext");
        Whitebox.setInternalState(contextField.get(processor), IServerSyncProvider.class, providerMock);
        processor.execute(requestMock, responseMock);

        JSONSerializationAdapter adapter = new JSONSerializationAdapter();
        String decodedResponse = URLDecoder.decode(writer.toString(), "UTF-8");
        Schema s = adapter.deserializeSchema(decodedResponse);
        assertEquals(schema, s);
    }

    @Test
    public void testGetChanges() throws IOException, SerializationException, SyncException, DatabaseAdapterException,
        ContextException, IllegalArgumentException, IllegalAccessException {

        HttpServletRequest requestMock = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse responseMock = Mockito.mock(HttpServletResponse.class);
        IServerSyncProvider providerMock = Mockito.mock(IServerSyncProvider.class);
        List<Change> expectedChangeList = newArrayList();
        Tuple<Integer, List<Change>> expectedChangeListTuple = new Tuple<Integer, List<Change>>(0, expectedChangeList);

        MDEntry entryOne = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);
        Map<String, Object> rowDataOne = newHashMap();
        rowDataOne.put(TEST_COLUMN1, 1);
        rowDataOne.put(TEST_COLUMN2, new Date(System.currentTimeMillis()));
        expectedChangeList.add(new Change(entryOne, rowDataOne));

        Mockito.when(requestMock.getParameter(ACTION.name())).thenReturn(GET_CHANGES.getStringName());
        Mockito.when(requestMock.getParameter(REVISION.name())).thenReturn("1");
        Mockito.when(requestMock.getContentLength()).thenReturn(MOCK_LENGTH);

        Mockito.when(providerMock.getChanges(1)).thenReturn(expectedChangeListTuple);

        StringWriter writer = new StringWriter();
        Mockito.when(responseMock.getWriter()).thenReturn(new PrintWriter(writer));

        HttpServletProcessor processor = new HttpServletProcessor();
        Field contextField = Whitebox.getField(HttpServletProcessor.class, "serverContext");
        Whitebox.setInternalState(contextField.get(processor), IServerSyncProvider.class, providerMock);
        processor.execute(requestMock, responseMock);

        JSONSerializationAdapter adapter = new JSONSerializationAdapter();
        String decodedResponse = URLDecoder.decode(writer.toString(), "UTF-8");
        Tuple<Integer, List<Change>> s = adapter.deserializeMaxRevisionAndChangeList(decodedResponse);
        assertEquals(expectedChangeList, s.getValue2());
        assertEquals(0, s.getValue1().intValue());
    }

    @Test
    public void testApplyChanges() throws SyncException, IOException, SerializationException, DatabaseAdapterException,
        ContextException, IllegalArgumentException, IllegalAccessException {

        HttpServletRequest requestMock = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse responseMock = Mockito.mock(HttpServletResponse.class);
        IServerSyncProvider providerMock = Mockito.mock(IServerSyncProvider.class);
        List<Change> changeList = newArrayList();

        MDEntry entryOne = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);
        Map<String, Object> rowDataOne = newHashMap();
        rowDataOne.put(TEST_COLUMN1, 1);
        rowDataOne.put(TEST_COLUMN2, new Date(System.currentTimeMillis()));
        changeList.add(new Change(entryOne, rowDataOne));

        JSONSerializationAdapter adapter = new JSONSerializationAdapter();

        Mockito.when(requestMock.getParameter(ACTION.name())).thenReturn(APPLY_CHANGES.getStringName());
        Mockito.when(requestMock.getParameter(CHANGES.name())).thenReturn(adapter.serializeChangeList(changeList));
        Mockito.when(requestMock.getParameter(REVISION.name())).thenReturn("1");
        Mockito.when(providerMock.applyChanges(changeList, 1)).thenReturn(1);
        Mockito.when(requestMock.getContentLength()).thenReturn(MOCK_LENGTH);
        StringWriter writer = new StringWriter();
        Mockito.when(responseMock.getWriter()).thenReturn(new PrintWriter(writer));

        HttpServletProcessor processor = new HttpServletProcessor();
        Field contextField = Whitebox.getField(HttpServletProcessor.class, "serverContext");
        Whitebox.setInternalState(contextField.get(processor), IServerSyncProvider.class, providerMock);
        processor.execute(requestMock, responseMock);

        assertEquals(1, Integer.parseInt(writer.toString()));
    }
}
