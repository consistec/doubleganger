package de.consistec.syncframework.impl.adapter.it_postgres;

import static org.junit.Assert.assertTrue;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.TableSyncStrategy;
import de.consistec.syncframework.common.adapter.DatabaseAdapterFactory;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.client.ClientChangesEnumerator;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
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
 * This class tests the correct handling of getChanges for client side.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 13.12.12 12:16
 */
public class ClientChangesEnumeratorTest extends AbstractSyncTest {

//<editor-fold defaultstate="expanded" desc=" Class fields " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class constructors " >

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class methods " >

//</editor-fold>

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientChangesEnumeratorTest.class.getCanonicalName());

    public static final String CONFIG_FILE = "/config_postgre.properties";

    protected static final DumpDataSource clientDs = new DumpDataSource(DumpDataSource.SupportedDatabases.POSTGRESQL,
        ConnectionType.CLIENT);
    protected static final DumpDataSource serverDs = new DumpDataSource(DumpDataSource.SupportedDatabases.POSTGRESQL,
        ConnectionType.SERVER);

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

    /**
     * Closes server and client connection.
     *
     * @throws java.sql.SQLException
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

    private List<Change> testGetChangesGlobal(ConflictStrategy strategy, SyncDirection direction) throws SyncException,
        ContextException, SQLException, DatabaseAdapterException {
        String resource = "category8_b_insert.xml";
        String resource2 = "category8_a_insert.xml";
        IDatabaseAdapter adapter = null;
        List<Change> clientChanges = null;

        try {
            // init db with data
            initClientAndServerWithoutSync(resource, resource2);

            adapter = DatabaseAdapterFactory.newInstance(DatabaseAdapterFactory.AdapterPurpose.CLIENT);

//            TableSyncStrategies strategies = new TableSyncStrategies();
//            strategies.addSyncStrategyForTable();

            Config configInstance = Config.getInstance();
            configInstance.setConflictStrategy(strategy);
            configInstance.setSyncDirection(direction);
            ClientChangesEnumerator clientChangesEnumerator = new ClientChangesEnumerator(adapter,
                new TableSyncStrategies());

            clientChanges = clientChangesEnumerator.getChanges();

        } finally {
            if (adapter != null) {
                if (adapter.getConnection() != null) {
                    adapter.getConnection().close();
                }
            }
        }
        return clientChanges;
    }

    private List<Change> testGetChangesPerTable(ConflictStrategy strategy, SyncDirection direction) throws
        SyncException,
        ContextException, SQLException, DatabaseAdapterException {
        String resource = "category9_b_insert.xml";
        String resource2 = "category9_a_insert.xml";
        IDatabaseAdapter adapter = null;
        List<Change> clientChanges = null;

        try {
            // init db with data
            initClientAndServerWithoutSync(resource, resource2);

            adapter = DatabaseAdapterFactory.newInstance(DatabaseAdapterFactory.AdapterPurpose.CLIENT);

            TableSyncStrategies strategies = new TableSyncStrategies();
            TableSyncStrategy tablsSyncStrategy = new TableSyncStrategy(direction, strategy);
            strategies.addSyncStrategyForTable("categories", tablsSyncStrategy);

            ClientChangesEnumerator clientChangesEnumerator = new ClientChangesEnumerator(adapter, strategies);

            clientChanges = clientChangesEnumerator.getChanges();

        } finally {
            if (adapter != null) {
                if (adapter.getConnection() != null) {
                    adapter.getConnection().close();
                }
            }
        }
        return clientChanges;
    }

    @Test
    public void getChangesServerToClient() throws ContextException, SyncException, DatabaseAdapterException,
        SQLException

    {
        List<Change> clientChanges = testGetChangesGlobal(ConflictStrategy.SERVER_WINS, SyncDirection.SERVER_TO_CLIENT);
        assertTrue(clientChanges.size() == 0);
    }

    @Test
    public void getChangesClientToServer() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        List<Change> clientChanges = testGetChangesGlobal(ConflictStrategy.CLIENT_WINS, SyncDirection.CLIENT_TO_SERVER);
        assertTrue(clientChanges.size() == 1);
    }

    @Test
    public void getChangesBidirectional() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        List<Change> clientChanges = testGetChangesGlobal(ConflictStrategy.CLIENT_WINS, SyncDirection.BIDIRECTIONAL);
        assertTrue(clientChanges.size() == 1);
    }

    @Test
    public void getChangesServerToClientPerTable() throws ContextException, SyncException, DatabaseAdapterException,
        SQLException

    {
        List<Change> clientChanges = testGetChangesPerTable(ConflictStrategy.SERVER_WINS,
            SyncDirection.SERVER_TO_CLIENT);
        assertTrue(clientChanges.size() == 1);
    }

    @Test
    public void getChangesClientToServerPerTable() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        List<Change> clientChanges = testGetChangesPerTable(ConflictStrategy.CLIENT_WINS,
            SyncDirection.CLIENT_TO_SERVER);
        assertTrue(clientChanges.size() == 2);
    }

    @Test
    public void getChangesBidirectionalPerTable() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        List<Change> clientChanges = testGetChangesPerTable(ConflictStrategy.CLIENT_WINS, SyncDirection.BIDIRECTIONAL);
        assertTrue(clientChanges.size() == 2);
    }
}
