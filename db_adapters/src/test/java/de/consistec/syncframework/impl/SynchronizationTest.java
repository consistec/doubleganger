package de.consistec.syncframework.impl;

import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;

import static de.consistec.syncframework.common.SyncDirection.BIDIRECTIONAL;
import static de.consistec.syncframework.common.SyncDirection.CLIENT_TO_SERVER;
import static de.consistec.syncframework.common.SyncDirection.SERVER_TO_CLIENT;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.CLIENT_WINS;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.SERVER_WINS;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.FIRE_EVENT;
import static de.consistec.syncframework.impl.adapter.ConnectionType.CLIENT;
import static de.consistec.syncframework.impl.adapter.ConnectionType.SERVER;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.IConflictListener;
import de.consistec.syncframework.common.SyncContext;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.TableSyncStrategy;
import de.consistec.syncframework.impl.adapter.AbstractSyncTest;
import de.consistec.syncframework.impl.adapter.ConnectionType;
import de.consistec.syncframework.impl.adapter.ExecuteStatementHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;

import de.consistec.syncframework.impl.adapter.DumpDataSource;
import java.io.IOException;
import java.sql.BatchUpdateException;
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
    private transient ExecuteStatementHelper helper = null;
    TestScenario scenario;
    static String deleteLastRow = "DELETE FROM categories WHERE id = 6";
    static String updateLastRow = "UPDATE categories SET id = 6, name = 'Cat6b', description = '6b' WHERE id = 6";
    static String insertNewRow = "INSERT INTO categories (id, name, description) VALUES (7, 'Cat7a', '7a')";
    static String updateNewRow = "UPDATE categories SET id = 7, name = 'Cat7b', description = '7b' WHERE id = 7";
    static String deleteNewRow = "DELETE FROM categories WHERE id = 7";
    static String[] tableNames = new String[]{"categories_md", "categories"};
    static String[] insertDataQueries = new String[]{
        "INSERT INTO categories (\"id\", \"name\", \"description\") VALUES (1, 'Beverages', 'Soft drinks')",
        "INSERT INTO categories (\"id\", \"name\", \"description\") VALUES (2, 'Condiments', 'Sweet and ')",
        "INSERT INTO categories (\"id\", \"name\", \"description\") VALUES (3, 'Confections', 'Desserts')",
        "INSERT INTO categories (\"id\", \"name\", \"description\") VALUES (4, 'Dairy Products', 'Cheeses')",
        "INSERT INTO categories (\"id\", \"name\", \"description\") VALUES (5, 'Grains', 'Breads, crackers')",
        "INSERT INTO categories (\"id\", \"name\", \"description\") VALUES (6, 'Cat6a', '6a')",
        "INSERT INTO categories_md (\"rev\", \"mdv\", \"pk\", \"f\") VALUES (1, 'B4F135B634EDA2894254E5205F401E90', 1, 0)",
        "INSERT INTO categories_md (\"rev\", \"mdv\", \"pk\", \"f\") VALUES (1, 'B9CBF2C3AA1964E3A752F4C34E07369D', 2, 0)",
        "INSERT INTO categories_md (\"rev\", \"mdv\", \"pk\", \"f\") VALUES (1, 'A359E8A01ED93D37C0BF6DB13CC74488', 3, 0)",
        "INSERT INTO categories_md (\"rev\", \"mdv\", \"pk\", \"f\") VALUES (1, '0DA3D1A2A2539E864D2D1B6636898395', 4, 0)",
        "INSERT INTO categories_md (\"rev\", \"mdv\", \"pk\", \"f\") VALUES (1, 'A52D87B86798B317A7C1C01837290D2F', 5, 0)",
        "INSERT INTO categories_md (\"rev\", \"mdv\", \"pk\", \"f\") VALUES (1, '0D9F6F55D5BF5190D2B8C1105AE21325', 6, 0)"
    };
    // POSTGRESQL
    public static final String CONFIG_FILE = "/config_postgre.properties";
    protected static final DumpDataSource serverDs = new DumpDataSource(DumpDataSource.SupportedDatabases.POSTGRESQL,
        ConnectionType.SERVER);
    protected static final DumpDataSource clientDs = new DumpDataSource(DumpDataSource.SupportedDatabases.POSTGRESQL,
        ConnectionType.CLIENT);
    protected static Connection serverConnection, clientConnection;

    @Parameterized.Parameters
    public static Collection<TestScenario[]> AllScenarii() {
        TestScenario[][] scenarii = new TestScenario[][]{
            {new TestScenario("Unchanged Unchanged", BIDIRECTIONAL, SERVER_WINS, CLIENT)},
            {new TestScenario("Add Unchanged", BIDIRECTIONAL, SERVER_WINS, CLIENT).addStep(CLIENT, insertNewRow)},
            {new TestScenario("Add Unchanged", BIDIRECTIONAL, CLIENT_WINS, CLIENT).addStep(CLIENT, insertNewRow)},
            {new TestScenario("Add Unchanged", CLIENT_TO_SERVER, CLIENT_WINS, CLIENT).addStep(CLIENT, insertNewRow)},
            {new TestScenario("Add Unchanged", SERVER_TO_CLIENT, SERVER_WINS, SERVER).addStep(CLIENT, insertNewRow)},
            {new TestScenario("Add Unchanged", BIDIRECTIONAL, FIRE_EVENT, CLIENT).addStep(CLIENT, insertNewRow)}};
        return Arrays.asList(scenarii);
    }

    public SynchronizationTest(TestScenario scenario) {
        this.scenario = scenario;
    }

    @BeforeClass
    public static void initClass() throws SQLException {
        // initialize logging framework
        DOMConfigurator.configure(ClassLoader.getSystemResource("log4j.xml"));
        serverConnection = serverDs.getConnection();
        clientConnection = clientDs.getConnection();
    }

    @Before
    public void init() throws SyncException, ContextException, SQLException, IOException {
        Config.getInstance().loadFromFile(getClass().getResourceAsStream(CONFIG_FILE));
        helper = new ExecuteStatementHelper(serverConnection, clientConnection);

        helper.dropTablesOnServer(tableNames);
        helper.dropTablesOnClient(tableNames);

        helper.executeQueriesOnServer(getCreateTableQueries());
        helper.executeQueriesOnClient(getCreateTableQueries());

        helper.executeQueriesOnServer(insertDataQueries);
        helper.executeQueriesOnClient(insertDataQueries);

        LOGGER.debug("\n---------------------\n" + scenario.toString() + "\n----------------------\n");
    }

    @Test
    public void executeTest() throws SQLException, ContextException, SyncException {
        scenario.setDataSources(serverDs, clientDs);
        
        scenario.setSelectQueries(new String[]{
                "select * from categories order by id asc",
                "select * from categories_md order by pk asc"
            });

        scenario.executeSteps();

        scenario.synchronize(tableNames);

        scenario.assertBothSidesAreInExpectedState();

    }

    @AfterClass
    public static void tearDownClass() throws SQLException {
        for (Connection connection : serverDs.getCreatedConnections()) {
            connection.close();
        }
        for (Connection connection : clientDs.getCreatedConnections()) {
            connection.close();
        }
    }

    public Connection getServerConnection() {
        return serverConnection;
    }

    public Connection getClientConnection() {
        return clientConnection;
    }

    // POSTGRESQL
    protected String[] getCreateTableQueries() {
        return new String[]{
                "create table categories (\"id\" INTEGER NOT NULL PRIMARY KEY ,\"name\" VARCHAR (300),\"description\" VARCHAR (300));",
                "create table categories_md (\"pk\" INTEGER NOT NULL PRIMARY KEY, \"mdv\" VARCHAR (300), \"rev\" INTEGER DEFAULT 1, \"f\" INTEGER DEFAULT 0);"};
    }
}
