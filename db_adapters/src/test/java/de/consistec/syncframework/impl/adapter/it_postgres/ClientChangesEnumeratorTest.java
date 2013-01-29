package de.consistec.syncframework.impl.adapter.it_postgres;

import static org.junit.Assert.assertTrue;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.SyncData;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.TableSyncStrategy;
import de.consistec.syncframework.common.adapter.DatabaseAdapterFactory;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.client.ClientChangesEnumerator;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;

import java.io.IOException;
import java.sql.SQLException;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the correct handling of getChanges for client side.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 13.12.12 12:16
 */
public class ClientChangesEnumeratorTest extends EnumeratorTest {

    private static String[] serverInsertQueries = new String[]{
        "INSERT INTO categories (categoryid, categoryname, description) VALUES (1, 'Beverages', 'Soft drinks')",
        "INSERT INTO categories (categoryid, categoryname, description) VALUES (2, 'Condiments', 'Sweet and ')",
        "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (1, '8F3CCBD3FE5C9106253D472F6E36F0E1', 1, 1)",
        "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (2, '75901F57520C09EB990837C7AA93F717', 2, 1)",};

    @Before
    @Override
    public void setUp() throws IOException, SQLException {
        super.setUp();

        postgresDB.executeQueriesOnClient(serverInsertQueries);
    }

    private SyncData testGetChangesGlobal(ConflictStrategy strategy, SyncDirection direction) throws SyncException,
        ContextException, SQLException, DatabaseAdapterException {
        IDatabaseAdapter adapter = null;

        try {
            adapter = DatabaseAdapterFactory.newInstance(DatabaseAdapterFactory.AdapterPurpose.CLIENT);

            Config configInstance = Config.getInstance();
            configInstance.setGlobalConflictStrategy(strategy);
            configInstance.setGlobalSyncDirection(direction);

            ClientChangesEnumerator clientChangesEnumerator = new ClientChangesEnumerator(adapter,
                new TableSyncStrategies());

            return clientChangesEnumerator.getChanges();

        } finally {
            if (adapter != null) {
                if (adapter.getConnection() != null) {
                    adapter.getConnection().close();
                }
            }
        }
    }

    private SyncData testGetChangesPerTable(ConflictStrategy strategy, SyncDirection direction) throws
        SyncException,
        ContextException, SQLException, DatabaseAdapterException {
        IDatabaseAdapter adapter = null;

        try {

            adapter = DatabaseAdapterFactory.newInstance(DatabaseAdapterFactory.AdapterPurpose.CLIENT);

            TableSyncStrategies strategies = new TableSyncStrategies();
            TableSyncStrategy tablsSyncStrategy = new TableSyncStrategy(direction, strategy);
            strategies.addSyncStrategyForTable("categories", tablsSyncStrategy);

            ClientChangesEnumerator clientChangesEnumerator = new ClientChangesEnumerator(adapter, strategies);

            return clientChangesEnumerator.getChanges();

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
        SyncData clientChanges = testGetChangesGlobal(ConflictStrategy.SERVER_WINS, SyncDirection.SERVER_TO_CLIENT);
        assertTrue(clientChanges.getChanges().isEmpty());
    }

    @Test
    public void getChangesClientToServer() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        SyncData clientChanges = testGetChangesGlobal(ConflictStrategy.CLIENT_WINS, SyncDirection.CLIENT_TO_SERVER);
        assertTrue(clientChanges.getChanges().size() == 2);
    }

    @Test
    public void getChangesBidirectional() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        SyncData clientChanges = testGetChangesGlobal(ConflictStrategy.CLIENT_WINS, SyncDirection.BIDIRECTIONAL);
        assertTrue(clientChanges.getChanges().size() == 2);
    }

    @Test
    public void getChangesServerToClientPerTable() throws ContextException, SyncException, DatabaseAdapterException,
        SQLException

    {
        SyncData clientChanges = testGetChangesPerTable(ConflictStrategy.SERVER_WINS,
            SyncDirection.SERVER_TO_CLIENT);
        assertTrue(clientChanges.getChanges().isEmpty());
    }

    @Test
    public void getChangesClientToServerPerTable() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        SyncData clientChanges = testGetChangesPerTable(ConflictStrategy.CLIENT_WINS,
            SyncDirection.CLIENT_TO_SERVER);
        assertTrue(clientChanges.getChanges().size() == 2);
    }

    @Test
    public void getChangesBidirectionalPerTable() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        SyncData clientChanges = testGetChangesPerTable(ConflictStrategy.CLIENT_WINS, SyncDirection.BIDIRECTIONAL);
        assertTrue(clientChanges.getChanges().size() == 2);
    }
}
