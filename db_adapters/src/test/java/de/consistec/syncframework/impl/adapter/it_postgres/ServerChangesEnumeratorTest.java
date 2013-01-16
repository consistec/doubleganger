package de.consistec.syncframework.impl.adapter.it_postgres;

import static org.junit.Assert.assertTrue;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.TableSyncStrategy;
import de.consistec.syncframework.common.Tuple;
import de.consistec.syncframework.common.adapter.DatabaseAdapterFactory;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.server.ServerChangesEnumerator;
import de.consistec.syncframework.impl.adapter.AbstractSyncTest;
import de.consistec.syncframework.impl.adapter.ConnectionType;
import de.consistec.syncframework.impl.adapter.DumpDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class tests the correct handling of getChanges for server side.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 13.12.12 15:10
 */
public class ServerChangesEnumeratorTest extends AbstractSyncTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerChangesEnumeratorTest.class.getCanonicalName());
    public static final String CONFIG_FILE = "/config_postgre.properties";
    protected static final DumpDataSource clientDs = new DumpDataSource(DumpDataSource.SupportedDatabases.POSTGRESQL,
        ConnectionType.CLIENT);
    protected static final DumpDataSource serverDs = new DumpDataSource(DumpDataSource.SupportedDatabases.POSTGRESQL,
        ConnectionType.SERVER);
    private Tuple<Integer, List<Change>> serverChanges;
    private TableSyncStrategies tableStrategies;

    @BeforeClass
    public static void setUpClass() throws Exception {
        clientConnection = clientDs.getConnection();
        serverConnection = serverDs.getConnection();
    }

    @Before
    public void setUp() throws IOException {
        Config.getInstance().loadFromFile(getClass().getResourceAsStream(CONFIG_FILE));
    }

    @Override
    public Connection getServerConnection() {
        return serverConnection;
    }

    @Override
    public Connection getClientConnection() {
        return clientConnection;
    }

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

    @Test
    public void getChangesServerToClient() throws ContextException, SyncException, DatabaseAdapterException,
        SQLException {
        initClientAndServerWithoutSync("category8_b_insert.xml", "category8_a_insert.xml");

        tableStrategies = setGlobalStrategy(ConflictStrategy.SERVER_WINS, SyncDirection.SERVER_TO_CLIENT);
        serverChanges = getChanges(tableStrategies);

        assertTrue(serverChanges.getValue2().size() == 1);
        assertTrue(serverChanges.getValue1() == 3);
    }

    @Test
    public void getChangesClientToServer() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        initClientAndServerWithoutSync("category8_b_insert.xml", "category8_a_insert.xml");

        tableStrategies = setGlobalStrategy(ConflictStrategy.CLIENT_WINS, SyncDirection.CLIENT_TO_SERVER);
        serverChanges = getChanges(tableStrategies);

        assertTrue(serverChanges.getValue2().isEmpty());
        assertTrue(serverChanges.getValue1() == 3);
    }

    @Test
    public void getChangesBidirectional() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        initClientAndServerWithoutSync("category8_b_insert.xml", "category8_a_insert.xml");

        tableStrategies = setGlobalStrategy(ConflictStrategy.CLIENT_WINS, SyncDirection.BIDIRECTIONAL);
        serverChanges = getChanges(tableStrategies);

        assertTrue(serverChanges.getValue2().size() == 1);
        assertTrue(serverChanges.getValue1() == 3);
    }

    @Test
    public void getChangesServerToClientPerTable() throws ContextException, SyncException, DatabaseAdapterException,
        SQLException {
        initClientAndServerWithoutSync("category9_b_insert.xml", "category9_a_insert.xml");

        tableStrategies = setStrategyForTable("categories", ConflictStrategy.SERVER_WINS, SyncDirection.SERVER_TO_CLIENT);
        serverChanges = getChanges(tableStrategies);

        assertTrue(serverChanges.getValue2().size() == 2);
        assertTrue(serverChanges.getValue1() == 3);
    }

    @Test
    public void getChangesClientToServerPerTable() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        initClientAndServerWithoutSync("category9_b_insert.xml", "category9_a_insert.xml");

        tableStrategies = setStrategyForTable("categories", ConflictStrategy.CLIENT_WINS, SyncDirection.CLIENT_TO_SERVER);
        serverChanges = getChanges(tableStrategies);

        assertTrue(serverChanges.getValue2().size() == 1);
        assertTrue(serverChanges.getValue1() == 3);
    }

    @Test
    public void getChangesBidirectionalPerTable() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        initClientAndServerWithoutSync("category9_b_insert.xml", "category9_a_insert.xml");

        tableStrategies = setStrategyForTable("categories", ConflictStrategy.CLIENT_WINS, SyncDirection.BIDIRECTIONAL);
        serverChanges = getChanges(tableStrategies);

        assertTrue(serverChanges.getValue2().size() == 2);
        assertTrue(serverChanges.getValue1() == 3);
    }

    private TableSyncStrategies setStrategyForTable(String tableName, ConflictStrategy strategy, SyncDirection direction) {
        TableSyncStrategies strategies = new TableSyncStrategies();
        TableSyncStrategy tablsSyncStrategy = new TableSyncStrategy(direction, strategy);
        strategies.addSyncStrategyForTable(tableName, tablsSyncStrategy);
        return strategies;
    }

    private TableSyncStrategies setGlobalStrategy(ConflictStrategy conflictStrategy, SyncDirection syncDirection) {
        Config configInstance = Config.getInstance();
        configInstance.setConflictStrategy(conflictStrategy);
        configInstance.setSyncDirection(syncDirection);
        return new TableSyncStrategies();
    }

    private Tuple<Integer, List<Change>> getChanges(TableSyncStrategies strategies)
        throws SyncException, ContextException, SQLException, DatabaseAdapterException {
        IDatabaseAdapter adapter = null;

        try {
            adapter = DatabaseAdapterFactory.newInstance(DatabaseAdapterFactory.AdapterPurpose.SERVER);

            ServerChangesEnumerator serverChangesEnumerator = new ServerChangesEnumerator(adapter, strategies);

            return serverChangesEnumerator.getChanges(1);

        } finally {
            if (adapter != null) {
                if (adapter.getConnection() != null) {
                    adapter.getConnection().close();
                }
            }
        }
    }
}
