package de.consistec.syncframework.impl.adapter;

import static de.consistec.syncframework.common.conflict.ConflictStrategy.FIRE_EVENT;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.SERVER_WINS;
import static de.consistec.syncframework.common.util.CollectionsUtil.newHashMap;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.IConflictListener;
import de.consistec.syncframework.common.SyncContext;
import de.consistec.syncframework.common.SyncContext.LocalContext;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.TableSyncStrategy;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for database adapters integrations tests.
 * This class hold instances of logger, junit test watcher, and loads logging facility's configuration.
 * <p/>
 * Methods names meaning:<ul>
 * <li><b><i>Uc</i></b> = Unchanged</li>
 * <li><b><i>Mod</i></b> = Modification</li>
 * <li><b><i>Del</i></b> = Delete</li>
 * <li><b><i>Add</i></b> = Add</li>
 * </ul>
 * <p/>
 *
 * @author markus
 * @company Consistec Engineering and Consulting GmbH
 * @date 19.04.12 15:47
 * @since 0.0.1-SNAPSHOT
 */
public abstract class AbstractSyncTest implements ISyncTests {

    // This test watcher prints a test's name before and after each test.
    @Rule
    public TestRule watchman2 = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            LOGGER.info("start {} for test class {} ...", description.getMethodName(),
                description.getTestClass().getCanonicalName());
        }

        @Override
        protected void finished(Description description) {
            LOGGER.info("end {} for test class {}", description.getMethodName(),
                description.getTestClass().getCanonicalName());
        }
    };
    String[] tableNames = new String[]{"categories_md", "categories", "items", "items_md"};
    String[] insertDataQueries = new String[]{
        "INSERT INTO categories (\"categoryid\", \"categoryname\", \"description\") "
        + "VALUES (1, 'Beverages', 'Soft drinks, coffees, teas, beers, and ales')",
        "INSERT INTO categories (\"categoryid\", \"categoryname\", \"description\") "
        + "VALUES (2, 'Condiments', 'Sweet and savory sauces, relishes, spreads, and seasonings')",
        "INSERT INTO categories (\"categoryid\", \"categoryname\", \"description\") "
        + "VALUES (3, 'Confections', 'Desserts, candies, and sweet breads')",
        "INSERT INTO categories (\"categoryid\", \"categoryname\", \"description\") "
        + "VALUES (4, 'Dairy Products', 'Cheeses')",
        "INSERT INTO categories (\"categoryid\", \"categoryname\", \"description\") "
        + "VALUES (5, 'Grains', 'Breads, crackers, pasta, and cereal')",
        "INSERT INTO categories (\"categoryid\", \"categoryname\", \"description\") "
        + "VALUES (6, 'Cat6a', 'uhhhhhhh 6a')",
        "INSERT INTO categories_md (\"rev\", \"mdv\", \"pk\", \"f\") "
        + "VALUES (1, 'B4F135B634EDA2894254E5205F401E90', 1, 0)",
        "INSERT INTO categories_md (\"rev\", \"mdv\", \"pk\", \"f\") "
        + "VALUES (1, 'B9CBF2C3AA1964E3A752F4C34E07369D', 2, 0)",
        "INSERT INTO categories_md (\"rev\", \"mdv\", \"pk\", \"f\") "
        + "VALUES (1, 'A359E8A01ED93D37C0BF6DB13CC74488', 3, 0)",
        "INSERT INTO categories_md (\"rev\", \"mdv\", \"pk\", \"f\") "
        + "VALUES (1, '0DA3D1A2A2539E864D2D1B6636898395', 4, 0)",
        "INSERT INTO categories_md (\"rev\", \"mdv\", \"pk\", \"f\") "
        + "VALUES (1, 'A52D87B86798B317A7C1C01837290D2F', 5, 0)",
        "INSERT INTO categories_md (\"rev\", \"mdv\", \"pk\", \"f\") "
        + "VALUES (1, '0D9F6F55D5BF5190D2B8C1105AE21325', 6, 0)"
    };
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSyncTest.class.getCanonicalName());
    protected static final Config CONF = Config.getInstance();
    protected static Connection clientConnection;
    protected static Connection serverConnection;
    private final transient ExecuteStatementHelper helper;

    public AbstractSyncTest() {
        // initialize logging framework
        DOMConfigurator.configure(ClassLoader.getSystemResource("log4j.xml"));
        helper = new ExecuteStatementHelper(getClientConnection(), getServerConnection());
    }

    /**
     * Closes server and client connection.
     *
     * @throws SQLException
     */
    @AfterClass
    public static void tearDownClass() throws SQLException {

        if (clientConnection != null) {
            try {
                clientConnection.close();
                clientConnection = null;
            } catch (SQLException e) {
                LOGGER.error("could not close client connection!", e);
                throw e;
            }
        }

        if (serverConnection != null) {
            try {
                serverConnection.close();
                serverConnection = null;
            } catch (SQLException e) {
                LOGGER.warn("could not close server connection!", e);
                throw e;
            }
        }
    }

    /**
     * Populates client and server database with test insertDataQueries.
     *
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    public void populateWithTestData() throws SyncException, ContextException, SQLException {

        helper.dropTablesOnServer(tableNames);
        helper.dropTablesOnClient(tableNames);

        helper.executeQueriesOnServer(getCreateTableQueries());
        syncWithoutCompare(SERVER_WINS);

        helper.executeQueriesOnServer(insertDataQueries);
        helper.executeQueriesOnClient(insertDataQueries);
    }

    /**
     * @param resourceName
     * @return
     */
    @Override
    public InputStream getResourceAsStream(String resourceName) {
        return ClassLoader.getSystemResourceAsStream(resourceName);
    }


    protected String[] getCreateTableQueries() {
        return new String[]{
                "create table categories (\"categoryid\" INTEGER NOT NULL PRIMARY KEY ,\"categoryname\" VARCHAR (30000),\"description\" VARCHAR (30000));",
                "create table items (\"itemid\" INTEGER NOT NULL PRIMARY KEY ,\"itemname\" VARCHAR (30000),\"description\" VARCHAR (30000));"};
    }

    protected void initAndSyncClient(String query, SyncDirection syncDirection, ConflictStrategy strategy,
        ConnectionType type) throws SyncException, SQLException, ContextException {
        initAndSyncClient(query, syncDirection, strategy, type, null);
    }

    protected void initAndSyncServer(String query, SyncDirection syncDirection, ConflictStrategy strategy,
        ConnectionType type) throws SyncException, SQLException, ContextException {
        initAndSyncServer(query, syncDirection, strategy, type, null);
    }

    protected void initAndSyncClient(String query, SyncDirection syncDirection, ConflictStrategy strategy,
        ConnectionType type, ConnectionType type2) throws SyncException, SQLException, ContextException {

        populateWithTestData();
        helper.executeUpdateOnClient(query);
        sync(syncDirection, strategy, type, type2);
    }

    protected void initAndSyncServer(String query, SyncDirection syncDirection, ConflictStrategy strategy,
        ConnectionType type, ConnectionType type2) throws SyncException, SQLException, ContextException {

        populateWithTestData();
        helper.executeUpdateOnServer(query);
        sync(syncDirection, strategy, type, type2);
    }

    protected void initClientAndServerWithSync(String query1, String query2, SyncDirection syncDirection,
        ConflictStrategy strategy, ConnectionType type) throws
        SyncException, SQLException, ContextException {
        populateWithTestData();
        helper.executeUpdateOnServer(query1);
        helper.executeUpdateOnClient(query2);

        sync(syncDirection, strategy, type);
    }

    protected void initClientAndServerWithoutSync(String query1, String query2) throws
        SyncException, SQLException, ContextException {
        populateWithTestData();
        helper.executeUpdateOnServer(query1);
        helper.executeUpdateOnClient(query2);

    }

    protected void initClientAndServerAndSync(String resource, SyncDirection syncDirection, ConflictStrategy strategy,
        ConnectionType type) throws SyncException,
        SQLException, ContextException {
        initClientAndServerWithSync(resource, resource, syncDirection, strategy, type);
    }

    /**
     * Sync.
     *
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    public void sync(ConnectionType type) throws SyncException, SQLException, ContextException {
        sync(SyncDirection.BIDIRECTIONAL, SERVER_WINS, type);
    }

    // was already ignored
    /**
     * Sync.
     *
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    public void sync() throws SyncException, SQLException, ContextException {
        sync(SyncDirection.BIDIRECTIONAL, SERVER_WINS);
    }

    /**
     * @param strategy
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    public void sync(final SyncDirection direction, final ConflictStrategy strategy, final ConnectionType type) throws
        SyncException, SQLException,
        ContextException {

        sync(direction, strategy);

        compareDatabases(type);
    }

    /**
     * @param strategy
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    public void sync(final SyncDirection direction, final ConflictStrategy strategy, final ConnectionType type,
        final ConnectionType type2) throws
        SyncException, SQLException,
        ContextException {

        sync(direction, strategy);

        compareDatabases(type, type2);
    }

    /**
     * @param strategy
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    public void sync(final SyncDirection direction, final ConflictStrategy strategy) throws
        SyncException, SQLException,
        ContextException {

        TableSyncStrategy tableSyncStrategy = new TableSyncStrategy(direction, strategy);

        TableSyncStrategies strategies = new TableSyncStrategies();
        strategies.addSyncStrategyForTable("categories", tableSyncStrategy);

        final LocalContext localCtx = SyncContext.local(getServerDataSource(), getClientDataSource(), strategies);

        if (strategy == FIRE_EVENT) {
            localCtx.setConflictListener(new IConflictListener() {
                @Override
                public Map<String, Object> resolve(Map<String, Object> serverData, Map<String, Object> clientData) {
                    return serverData;
                }
            });
        }

        readTableContentBeforeSync();

        localCtx.synchronize();
    }

    /**
     * @param strategy
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    public void syncWithoutCompare(final ConflictStrategy strategy) throws SyncException, SQLException,
        ContextException {

        final LocalContext localCtx = SyncContext.local(getServerDataSource(), getClientDataSource());

        if (strategy == FIRE_EVENT) {
            localCtx.setConflictListener(new IConflictListener() {
                @Override
                public Map<String, Object> resolve(Map<String, Object> serverData, Map<String, Object> clientData) {
                    return serverData;
                }
            });
        }

        localCtx.synchronize();
    }

//    /**
//     * @param strategy
//     * @throws SyncException
//     * @throws SQLException
//     * @throws ContextException
//     */
//    public void syncWithoutCompare(final ConflictStrategy strategy) throws SyncException, SQLException,
//        ContextException {
//
//        final LocalContext localCtx = SyncContext.LocalContext.create(getServerDataSource(), getClientDataSource());
//
//        if (strategy == FIRE_EVENT) {
//            localCtx.setConflictListener(new IConflictListener() {
//                @Override
//                public Map<String, Object> resolve(Map<String, Object> serverData, Map<String, Object> clientData) {
//                    return serverData;
//                }
//            });
//        }
//
//        localCtx.synchronize();
//    }
    private Map<String, String> initTestTableStatementMap() {
        Map<String, String> statementsToExecute = newHashMap();
        statementsToExecute.put("categories", "select * from categories order by categoryid asc");
        statementsToExecute.put("items", "select * from items order by itemid asc");
        statementsToExecute.put(String.format("categories%s", CONF.getMdTableSuffix()),
            String.format("select pk, mdv, rev from categories%s  order by pk asc", CONF.getMdTableSuffix()));
        statementsToExecute.put(String.format("items%s", CONF.getMdTableSuffix()),
            String.format("select pk, mdv, rev from items%s order by pk asc", CONF.getMdTableSuffix()));
        return statementsToExecute;
    }

    private void readTableContentBeforeSync() throws SQLException {
        Map<String, String> tableStatementMap = initTestTableStatementMap();
        helper.readTableContent(tableStatementMap);
    }

    /**
     * Compare databases.
     */
    private void compareDatabases(ConnectionType type) throws
        SyncException, SQLException {
        Map<String, String> statementsToExecute = initTestTableStatementMap();

        helper.executeStatementAndCompareResults(statementsToExecute,
            new ResultSetComparator(), CONF, type);
    }

    /**
     * Compare databases.
     */
    private void compareDatabases(ConnectionType type, ConnectionType type2) throws
        SyncException, SQLException {
        Map<String, String> statementsToExecute = initTestTableStatementMap();

        helper.executeStatementAndCompareResults(statementsToExecute,
            new ResultSetComparator(), CONF, type, type2);
    }

    @Override
    public DataSource getClientDataSource() {
        return null;
    }

    @Override
    public DataSource getServerDataSource() {
        return null;
    }
}
