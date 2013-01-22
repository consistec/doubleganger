package de.consistec.syncframework.impl.adapter.it_postgres;

import static de.consistec.syncframework.common.i18n.MessageReader.read;
import static de.consistec.syncframework.common.server.ServerStatus.CLIENT_NOT_UPTODATE;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.SyncData;
import de.consistec.syncframework.common.client.IClientSyncProvider;
import de.consistec.syncframework.common.client.SyncAgent;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.ServerStatusException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.i18n.Errors;
import de.consistec.syncframework.common.server.IServerSyncProvider;
import de.consistec.syncframework.impl.TestDatabase;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 21.01.13 10:53
 */
public class RepeatSyncTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RepeatSyncTest.class.getCanonicalName());

//    protected static String[] tableNames = new String[]{"categories", "categories_md", "items", "items_md"};
//
//    protected static String[] createQueries = new String[]{
//        "CREATE TABLE categories (categoryid INTEGER NOT NULL PRIMARY KEY ,categoryname VARCHAR (300),description VARCHAR (300));",
//        "CREATE TABLE categories_md (pk INTEGER NOT NULL PRIMARY KEY, mdv VARCHAR (300), rev INTEGER DEFAULT 1, f INTEGER DEFAULT 0);",
//        "CREATE TABLE items (id INTEGER NOT NULL PRIMARY KEY ,name VARCHAR (300),description VARCHAR (300));",
//        "CREATE TABLE items_md (pk INTEGER NOT NULL PRIMARY KEY, mdv VARCHAR (300), rev INTEGER DEFAULT 1, f INTEGER DEFAULT 0);",
//        "INSERT INTO categories (categoryid, categoryname, description) VALUES (1, 'Beverages', 'Soft drinks')",
//        "INSERT INTO categories (categoryid, categoryname, description) VALUES (2, 'Condiments', 'Sweet and ')",
//        "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (1, '8F3CCBD3FE5C9106253D472F6E36F0E1', 1, 0)",
//        "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (1, '75901F57520C09EB990837C7AA93F717', 2, 0)",};
//
//    protected static String updateRow2b = "UPDATE categories SET categoryname = 'Cat2b', description = '2b' WHERE categoryid = 2";

//    protected static String[] insertServerQuery = new String[]{
//        "INSERT INTO categories (categoryid, categoryname, description) VALUES (3, 'Beverages', 'Soft drinks')",
//        "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (2, '8F3CCBD3FE5C9106253D472F6E36F0E1', 1, 0)",};
//
//    protected static String[] insertClientQuery = new String[]{
//        "INSERT INTO categories (categoryid, categoryname, description) VALUES (3, 'Beverages', 'Soft drinks')",
//        "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (2, '75901F57520C09EB990837C7AA93F717', 1, 0)",};

    @Mock
    private IServerSyncProvider serverSyncProviderMock;

    @Mock
    private IClientSyncProvider clientSyncProviderMock;

    private TestDatabase db;

    public RepeatSyncTest() {
        db = new PostgresDatabase();
    }

    @BeforeClass
    public static void setUpClass() {
        // initialize logging framework
        DOMConfigurator.configure(ClassLoader.getSystemResource("log4j.xml"));
    }

    @Before
    public void setUp() throws IOException, SQLException {
        Config.getInstance().init(getClass().getResourceAsStream(db.getConfigFile()));

        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = ServerStatusException.class)
    public void repeatSyncDueToClientNotUptoDate() throws ContextException, SyncException {

        when(this.serverSyncProviderMock.getChanges(anyInt())).thenReturn(new SyncData(1, new ArrayList<Change>()));
        // throw exception to repeat the sync
        when(this.serverSyncProviderMock.applyChanges((SyncData) anyObject())).thenThrow(new ServerStatusException(
            CLIENT_NOT_UPTODATE, read(Errors.COMMON_UPDATE_NECESSARY)));

        SyncAgent agent = new SyncAgent(this.serverSyncProviderMock, this.clientSyncProviderMock);
        agent.synchronize();
    }
}
