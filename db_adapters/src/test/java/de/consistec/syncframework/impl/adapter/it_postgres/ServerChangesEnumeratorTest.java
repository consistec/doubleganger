package de.consistec.syncframework.impl.adapter.it_postgres;

import static de.consistec.syncframework.common.SyncDirection.BIDIRECTIONAL;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.SERVER_WINS;
import static org.junit.Assert.assertTrue;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.SyncData;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.TableSyncStrategy;
import de.consistec.syncframework.common.adapter.DatabaseAdapterFactory;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.server.ServerChangesEnumerator;

import java.io.IOException;
import java.sql.SQLException;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the correct handling of getChanges for server side.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 13.12.12 15:10
 */
public class ServerChangesEnumeratorTest extends EnumeratorTest {

    private static String[] serverInsertQueries = new String[]{
        "INSERT INTO categories (categoryid, categoryname, description) VALUES (1, 'Beverages', 'Soft drinks')",
        "INSERT INTO categories (categoryid, categoryname, description) VALUES (2, 'Condiments', 'Sweet and ')",
        "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (1, '8F3CCBD3FE5C9106253D472F6E36F0E1', 1, 0)",
        "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (2, '75901F57520C09EB990837C7AA93F717', 2, 0)",};


    @Before
    @Override
    public void setUp() throws IOException, SQLException {
        super.setUp();

        postgresDB.executeQueriesOnServer(serverInsertQueries);

    }

    private SyncData testGetChangesGlobal(ConflictStrategy strategy, SyncDirection direction) throws
        SyncException,
        ContextException, SQLException, DatabaseAdapterException {

        Config configInstance = Config.getInstance();
        configInstance.setGlobalConflictStrategy(strategy);
        configInstance.setGlobalSyncDirection(direction);

        IDatabaseAdapter adapter = null;
        try {
            adapter = DatabaseAdapterFactory.newInstance(DatabaseAdapterFactory.AdapterPurpose.SERVER);
            ServerChangesEnumerator serverChangesEnumerator = new ServerChangesEnumerator(adapter,
                new TableSyncStrategies());

            return serverChangesEnumerator.getChanges(1);
        } finally {
            if (adapter != null) {
                if (adapter.getConnection() != null) {
                    adapter.getConnection().close();
                }
            }
        }
    }

    private SyncData testGetChangesPerTable(ConflictStrategy strategy, SyncDirection direction
    ) throws
        SyncException,
        ContextException, SQLException, DatabaseAdapterException {

        IDatabaseAdapter adapter = null;

        try {
            adapter = DatabaseAdapterFactory.newInstance(DatabaseAdapterFactory.AdapterPurpose.SERVER);

            TableSyncStrategies strategies = new TableSyncStrategies();
            TableSyncStrategy tablsSyncStrategy = new TableSyncStrategy(direction, strategy);
            strategies.addSyncStrategyForTable("categories", tablsSyncStrategy);

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

    @Test
    public void getChangesServerToClient() throws ContextException, SyncException, DatabaseAdapterException,
        SQLException

    {
        SyncData serverChanges = testGetChangesGlobal(SERVER_WINS,
            SyncDirection.SERVER_TO_CLIENT);

        assertTrue(serverChanges.getChanges().size() == 1);
        assertTrue(serverChanges.getRevision() == 2);
    }

    @Test
    public void getChangesClientToServer() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        SyncData serverChanges = testGetChangesGlobal(ConflictStrategy.CLIENT_WINS,
            SyncDirection.CLIENT_TO_SERVER);

        assertTrue(serverChanges.getChanges().size() == 0);
        assertTrue(serverChanges.getRevision() == 2);
    }

    @Test
    public void getChangesBidirectional() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        SyncData serverChanges = testGetChangesGlobal(ConflictStrategy.CLIENT_WINS,
            BIDIRECTIONAL);

        assertTrue(serverChanges.getChanges().size() == 1);
        assertTrue(serverChanges.getRevision() == 2);
    }

    @Test
    public void getChangesServerToClientPerTable() throws ContextException, SyncException, DatabaseAdapterException,
        SQLException

    {
        SyncData serverChanges = testGetChangesPerTable(SERVER_WINS,
            SyncDirection.SERVER_TO_CLIENT);

        assertTrue(serverChanges.getChanges().size() == 1);
        assertTrue(serverChanges.getRevision() == 2);
    }

    @Test
    public void getChangesClientToServerPerTable() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        SyncData serverChanges = testGetChangesPerTable(ConflictStrategy.CLIENT_WINS,
            SyncDirection.CLIENT_TO_SERVER);

        assertTrue(serverChanges.getChanges().size() == 0);
        assertTrue(serverChanges.getRevision() == 2);
    }

    @Test
    public void getChangesBidirectionalPerTable() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        SyncData serverChanges = testGetChangesPerTable(ConflictStrategy.CLIENT_WINS,
            BIDIRECTIONAL);

        assertTrue(serverChanges.getChanges().size() == 1);
        assertTrue(serverChanges.getRevision() == 2);
    }
}
