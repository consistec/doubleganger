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
import de.consistec.syncframework.impl.adapter.AbstractSyncTest;
import de.consistec.syncframework.impl.adapter.DumpDataSource.SupportedDatabases;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @company Consistec Engineering and Consulting GmbH
 * @date 10.01.2013 15:41:50
 * @author davidm
 * @since
 */
@RunWith(value = Parameterized.class)
public class SynchronizationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSyncTest.class.getCanonicalName());
    protected static final Config CONF = Config.getInstance();
    private TestScenario scenario;
    private static TestDatabase db;
    static String deleteLastRow = "DELETE FROM categories WHERE id = 2";
    static String updateLastRow = "UPDATE categories SET id = 2, name = 'Cat2b', description = '2b' WHERE id = 2";
    static String insertNewRow = "INSERT INTO categories (id, name, description) VALUES (3, 'Cat3a', '3a')";
    static String updateNewRow = "UPDATE categories SET id = 3, name = 'Cat3b', description = '3b' WHERE id = 3";
    static String deleteNewRow = "DELETE FROM categories WHERE id = 3";
    static String[] tableNames = new String[]{"categories", "categories_md"};
    static String[] insertDataQueries = new String[]{
        "INSERT INTO categories (\"id\", \"name\", \"description\") VALUES (1, 'Beverages', 'Soft drinks')",
        "INSERT INTO categories (\"id\", \"name\", \"description\") VALUES (2, 'Condiments', 'Sweet and ')",
        "INSERT INTO categories_md (\"rev\", \"mdv\", \"pk\", \"f\") VALUES (1, '8B7132AE51A73532FBD29CCA15B2CB38', 1, 0)",
        "INSERT INTO categories_md (\"rev\", \"mdv\", \"pk\", \"f\") VALUES (1, 'FB5EF33FE008589C86C0007AC0597E00', 2, 0)",};
    static String[] postgresCreateQueries = new String[]{
        "CREATE TABLE categories (\"id\" INTEGER NOT NULL PRIMARY KEY ,\"name\" VARCHAR (300),\"description\" VARCHAR (300));",
        "CREATE TABLE categories_md (\"pk\" INTEGER NOT NULL PRIMARY KEY, \"mdv\" VARCHAR (300), \"rev\" INTEGER DEFAULT 1, \"f\" INTEGER DEFAULT 0);"};

    @Parameterized.Parameters
    public static Collection<TestScenario[]> AllScenarii() {
        TestScenario[][] scenarii = new TestScenario[][]{
            {new TestScenario("Unchanged Unchanged", BIDIRECTIONAL, SERVER_WINS, CLIENT, CLIENT)},
            {new TestScenario("Unchanged Unchanged", BIDIRECTIONAL, CLIENT_WINS, CLIENT, CLIENT)},
            {new TestScenario("Unchanged Unchanged", BIDIRECTIONAL, SERVER_WINS, CLIENT, CLIENT)},
            {new TestScenario("Unchanged Unchanged", BIDIRECTIONAL, CLIENT_WINS, CLIENT, CLIENT)},
            {new TestScenario("Add Unchanged", BIDIRECTIONAL, SERVER_WINS, CLIENT, CLIENT).addStep(CLIENT, insertNewRow)},
            {new TestScenario("Add Unchanged", BIDIRECTIONAL, CLIENT_WINS, CLIENT, CLIENT).addStep(CLIENT, insertNewRow)},
            {new TestScenario("Add Unchanged", CLIENT_TO_SERVER, CLIENT_WINS, CLIENT, CLIENT).addStep(CLIENT, insertNewRow)},
            {new TestScenario("Add Unchanged", SERVER_TO_CLIENT, SERVER_WINS, SERVER, CLIENT).addStep(CLIENT, insertNewRow)},
            {new TestScenario("Add Unchanged", BIDIRECTIONAL, FIRE_EVENT, CLIENT, CLIENT).addStep(CLIENT, insertNewRow)}
        };
        return Arrays.asList(scenarii);
    }

    public SynchronizationTest(TestScenario scenario) {
        this.scenario = scenario;
    }

    @BeforeClass
    public static void initClass() throws SQLException {
        // initialize logging framework
        DOMConfigurator.configure(ClassLoader.getSystemResource("log4j.xml"));
        db = new TestDatabase("/config_postgre.properties", SupportedDatabases.POSTGRESQL, postgresCreateQueries);
    }

    @Before
    public void init() throws SyncException, ContextException, SQLException, IOException {
        LOGGER.debug("\n---------------------\n" + scenario.toString() + "\n----------------------\n");

        Config.getInstance().loadFromFile(getClass().getResourceAsStream(db.getConfigFile()));

        db.init();

        db.dropTablesOnServer(tableNames);
        db.dropTablesOnClient(tableNames);

        db.createTablesOnServer();
        db.createTablesOnClient();

        db.executeQueriesOnServer(insertDataQueries);
        db.executeQueriesOnClient(insertDataQueries);


        scenario.setDataSources(db.getServerDs(), db.getClientDs());
        scenario.setConnections(db.getServerConnection(), db.getClientConnection());

        scenario.setSelectQueries(new String[]{
                "select * from categories order by id asc",
                "select * from categories_md order by pk asc"
            });
    }

    @Test
    public void executeTest() throws SQLException, ContextException, SyncException {

        scenario.executeSteps();

        scenario.saveCurrentStateAsExpectedServerResult();
        scenario.saveCurrentStateAsExpectedClientResult();

        scenario.synchronize(tableNames);

        scenario.assertBothSidesAreInExpectedState();

    }

    @After
    public void tearDown() throws SQLException {
        db.clean();
    }
}
