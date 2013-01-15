package de.consistec.syncframework.impl;

import static de.consistec.syncframework.common.SyncDirection.BIDIRECTIONAL;
import static de.consistec.syncframework.common.SyncDirection.CLIENT_TO_SERVER;
import static de.consistec.syncframework.common.SyncDirection.SERVER_TO_CLIENT;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.CLIENT_WINS;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.FIRE_EVENT;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.SERVER_WINS;
import static de.consistec.syncframework.impl.adapter.ConnectionType.CLIENT;
import static de.consistec.syncframework.impl.adapter.ConnectionType.SERVER;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.impl.adapter.DumpDataSource.SupportedDatabases;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @company Consistec Engineering and Consulting GmbH
 * @date 10.01.2013 15:41:50
 * @author davidm
 * @since
 */
@RunWith(value = Parameterized.class)
public class ITSynchronization {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ITSynchronization.class.getCanonicalName());
    protected static final Config CONF = Config.getInstance();
    protected TestDatabase db;
    private static TestDatabase dbPostgre = new TestDatabase("/config_postgre.properties", SupportedDatabases.POSTGRESQL);
    private static TestDatabase dbMySql = new TestDatabase("/config_mysql.properties", SupportedDatabases.MYSQL);
    private static TestDatabase dbSqlite = new TestDatabase("/config_sqlite.properties", SupportedDatabases.SQLITE);
    protected TestScenario scenario;
    protected static String deleteRow2 = "DELETE FROM categories WHERE id = 2";
    protected static String updateRow2b = "UPDATE categories SET name = 'Cat2b', description = '2b' WHERE id = 2";
    protected static String updateRow2c = "UPDATE categories SET name = 'Cat2c', description = '2c' WHERE id = 2";
    protected static String insertRow3 = "INSERT INTO categories (id, name, description) VALUES (3, 'Cat3a', '3a')";
    protected static String updateRow3 = "UPDATE categories SET name = 'Cat3b', description = '3b' WHERE id = 3";
    protected static String deleteRow3 = "DELETE FROM categories WHERE id = 3";
    protected static String[] tableNames = new String[]{"categories", "categories_md", "items", "items_md"};
    protected static String[] insertDataQueries = new String[]{
        "INSERT INTO categories (\"id\", \"name\", \"description\") VALUES (1, 'Beverages', 'Soft drinks')",
        "INSERT INTO categories (\"id\", \"name\", \"description\") VALUES (2, 'Condiments', 'Sweet and ')",
        "INSERT INTO categories_md (\"rev\", \"mdv\", \"pk\", \"f\") VALUES (1, '8B7132AE51A73532FBD29CCA15B2CB38', 1, 0)",
        "INSERT INTO categories_md (\"rev\", \"mdv\", \"pk\", \"f\") VALUES (1, 'FB5EF33FE008589C86C0007AC0597E00', 2, 0)",};
    protected static String[] createQueries = new String[]{
        "CREATE TABLE categories (\"id\" INTEGER NOT NULL PRIMARY KEY ,\"name\" VARCHAR (300),\"description\" VARCHAR (300));",
        "CREATE TABLE categories_md (\"pk\" INTEGER NOT NULL PRIMARY KEY, \"mdv\" VARCHAR (300), \"rev\" INTEGER DEFAULT 1, \"f\" INTEGER DEFAULT 0);",
        "CREATE TABLE items (\"id\" INTEGER NOT NULL PRIMARY KEY ,\"name\" VARCHAR (300),\"description\" VARCHAR (300));",
        "CREATE TABLE items_md (\"pk\" INTEGER NOT NULL PRIMARY KEY, \"mdv\" VARCHAR (300), \"rev\" INTEGER DEFAULT 1, \"f\" INTEGER DEFAULT 0);"};

    public ITSynchronization(TestScenario scenario) {
        this.scenario = scenario;
    }

    @BeforeClass
    public static void initClass() throws SQLException {
        // initialize logging framework
        DOMConfigurator.configure(ClassLoader.getSystemResource("log4j.xml"));
    }

    @Before
    public void init() throws SyncException, ContextException, SQLException, IOException {
        LOGGER.debug("\n---------------------\n" + scenario.getLongDescription() + "\n----------------------\n");

        // THIS WILL BE PARAMETRIZED IN A NEAR FUTURE!
        this.db = dbPostgre;

        Config.getInstance().loadFromFile(getClass().getResourceAsStream(db.getConfigFile()));

        db.init();

        db.dropTablesOnServer(tableNames);
        db.dropTablesOnClient(tableNames);

        db.executeQueriesOnServer(createQueries);
        db.executeQueriesOnClient(createQueries);

        db.executeQueriesOnServer(insertDataQueries);
        db.executeQueriesOnClient(insertDataQueries);
    }

    @Test
    public void test() throws SQLException, ContextException, SyncException {
        scenario.setDataSources(db.getServerDs(), db.getClientDs());
        scenario.setConnections(db.getServerConnection(), db.getClientConnection());

        scenario.setSelectQueries(new String[]{
                "select * from categories order by id asc",
                "select * from categories_md order by pk asc"
            });

        scenario.executeSteps();

        scenario.saveCurrentState();

        if (scenario.hasInvalidDirectionAndStrategyCombination()) {
            try {
                scenario.synchronize(tableNames);
                fail("Expected an exception for this sync direction + strategy");
            } catch (IllegalStateException ex) {
                // this exception MUST happen!
                assertTrue(ex.getMessage().startsWith("The configured conflict strategy " + scenario.getStrategy()));
            }
        } else {
            scenario.synchronize(tableNames);

            scenario.assertServerIsInExpectedState();

            scenario.assertClientIsInExpectedState();
        }

    }

    @After
    public void tearDown() throws SQLException {
        db.clean();
    }

    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> AllScenarii() {
        return Arrays.asList(new Object[][]{
                // First the scenario's name, then:      direction,     strategy, expected rows on server, expected rows on client
                // - 'S' codes one row which originates from the server
                // - 'C' from the client
                // - a blank space counts as a deleted row:
                //    "C S" = 1st row is from the client, the 2nd row was deleted and replaced by the server's 3rd row
                {new TestScenario("Unchanged Unchanged", BIDIRECTIONAL, SERVER_WINS, "SS", "CC")},
                {new TestScenario("Unchanged Unchanged", BIDIRECTIONAL, CLIENT_WINS, "SS", "CC")},
                {new TestScenario("Unchanged Unchanged", CLIENT_TO_SERVER, CLIENT_WINS, "SS", "CC")},
                {new TestScenario("Unchanged Unchanged", SERVER_TO_CLIENT, SERVER_WINS, "SS", "CC")},
                {new TestScenario("Unchanged Unchanged", BIDIRECTIONAL, FIRE_EVENT, "SS", "CC")},
                {new TestScenario("* Unchanged Unchanged invalid", SERVER_TO_CLIENT, CLIENT_WINS, "SS", "CC")}, // invalid combination
                {new TestScenario("* Unchanged Unchanged invalid", CLIENT_TO_SERVER, SERVER_WINS, "SS", "CC")}, // invalid combination
                //
                {new TestScenario("Added Unchanged", BIDIRECTIONAL, SERVER_WINS, "SSC", "CCC")
                    .addStep(CLIENT, insertRow3)},
                {new TestScenario("Added Unchanged", BIDIRECTIONAL, CLIENT_WINS, "SSC", "CCC")
                    .addStep(CLIENT, insertRow3)},
                {new TestScenario("Added Unchanged", CLIENT_TO_SERVER, CLIENT_WINS, "SSC", "CCC")
                    .addStep(CLIENT, insertRow3)},
                {new TestScenario("Added Unchanged", SERVER_TO_CLIENT, SERVER_WINS, "SS", "CCC")
                    .addStep(CLIENT, insertRow3)},
                {new TestScenario("Added Unchanged", BIDIRECTIONAL, FIRE_EVENT, "SSC", "CCC")
                    .addStep(CLIENT, insertRow3)},
                {new TestScenario("* Added Unchanged invalid", CLIENT_TO_SERVER, SERVER_WINS, "SSC", "CCC")
                    .addStep(CLIENT, insertRow3)},  // invalid combination
                {new TestScenario("* Added Unchanged invalid", SERVER_TO_CLIENT, CLIENT_WINS, "SS", "CCC")
                    .addStep(CLIENT, insertRow3)},  // invalid combination
                //
                {new TestScenario("Modified Unchanged", BIDIRECTIONAL, SERVER_WINS, "SC", "CC")
                    .addStep(CLIENT, updateRow2b)},
                {new TestScenario("Modified Unchanged", BIDIRECTIONAL, CLIENT_WINS, "SC", "CC")
                    .addStep(CLIENT, updateRow2b)},
                {new TestScenario("Modified Unchanged", CLIENT_TO_SERVER, CLIENT_WINS, "SC", "CC")
                    .addStep(CLIENT, updateRow2b)},
                {new TestScenario("Modified Unchanged", SERVER_TO_CLIENT, SERVER_WINS, "SS", "CC")
                    .addStep(CLIENT, updateRow2b)},
                {new TestScenario("Modified Unchanged", BIDIRECTIONAL, FIRE_EVENT, "SC", "CC")
                    .addStep(CLIENT, updateRow2b)},
                {new TestScenario("* Modified Unchanged invalid", SERVER_TO_CLIENT, CLIENT_WINS, "SC", "CC")
                    .addStep(CLIENT, updateRow2b)},  // invalid combination
                {new TestScenario("* Modified Unchanged invalid", CLIENT_TO_SERVER, SERVER_WINS, "SS", "CC")
                    .addStep(CLIENT, updateRow2b)},  // invalid combination
                //
                {new TestScenario("Deleted Unchanged", BIDIRECTIONAL, SERVER_WINS, "S", "C").addStep(CLIENT, deleteRow2)},
                {new TestScenario("Deleted Unchanged", BIDIRECTIONAL, CLIENT_WINS, "S", "C").addStep(CLIENT, deleteRow2)},
                {new TestScenario("Deleted Unchanged", CLIENT_TO_SERVER, CLIENT_WINS, "S", "C").addStep(CLIENT,
                    deleteRow2)},
                {new TestScenario("Deleted Unchanged", SERVER_TO_CLIENT, SERVER_WINS, "SS", "C").addStep(CLIENT,
                    deleteRow2)},
                {new TestScenario("Deleted Unchanged", BIDIRECTIONAL, FIRE_EVENT, "S", "C").addStep(CLIENT, deleteRow2)},
                {new TestScenario("* Deleted Unchanged invalid", SERVER_TO_CLIENT, CLIENT_WINS, "S", "C").addStep(CLIENT,
                    deleteRow2)}, // invalid combination
                {new TestScenario("* Deleted Unchanged invalid", CLIENT_TO_SERVER, SERVER_WINS, "SS", "C").addStep(CLIENT,
                    deleteRow2)}, // invalid combination
                //
                {new TestScenario("Unchanged Added", BIDIRECTIONAL, SERVER_WINS, "SSS", "CCS").addStep(SERVER,
                    insertRow3)},
                {new TestScenario("Unchanged Added", BIDIRECTIONAL, CLIENT_WINS, "SSS", "CCS").addStep(SERVER,
                    insertRow3)},
                {new TestScenario("Unchanged Added", CLIENT_TO_SERVER, CLIENT_WINS, "SSS", "CC").addStep(SERVER,
                    insertRow3)},
                {new TestScenario("Unchanged Added", SERVER_TO_CLIENT, SERVER_WINS, "SSS", "CCS").addStep(SERVER,
                    insertRow3)},
                {new TestScenario("Unchanged Added", BIDIRECTIONAL, FIRE_EVENT, "SSS", "CCS").addStep(SERVER, insertRow3)},
                {new TestScenario("* Unchanged Added invalid", SERVER_TO_CLIENT, CLIENT_WINS, "SSS", "CC").addStep(SERVER,
                    insertRow3)}, // invalid combination
                {new TestScenario("* Unchanged Added invalid", CLIENT_TO_SERVER, SERVER_WINS, "SSS", "CCS").addStep(SERVER,
                    insertRow3)}, // invalid combination
                //
                {new TestScenario("Added Added", BIDIRECTIONAL, SERVER_WINS, "SSS", "CCS")
                    .addStep(CLIENT, insertRow3)
                    .addStep(SERVER, insertRow3)},
                {new TestScenario("Added Added", BIDIRECTIONAL, CLIENT_WINS, "SSC", "CCC")
                    .addStep(CLIENT, insertRow3)
                    .addStep(SERVER, insertRow3)},
                {new TestScenario("Added Added", CLIENT_TO_SERVER, CLIENT_WINS, "SSC", "CCC")
                    .addStep(CLIENT, insertRow3)
                    .addStep(SERVER, insertRow3)},
                {new TestScenario("Added Added", SERVER_TO_CLIENT, SERVER_WINS, "SSS", "CCS")
                    .addStep(CLIENT, insertRow3)
                    .addStep(SERVER, insertRow3)},
                {new TestScenario("Added Added", BIDIRECTIONAL, FIRE_EVENT, "SSS", "CCC")
                    .addStep(CLIENT, insertRow3)
                    .addStep(SERVER, insertRow3)},
                {new TestScenario("* Added Added invalid", SERVER_TO_CLIENT, CLIENT_WINS, "SSC", "CCC")
                    .addStep(CLIENT, insertRow3)
                    .addStep(SERVER, insertRow3)},  // invalid combination
                {new TestScenario("* Added Added invalid", CLIENT_TO_SERVER, SERVER_WINS, "SSS", "CCS")
                    .addStep(CLIENT, insertRow3)
                    .addStep(SERVER, insertRow3)},  // invalid combination
                //
                {new TestScenario("Modified Added", BIDIRECTIONAL, SERVER_WINS, "SCS", "CCS")
                    .addStep(CLIENT, updateRow2b)
                    .addStep(SERVER, insertRow3)},
                {new TestScenario("Modified Added", BIDIRECTIONAL, CLIENT_WINS, "SCS", "CCS")
                    .addStep(CLIENT, updateRow2b)
                    .addStep(SERVER, insertRow3)},
                {new TestScenario("Modified Added", CLIENT_TO_SERVER, CLIENT_WINS, "SCS", "CC")
                    .addStep(CLIENT, updateRow2b)
                    .addStep(SERVER, insertRow3)},
                {new TestScenario("Modified Added", SERVER_TO_CLIENT, SERVER_WINS, "SSS", "CCS")
                    .addStep(CLIENT, updateRow2b)
                    .addStep(SERVER, insertRow3)},
                {new TestScenario("Modified Added", BIDIRECTIONAL, FIRE_EVENT, "SCS", "CCS")
                    .addStep(CLIENT, updateRow2b)
                    .addStep(SERVER, insertRow3)},
                {new TestScenario("* Modified Added invalid", SERVER_TO_CLIENT, CLIENT_WINS, "SCS", "CC")
                    .addStep(CLIENT, updateRow2b)
                    .addStep(SERVER, insertRow3)},  // invalid combination
                {new TestScenario("* Modified Added invalid", CLIENT_TO_SERVER, SERVER_WINS, "SSS", "CCS")
                    .addStep(CLIENT, updateRow2b)
                    .addStep(SERVER, insertRow3)},  // invalid combination
                //
                {new TestScenario("Deleted Added", BIDIRECTIONAL, SERVER_WINS, "SS", "CS")
                    .addStep(CLIENT, deleteRow2)
                    .addStep(SERVER, insertRow3)},
                {new TestScenario("Deleted Added", BIDIRECTIONAL, CLIENT_WINS, "SS", "CS")
                    .addStep(CLIENT, deleteRow2)
                    .addStep(SERVER, insertRow3)},
                {new TestScenario("Deleted Added", CLIENT_TO_SERVER, CLIENT_WINS, "SS", "C")
                    .addStep(CLIENT, deleteRow2)
                    .addStep(SERVER, insertRow3)},
                {new TestScenario("Deleted Added", SERVER_TO_CLIENT, SERVER_WINS, "SSS", "C S")
                    .addStep(CLIENT, deleteRow2)
                    .addStep(SERVER, insertRow3)},
                {new TestScenario("Deleted Added", BIDIRECTIONAL, FIRE_EVENT, "SS", "CS")
                    .addStep(CLIENT, deleteRow2)
                    .addStep(SERVER, insertRow3)}, //
                {new TestScenario("* Deleted Added invalid", SERVER_TO_CLIENT, CLIENT_WINS, "SS", "C")
                    .addStep(CLIENT, deleteRow2)
                    .addStep(SERVER, insertRow3)},
                {new TestScenario("* Deleted Added invalid", CLIENT_TO_SERVER, SERVER_WINS, "SSS", "C S")
                    .addStep(CLIENT, deleteRow2)
                    .addStep(SERVER, insertRow3)},
                //
                {new TestScenario("Added Modified", BIDIRECTIONAL, SERVER_WINS, "SSS", "CCS")
                    .addStep(CLIENT, insertRow3)
                    .addStep(SERVER, insertRow3)
                    .addStep(SERVER, updateRow3)},
                {new TestScenario("Added Modified", BIDIRECTIONAL, CLIENT_WINS, "SSC", "CCC")
                    .addStep(CLIENT, insertRow3)
                    .addStep(SERVER, insertRow3)
                    .addStep(SERVER, updateRow3)},
                {new TestScenario("Added Modified", CLIENT_TO_SERVER, CLIENT_WINS, "SSC", "CCC")
                    .addStep(CLIENT, insertRow3)
                    .addStep(SERVER, insertRow3)
                    .addStep(SERVER, updateRow3)},
                {new TestScenario("Added Modified", SERVER_TO_CLIENT, SERVER_WINS, "SSS", "CCS")
                    .addStep(CLIENT, insertRow3)
                    .addStep(SERVER, insertRow3)
                    .addStep(SERVER, updateRow3)},
                {new TestScenario("Added Modified", BIDIRECTIONAL, FIRE_EVENT, "SSS", "CCS")
                    .addStep(CLIENT, insertRow3)
                    .addStep(SERVER, insertRow3)
                    .addStep(SERVER, updateRow3)},
                {new TestScenario("* Added Modified invalid", SERVER_TO_CLIENT, CLIENT_WINS, "", "")
                    .addStep(CLIENT, insertRow3)
                    .addStep(SERVER, insertRow3)
                    .addStep(SERVER, updateRow3)},  // invalid combination
                {new TestScenario("* Added Modified invalid", CLIENT_TO_SERVER, SERVER_WINS, "", "")
                    .addStep(CLIENT, insertRow3)
                    .addStep(SERVER, insertRow3)
                    .addStep(SERVER, updateRow3)},  // invalid combination
                //
                {new TestScenario("Modified Modified", BIDIRECTIONAL, SERVER_WINS, "SS", "CS")
                    .addStep(CLIENT, updateRow2b)
                    .addStep(SERVER, updateRow2c)},
                {new TestScenario("Modified Modified", BIDIRECTIONAL, CLIENT_WINS, "SC", "CC")
                    .addStep(CLIENT, updateRow2b)
                    .addStep(SERVER, updateRow2c)},
                {new TestScenario("Modified Modified", CLIENT_TO_SERVER, CLIENT_WINS, "SC", "CC")
                    .addStep(CLIENT, updateRow2b)
                    .addStep(SERVER, updateRow2c)},
                {new TestScenario("Modified Modified", SERVER_TO_CLIENT, SERVER_WINS, "SS", "CS")
                    .addStep(CLIENT, updateRow2b)
                    .addStep(SERVER, updateRow2c)},
                {new TestScenario("Modified Modified", BIDIRECTIONAL, FIRE_EVENT, "SS", "CS")
                    .addStep(CLIENT, updateRow2b)
                    .addStep(SERVER, updateRow2c)},
                {new TestScenario("* Modified Modified invalid", SERVER_TO_CLIENT, CLIENT_WINS, "", "")
                    .addStep(CLIENT, updateRow2b)
                    .addStep(SERVER, updateRow2c)},  // invalid combination
                {new TestScenario("* Modified Modified invalid", CLIENT_TO_SERVER, SERVER_WINS, "", "")
                    .addStep(CLIENT, updateRow2b)
                    .addStep(SERVER, updateRow2c)},  // invalid combination
                //
                {new TestScenario("Deleted Modified", BIDIRECTIONAL, SERVER_WINS, "SS", "CS")
                    .addStep(CLIENT, deleteRow2)
                    .addStep(SERVER, updateRow2b)},
                {new TestScenario("Deleted Modified", BIDIRECTIONAL, CLIENT_WINS, "S", "C")
                    .addStep(CLIENT, deleteRow2)
                    .addStep(SERVER, updateRow2b)},
                {new TestScenario("Deleted Modified", CLIENT_TO_SERVER, CLIENT_WINS, "S", "C")
                    .addStep(CLIENT, deleteRow2)
                    .addStep(SERVER, updateRow2b)},
                {new TestScenario("Deleted Modified", SERVER_TO_CLIENT, SERVER_WINS, "SS", "CS")
                    .addStep(CLIENT, deleteRow2)
                    .addStep(SERVER, updateRow2b)},
                {new TestScenario("Deleted Modified", BIDIRECTIONAL, FIRE_EVENT, "SS", "CS")
                    .addStep(CLIENT, deleteRow2)
                    .addStep(SERVER, updateRow2b)},
                {new TestScenario("* Deleted Modified invalid", SERVER_TO_CLIENT, CLIENT_WINS, "", "")
                    .addStep(CLIENT, deleteRow2)
                    .addStep(SERVER, updateRow2b)},  // invalid combination
                {new TestScenario("* Deleted Modified invalid", CLIENT_TO_SERVER, SERVER_WINS, "", "")
                    .addStep(CLIENT, deleteRow2)
                    .addStep(SERVER, updateRow2b)},  // invalid combination
                //
            });
    }
}
