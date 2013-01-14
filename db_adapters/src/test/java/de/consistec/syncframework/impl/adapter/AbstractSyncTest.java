package de.consistec.syncframework.impl.adapter;

import static de.consistec.syncframework.common.conflict.ConflictStrategy.FIRE_EVENT;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.SERVER_WINS;
import static de.consistec.syncframework.common.util.CollectionsUtil.newHashMap;
import static de.consistec.syncframework.impl.adapter.ConnectionType.CLIENT;
import static de.consistec.syncframework.impl.adapter.ConnectionType.SERVER;

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
import de.consistec.syncframework.impl.ResultSetHelper;

import java.io.IOException;
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
public abstract class AbstractSyncTest implements ISyncIntegrationTest {

    /**
     * Test watcher, with methods invoked before and after of each tests.
     * This "watcher" prints test name populateWithTestData and after each test.
     */
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
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSyncTest.class.getCanonicalName());
    /**
     * Framework configuration singleton.
     */
    protected static final Config CONF = Config.getInstance();
    /**
     * Jdbc connection for client database.
     * Use this connection to prepare the data for tests.
     */
    protected static Connection clientConnection;
    /**
     * Jdbc connection for server database.
     * Use this connection to prepare the data for tests.
     */
    protected static Connection serverConnection;
    private final transient ExecuteStatementHelper helper;

    /**
     * This constructor initialize log4j framework.
     */
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
     * Populates client and server database with test data.
     *
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    public void populateWithTestData() throws SyncException, SQLException, ContextException {
        InputStream stream = null;
        try {
            resetClientAndServerDatabase();
            stream = getResourceAsStream("server_data.xml");
            helper.executeUpdate(SERVER, stream);

            stream = getResourceAsStream("client_data.xml");
            helper.executeUpdate(CLIENT, stream);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    LOGGER.warn("Cannot close server input stream!");
                }
            }
        }
    }

    /**
     * @param resourceName
     * @return
     */
    @Override
    public InputStream getResourceAsStream(String resourceName) {
        return ClassLoader.getSystemResourceAsStream(resourceName);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Override
    public void resetClientAndServerDatabase() throws SyncException, SQLException, ContextException {
        Statement stmt = null;
        String[] tables = new String[]{"categories_md", "categories", "items", "items_md"};

        try {
            LOGGER.info("Dropping tables...");

            helper.createAndExecuteDropBatch(SERVER, tables);
            helper.createAndExecuteDropBatch(ConnectionType.CLIENT, tables);

            LOGGER.info("Creating tables...");

            helper.createAndExecuteBatch(SERVER, getCreateTableQueries());

            syncWithoutCompare(SERVER_WINS);

        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    /**
     * @return CREATE TABLE sql statements for test tables.
     */
    protected String[] getCreateTableQueries() {
        return new String[]{
            "create table categories (\"categoryid\" INTEGER NOT NULL PRIMARY KEY ,\"categoryname\" VARCHAR (30000),\"description\" VARCHAR (30000));",
            "create table items (\"itemid\" INTEGER NOT NULL PRIMARY KEY ,\"itemname\" VARCHAR (30000),\"description\" VARCHAR (30000));"};
    }

    protected void initAndSync(String resource, SyncDirection syncDirection, ConflictStrategy strategy,
                               ConnectionType dbToUpdate, ConnectionType type, ConnectionType type2
    ) throws SyncException,
        SQLException, ContextException {
        InputStream stream = null;
        try {
            populateWithTestData();
            stream = getResourceAsStream(resource);
            helper.executeUpdate(dbToUpdate, stream);

            sync(syncDirection, strategy, type, type2);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    LOGGER.warn("Cannot close input stream!");
                }
            }
        }
    }

    protected void initAndSync(String resource, SyncDirection syncDirection, ConflictStrategy strategy,
                               ConnectionType dbToUpdate, ConnectionType type
    ) throws SyncException,
        SQLException, ContextException {
        InputStream stream = null;
        try {
            populateWithTestData();
            stream = getResourceAsStream(resource);
            helper.executeUpdate(dbToUpdate, stream);

            sync(syncDirection, strategy, type);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    LOGGER.warn("Cannot close input stream!");
                }
            }
        }
    }

    protected void initClientAndServerWithSync(String resource1, String resource2, SyncDirection syncDirection,
                                               ConflictStrategy strategy, ConnectionType type
    ) throws
        SyncException, SQLException, ContextException {
        InputStream stream = null;
        try {
            populateWithTestData();
            stream = getResourceAsStream(resource1);
            helper.executeUpdate(SERVER, stream);
            stream = getResourceAsStream(resource2);
            helper.executeUpdate(CLIENT, stream);

            sync(syncDirection, strategy, type);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    LOGGER.warn("Cannot close input stream!");
                }
            }
        }
    }

    protected void initClientAndServerWithoutSync(String resource1, String resource2) throws
        SyncException, SQLException, ContextException {
        InputStream stream = null;
        try {
            populateWithTestData();
            stream = getResourceAsStream(resource1);
            helper.executeUpdate(SERVER, stream);
            stream = getResourceAsStream(resource2);
            helper.executeUpdate(CLIENT, stream);

        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    LOGGER.warn("Cannot close input stream!");
                }
            }
        }
    }

    protected void initClientAndServerAndSync(String resource, SyncDirection syncDirection, ConflictStrategy strategy,
                                              ConnectionType type
    ) throws SyncException,
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
                     final ConnectionType type2
    ) throws
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

        helper.executeStatementAndCompareResults(statementsToExecute, CONF, type);
    }

    /**
     * Compare databases.
     */
    private void compareDatabases(ConnectionType type, ConnectionType type2) throws
        SyncException, SQLException {
        Map<String, String> statementsToExecute = initTestTableStatementMap();

        helper.executeStatementAndCompareResults(statementsToExecute, CONF, type, type2);
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
