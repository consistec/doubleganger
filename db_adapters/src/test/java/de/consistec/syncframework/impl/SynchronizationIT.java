package de.consistec.syncframework.impl;

import static de.consistec.syncframework.common.SyncDirection.BIDIRECTIONAL;
import static de.consistec.syncframework.common.SyncDirection.CLIENT_TO_SERVER;
import static de.consistec.syncframework.common.SyncDirection.SERVER_TO_CLIENT;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.CLIENT_WINS;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.FIRE_EVENT;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.SERVER_WINS;
import static de.consistec.syncframework.common.i18n.Errors.COMMON_NO_CLIENTCHANGES_ALLOWED_TO_SYNC_FOR_TABLE;
import static de.consistec.syncframework.common.i18n.Errors.NOT_SUPPORTED_CONFLICT_STRATEGY;
import static de.consistec.syncframework.impl.adapter.ConnectionType.CLIENT;
import static de.consistec.syncframework.impl.adapter.ConnectionType.SERVER;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.IConflictListener;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author davidm
 * @company Consistec Engineering and Consulting GmbH
 * @date 10.01.2013 15:41:50
 */
@RunWith(value = Parameterized.class)
public class SynchronizationIT {

    protected static final Logger LOGGER = LoggerFactory.getLogger(SynchronizationIT.class.getCanonicalName());
    protected static final Config CONF = Config.getInstance();
    protected TestDatabase db;
    protected TestScenario scenario;
    protected static String[] tableNames = new String[]{"categories", "categories_md", "items", "items_md"};
    protected static String[] createQueries = new String[]{
        "CREATE TABLE categories (id INTEGER NOT NULL PRIMARY KEY, name VARCHAR (300),description VARCHAR (300));",
        "CREATE TABLE items (id INTEGER NOT NULL PRIMARY KEY, name VARCHAR (300),description VARCHAR (300));"};
    protected static String[] insertQueries = new String[]{
        "INSERT INTO categories (id, name, description) VALUES (1, 'Beverages', 'Soft drinks')",
        "INSERT INTO categories (id, name, description) VALUES (2, 'Condiments', 'Sweet and ')"};
    protected static String deleteRow2 = "DELETE FROM categories WHERE id = 2";
    protected static String updateRow2b = "UPDATE categories SET name = 'Cat2b', description = '2b' WHERE id = 2";
    protected static String updateRow2c = "UPDATE categories SET name = 'Cat2c', description = '2c' WHERE id = 2";
    protected static String insertRow3a = "INSERT INTO categories (id, name, description) VALUES (3, 'Cat3a', '3a')";
    protected static String updateRow3b = "UPDATE categories SET name = 'Cat3b', description = '3b' WHERE id = 3";
    protected static String deleteRow3 = "DELETE FROM categories WHERE id = 3";

    public SynchronizationIT(TestScenario scenario) {
        this.scenario = scenario;
    }

    @BeforeClass
    public static void initClass() throws SQLException {
        // initialize logging framework
        DOMConfigurator.configure(ClassLoader.getSystemResource("log4j.xml"));
    }

    @Before
    public void init() throws SyncException, ContextException, SQLException, IOException {
        db.init();

        db.dropTablesOnServer(tableNames);
        db.dropTablesOnClient(tableNames);

        db.executeQueriesOnServer(createQueries);
        db.executeQueriesOnServer(insertQueries);

        LOGGER.debug(scenario.getLongDescription());
    }

    @Test
    public void testWithDataBeforeFirstSync() throws SQLException, ContextException, SyncException {

        try {
            // there has to be a sync here, to prepare the MD tables and the triggers (if needed)
            scenario.synchronize(tableNames, getServerConflictResolver(), BIDIRECTIONAL);

            if (CONF.isSqlTriggerActivated()) {
                db.connectWithExternalUser();
            }

            scenario.setDataSources(db.getServerDs(), db.getClientDs());
            scenario.setConnections(db.getServerConnection(), db.getClientConnection());
            scenario.setSelectQueries(new String[]{
                    "select * from categories order by id asc",
                    "select * from categories_md order by pk asc"
                });

            scenario.executeSteps();

            scenario.saveCurrentState();

            scenario.synchronize(tableNames, getServerConflictResolver());

            scenario.assertNoExceptionExpected();
            scenario.assertServerIsInExpectedState();
            scenario.assertClientIsInExpectedState();

        } catch (SyncException ex) {
            if (!isExpectedException(ex)) {
                // we let this unwanted exception bubble up...
                throw ex;
            }
        } catch (IllegalStateException ex) {
            Assert.assertEquals("Unexpected exception: " + ex.getLocalizedMessage(), ex.getClass(),
                scenario.getExpectedExceptionClass());
            Assert.assertTrue(ex.getLocalizedMessage().startsWith(scenario.getExpectedErrorMsg()));
        }
    }

    private boolean isExpectedException(Exception ex) {
        boolean correctClass = ex.getClass().equals(scenario.getExpectedExceptionClass());
        boolean correctMessage = false;
        if (scenario.getExpectedErrorMsg() != null) {
            correctMessage = ex.getLocalizedMessage().startsWith(scenario.getExpectedErrorMsg());
        }

        return correctClass && correctMessage;
    }

    public IConflictListener getServerConflictResolver() {
        return new IConflictListener() {
            @Override
            public Map<String, Object> resolve(Map<String, Object> serverData, Map<String, Object> clientData) {
                return serverData;
            }
        };
    }

    public IConflictListener getClientConflictResolver() {
        return new IConflictListener() {
            @Override
            public Map<String, Object> resolve(Map<String, Object> serverData, Map<String, Object> clientData) {
                return clientData;
            }
        };
    }

    @After
    public void tearDown() throws SQLException {
        db.closeConnections();
    }

    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> AllScenarii() {

        return Arrays.asList(new Object[][]{
                // Creates a scenario  [name]            [direction]   [strategy]
                {new TestScenario("ServerUc ClientUc", BIDIRECTIONAL, SERVER_WINS)
                    // and sets the expected state after a sync:
                    // - 'S' codes a row that originates from the server
                    // - 'C' a row from the client
                    // - ' ' a blank space counts as a deleted row:
                    //    "C S" = 1st row is from the client, the 2nd row was deleted and replaced by the server's 3rd row
                    .expectServer("SS") // expected rows on server
                    .expectClient("CC")}, // expected rows on client
                {new TestScenario("ServerUc ClientUc", BIDIRECTIONAL, CLIENT_WINS)
                    .expectServer("SS")
                    .expectClient("CC")},
                {new TestScenario("ServerUc ClientUc", CLIENT_TO_SERVER, CLIENT_WINS)
                    .expectServer("SS")
                    .expectClient("CC")},
                {new TestScenario("ServerUc ClientUc", SERVER_TO_CLIENT, SERVER_WINS)
                    .expectServer("SS")
                    .expectClient("CC")},
                {new TestScenario("ServerUc ClientUc", BIDIRECTIONAL, FIRE_EVENT)
                    .expectServer("SS")
                    .expectClient("CC")},
                {new TestScenario("* ServerUc ClientUc invalid", SERVER_TO_CLIENT, CLIENT_WINS)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                {new TestScenario("* ServerUc ClientUc invalid", CLIENT_TO_SERVER, SERVER_WINS)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                //
                {new TestScenario("ServerUc ClientAdd", BIDIRECTIONAL, SERVER_WINS)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSC")
                    .expectClient("CCC")},
                {new TestScenario("ServerUc ClientAdd", BIDIRECTIONAL, CLIENT_WINS)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSC")
                    .expectClient("CCC")},
                {new TestScenario("ServerUc ClientAdd", CLIENT_TO_SERVER, CLIENT_WINS)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSC")
                    .expectClient("CCC")},
                {new TestScenario("ServerUc ClientAdd", SERVER_TO_CLIENT, SERVER_WINS)
                    .addStep(CLIENT, insertRow3a)
                    .expectException(SyncException.class, COMMON_NO_CLIENTCHANGES_ALLOWED_TO_SYNC_FOR_TABLE)},
                {new TestScenario("ServerUc ClientAdd", BIDIRECTIONAL, FIRE_EVENT)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSC")
                    .expectClient("CCC")},
                {new TestScenario("* ServerUc ClientAdd invalid", CLIENT_TO_SERVER, SERVER_WINS)
                    .addStep(CLIENT, insertRow3a)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                {new TestScenario("* ServerUc ClientAdd invalid", SERVER_TO_CLIENT, CLIENT_WINS)
                    .addStep(CLIENT, insertRow3a)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                //
                {new TestScenario("ServerUc ClientMod", BIDIRECTIONAL, SERVER_WINS)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("SC")
                    .expectClient("CC")},
                {new TestScenario("ServerUc ClientMod", BIDIRECTIONAL, CLIENT_WINS)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("SC")
                    .expectClient("CC")},
                {new TestScenario("ServerUc ClientMod", CLIENT_TO_SERVER, CLIENT_WINS)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("SC")
                    .expectClient("CC")},
                {new TestScenario("ServerUc ClientMod", SERVER_TO_CLIENT, SERVER_WINS)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("SS")
                    .expectClient("CC")
                    .expectException(SyncException.class, COMMON_NO_CLIENTCHANGES_ALLOWED_TO_SYNC_FOR_TABLE)},
                {new TestScenario("ServerUc ClientMod", BIDIRECTIONAL, FIRE_EVENT)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("SC")
                    .expectClient("CC")},
                {new TestScenario("* ServerUc ClientMod invalid", SERVER_TO_CLIENT, CLIENT_WINS)
                    .addStep(CLIENT, updateRow2b)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                {new TestScenario("* ServerUc ClientMod invalid", CLIENT_TO_SERVER, SERVER_WINS)
                    .addStep(CLIENT, updateRow2b)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                //
                {new TestScenario("ServerUc ClientDel", BIDIRECTIONAL, SERVER_WINS)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("ServerUc ClientDel", BIDIRECTIONAL, CLIENT_WINS)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("ServerUc ClientDel", CLIENT_TO_SERVER, CLIENT_WINS)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("ServerUc ClientDel", SERVER_TO_CLIENT, SERVER_WINS)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("SS")
                    .expectClient("C")
                    .expectException(SyncException.class, COMMON_NO_CLIENTCHANGES_ALLOWED_TO_SYNC_FOR_TABLE)},
                {new TestScenario("ServerUc ClientDel", BIDIRECTIONAL, FIRE_EVENT)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("* ServerUc ClientDel invalid", SERVER_TO_CLIENT, CLIENT_WINS)
                    .addStep(CLIENT, deleteRow2)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                {new TestScenario("* ServerUc ClientDel invalid", CLIENT_TO_SERVER, SERVER_WINS)
                    .addStep(CLIENT, deleteRow2)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                //
                {new TestScenario("ServerAdd ClientUc", BIDIRECTIONAL, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .expectServer("SSS")
                    .expectClient("CCS")},
                {new TestScenario("ServerAdd ClientUc", BIDIRECTIONAL, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .expectServer("SSS")
                    .expectClient("CCS")},
                {new TestScenario("ServerAdd ClientUc", CLIENT_TO_SERVER, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .expectServer("SSS")
                    .expectClient("CC")},
                {new TestScenario("ServerAdd ClientUc", SERVER_TO_CLIENT, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .expectServer("SSS")
                    .expectClient("CCS")},
                {new TestScenario("ServerAdd ClientUc", BIDIRECTIONAL, FIRE_EVENT)
                    .addStep(SERVER, insertRow3a)
                    .expectServer("SSS")
                    .expectClient("CCS")},
                {new TestScenario("* ServerAdd ClientUc invalid", SERVER_TO_CLIENT, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                {new TestScenario("* ServerAdd ClientUc invalid", CLIENT_TO_SERVER, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                //
                {new TestScenario("ServerAdd ClientAdd", BIDIRECTIONAL, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSS")
                    .expectClient("CCS")},
                {new TestScenario("ServerAdd ClientAdd", BIDIRECTIONAL, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSC")
                    .expectClient("CCC")},
                {new TestScenario("ServerAdd ClientAdd", CLIENT_TO_SERVER, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSC")
                    .expectClient("CCC")},
                {new TestScenario("ServerAdd ClientAdd", SERVER_TO_CLIENT, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSS")
                    .expectClient("CCS")},
                {new TestScenario("ServerAdd ClientAdd", BIDIRECTIONAL, FIRE_EVENT)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSS")
                    .expectClient("CCC")},
                {new TestScenario("* ServerAdd ClientAdd invalid", SERVER_TO_CLIENT, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, insertRow3a)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                {new TestScenario("* ServerAdd ClientAdd invalid", CLIENT_TO_SERVER, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, insertRow3a)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                //
                {new TestScenario("ServerAdd ClientMod", BIDIRECTIONAL, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("SCS")
                    .expectClient("CCS")},
                {new TestScenario("ServerAdd ClientMod", BIDIRECTIONAL, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("SCS")
                    .expectClient("CCS")},
                {new TestScenario("ServerAdd ClientMod", CLIENT_TO_SERVER, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("SCS")
                    .expectClient("CC")},
                {new TestScenario("ServerAdd ClientMod", SERVER_TO_CLIENT, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("SSS")
                    .expectClient("CCS")
                    .expectException(SyncException.class, COMMON_NO_CLIENTCHANGES_ALLOWED_TO_SYNC_FOR_TABLE)},
                {new TestScenario("ServerAdd ClientMod", BIDIRECTIONAL, FIRE_EVENT)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("SCS")
                    .expectClient("CCS")},
                {new TestScenario("* ServerAdd ClientMod invalid", SERVER_TO_CLIENT, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, updateRow2b)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                {new TestScenario("* ServerAdd ClientMod invalid", CLIENT_TO_SERVER, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, updateRow2b)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                //
                {new TestScenario("ServerAdd ClientDel", BIDIRECTIONAL, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("S S")
                    .expectClient("C S")},
                {new TestScenario("ServerAdd ClientDel", BIDIRECTIONAL, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("S S")
                    .expectClient("C S")},
                {new TestScenario("ServerAdd ClientDel", CLIENT_TO_SERVER, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("S S")
                    .expectClient("C")},
                {new TestScenario("ServerAdd ClientDel", SERVER_TO_CLIENT, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("SSS")
                    .expectClient("C S")
                    .expectException(SyncException.class, COMMON_NO_CLIENTCHANGES_ALLOWED_TO_SYNC_FOR_TABLE)},
                {new TestScenario("ServerAdd ClientDel", BIDIRECTIONAL, FIRE_EVENT)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("S S")
                    .expectClient("C S")},
                {new TestScenario("* ServerAdd ClientDel invalid", SERVER_TO_CLIENT, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, deleteRow2)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                {new TestScenario("* ServerAdd ClientDel invalid", CLIENT_TO_SERVER, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(CLIENT, deleteRow2)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                //
                {new TestScenario("ServerMod ClientUc", BIDIRECTIONAL, SERVER_WINS)
                    .addStep(SERVER, updateRow2b)
                    .expectServer("SS")
                    .expectClient("CS")},
                {new TestScenario("ServerMod ClientUc", BIDIRECTIONAL, CLIENT_WINS)
                    .addStep(SERVER, updateRow2b)
                    .expectServer("SS")
                    .expectClient("CS")},
                {new TestScenario("ServerMod ClientUc", CLIENT_TO_SERVER, CLIENT_WINS)
                    .addStep(SERVER, updateRow2b)
                    .expectServer("SS")
                    .expectClient("CC")},
                {new TestScenario("ServerMod ClientUc", SERVER_TO_CLIENT, SERVER_WINS)
                    .addStep(SERVER, updateRow2b)
                    .expectServer("SS")
                    .expectClient("CS")},
                {new TestScenario("ServerMod ClientUc", BIDIRECTIONAL, FIRE_EVENT)
                    .addStep(SERVER, updateRow2b)
                    .expectServer("SS")
                    .expectClient("CS")},
                {new TestScenario("* ServerMod ClientUc invalid", SERVER_TO_CLIENT, CLIENT_WINS)
                    .addStep(SERVER, updateRow2b)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                {new TestScenario("* ServerMod ClientUc invalid", CLIENT_TO_SERVER, SERVER_WINS)
                    .addStep(SERVER, updateRow2b)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                //
                {new TestScenario("ServerMod ClientAdd", BIDIRECTIONAL, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(SERVER, updateRow3b)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSS")
                    .expectClient("CCS")},
                {new TestScenario("ServerMod ClientAdd", BIDIRECTIONAL, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(SERVER, updateRow3b)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSC")
                    .expectClient("CCC")},
                {new TestScenario("ServerMod ClientAdd", CLIENT_TO_SERVER, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(SERVER, updateRow3b)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSC")
                    .expectClient("CCC")},
                {new TestScenario("ServerMod ClientAdd", SERVER_TO_CLIENT, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(SERVER, updateRow3b)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSS")
                    .expectClient("CCS")},
                {new TestScenario("ServerMod ClientAdd", BIDIRECTIONAL, FIRE_EVENT)
                    .addStep(SERVER, insertRow3a)
                    .addStep(SERVER, updateRow3b)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSS")
                    .expectClient("CCS")},
                {new TestScenario("* ServerMod ClientAdd invalid", SERVER_TO_CLIENT, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(SERVER, updateRow3b)
                    .addStep(CLIENT, insertRow3a)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                {new TestScenario("* ServerMod ClientAdd invalid", CLIENT_TO_SERVER, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(SERVER, updateRow3b)
                    .addStep(CLIENT, insertRow3a)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                //
                {new TestScenario("ServerMod ClientMod", BIDIRECTIONAL, SERVER_WINS)
                    .addStep(SERVER, updateRow2c)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("SS")
                    .expectClient("CS")},
                {new TestScenario("ServerMod ClientMod", BIDIRECTIONAL, CLIENT_WINS)
                    .addStep(SERVER, updateRow2c)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("SC")
                    .expectClient("CC")},
                {new TestScenario("ServerMod ClientMod", CLIENT_TO_SERVER, CLIENT_WINS)
                    .addStep(SERVER, updateRow2c)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("SC")
                    .expectClient("CC")},
                {new TestScenario("ServerMod ClientMod", SERVER_TO_CLIENT, SERVER_WINS)
                    .addStep(SERVER, updateRow2c)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("SS")
                    .expectClient("CS")},
                {new TestScenario("ServerMod ClientMod", BIDIRECTIONAL, FIRE_EVENT)
                    .addStep(SERVER, updateRow2c)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("SS")
                    .expectClient("CS")},
                {new TestScenario("* ServerMod ClientMod invalid", SERVER_TO_CLIENT, CLIENT_WINS)
                    .addStep(SERVER, updateRow2c)
                    .addStep(CLIENT, updateRow2b)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                {new TestScenario("* ServerMod ClientMod invalid", CLIENT_TO_SERVER, SERVER_WINS)
                    .addStep(SERVER, updateRow2c)
                    .addStep(CLIENT, updateRow2b)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                //
                {new TestScenario("ServerMod ClientDel", BIDIRECTIONAL, SERVER_WINS)
                    .addStep(SERVER, updateRow2b)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("SS")
                    .expectClient("CS")},
                {new TestScenario("ServerMod ClientDel", BIDIRECTIONAL, CLIENT_WINS)
                    .addStep(SERVER, updateRow2b)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("ServerMod ClientDel", CLIENT_TO_SERVER, CLIENT_WINS)
                    .addStep(SERVER, updateRow2b)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("ServerMod ClientDel", SERVER_TO_CLIENT, SERVER_WINS)
                    .addStep(SERVER, updateRow2b)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("SS")
                    .expectClient("CS")},
                {new TestScenario("ServerMod ClientDel", BIDIRECTIONAL, FIRE_EVENT)
                    .addStep(SERVER, updateRow2b)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("SS")
                    .expectClient("CS")},
                {new TestScenario("* ServerMod ClientDel invalid", SERVER_TO_CLIENT, CLIENT_WINS)
                    .addStep(SERVER, updateRow2b)
                    .addStep(CLIENT, deleteRow2)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                {new TestScenario("* ServerMod ClientDel invalid", CLIENT_TO_SERVER, SERVER_WINS)
                    .addStep(SERVER, updateRow2b)
                    .addStep(CLIENT, deleteRow2)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                //
                {new TestScenario("ServerDel ClientUc", BIDIRECTIONAL, SERVER_WINS)
                    .addStep(SERVER, deleteRow2)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("ServerDel ClientUc", BIDIRECTIONAL, CLIENT_WINS)
                    .addStep(SERVER, deleteRow2)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("ServerDel ClientUc", CLIENT_TO_SERVER, CLIENT_WINS)
                    .addStep(SERVER, deleteRow2)
                    .expectServer("S")
                    .expectClient("CC")},
                {new TestScenario("ServerDel ClientUc", SERVER_TO_CLIENT, SERVER_WINS)
                    .addStep(SERVER, deleteRow2)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("ServerDel ClientUc", BIDIRECTIONAL, FIRE_EVENT)
                    .addStep(SERVER, deleteRow2)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("* ServerDel ClientUc invalid", SERVER_TO_CLIENT, CLIENT_WINS)
                    .addStep(SERVER, deleteRow2)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                {new TestScenario("* ServerDel ClientUc invalid", CLIENT_TO_SERVER, SERVER_WINS)
                    .addStep(SERVER, deleteRow2)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                //
                {new TestScenario("ServerDel ClientAdd", BIDIRECTIONAL, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(SERVER, deleteRow3)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSC")
                    .expectClient("CCC")},
                {new TestScenario("ServerDel ClientAdd", BIDIRECTIONAL, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(SERVER, deleteRow3)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSC")
                    .expectClient("CCC")},
                {new TestScenario("ServerDel ClientAdd", CLIENT_TO_SERVER, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(SERVER, deleteRow3)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSC")
                    .expectClient("CCC")},
                {new TestScenario("ServerDel ClientAdd", SERVER_TO_CLIENT, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(SERVER, deleteRow3)
                    .addStep(CLIENT, insertRow3a)
                    .expectException(SyncException.class, COMMON_NO_CLIENTCHANGES_ALLOWED_TO_SYNC_FOR_TABLE)},
                {new TestScenario("ServerDel ClientAdd", BIDIRECTIONAL, FIRE_EVENT)
                    .addStep(SERVER, insertRow3a)
                    .addStep(SERVER, deleteRow3)
                    .addStep(CLIENT, insertRow3a)
                    .expectServer("SSC")
                    .expectClient("CCC")},
                {new TestScenario("* ServerDel ClientAdd invalid", SERVER_TO_CLIENT, CLIENT_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(SERVER, deleteRow3)
                    .addStep(CLIENT, insertRow3a)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                {new TestScenario("* ServerDel ClientAdd invalid", CLIENT_TO_SERVER, SERVER_WINS)
                    .addStep(SERVER, insertRow3a)
                    .addStep(SERVER, deleteRow3)
                    .addStep(CLIENT, insertRow3a)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                //
                {new TestScenario("ServerDel ClientMod", BIDIRECTIONAL, SERVER_WINS)
                    .addStep(SERVER, deleteRow2)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("ServerDel ClientMod", BIDIRECTIONAL, CLIENT_WINS)
                    .addStep(SERVER, deleteRow2)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("SC")
                    .expectClient("CC")},
                {new TestScenario("ServerDel ClientMod", CLIENT_TO_SERVER, CLIENT_WINS)
                    .addStep(SERVER, deleteRow2)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("SC")
                    .expectClient("CC")},
                {new TestScenario("ServerDel ClientMod", SERVER_TO_CLIENT, SERVER_WINS)
                    .addStep(SERVER, deleteRow2)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("ServerDel ClientMod", BIDIRECTIONAL, FIRE_EVENT)
                    .addStep(SERVER, deleteRow2)
                    .addStep(CLIENT, updateRow2b)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("* ServerDel ClientMod invalid", SERVER_TO_CLIENT, CLIENT_WINS)
                    .addStep(SERVER, deleteRow2)
                    .addStep(CLIENT, updateRow2b)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                {new TestScenario("* ServerDel ClientMod invalid", CLIENT_TO_SERVER, SERVER_WINS)
                    .addStep(SERVER, deleteRow2)
                    .addStep(CLIENT, updateRow2b)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                //
                {new TestScenario("ServerDel ClientDel", BIDIRECTIONAL, SERVER_WINS)
                    .addStep(SERVER, deleteRow2)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("ServerDel ClientDel", BIDIRECTIONAL, CLIENT_WINS)
                    .addStep(SERVER, deleteRow2)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("ServerDel ClientDel", CLIENT_TO_SERVER, CLIENT_WINS)
                    .addStep(SERVER, deleteRow2)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("ServerDel ClientDel", SERVER_TO_CLIENT, SERVER_WINS)
                    .addStep(SERVER, deleteRow2)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("ServerDel ClientDel", BIDIRECTIONAL, FIRE_EVENT)
                    .addStep(SERVER, deleteRow2)
                    .addStep(CLIENT, deleteRow2)
                    .expectServer("S")
                    .expectClient("C")},
                {new TestScenario("* ServerDel ClientDel invalid", SERVER_TO_CLIENT, CLIENT_WINS)
                    .addStep(SERVER, deleteRow2)
                    .addStep(CLIENT, deleteRow2)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)},
                {new TestScenario("* ServerDel ClientDel invalid", CLIENT_TO_SERVER, SERVER_WINS)
                    .addStep(SERVER, deleteRow2)
                    .addStep(CLIENT, deleteRow2)
                    .expectException(IllegalStateException.class, NOT_SUPPORTED_CONFLICT_STRATEGY)}
            });
    }
}
