package de.consistec.syncframework.impl.adapter.it_alldb;

import static org.junit.Assert.assertTrue;

import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.impl.TestDatabase;
import de.consistec.syncframework.impl.adapter.AbstractSyncTest;

import java.sql.SQLException;
import java.util.List;
import org.junit.Test;

/**
 * This class tests the correct handling of getChanges for client side.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 13.12.12 12:16
 */
public class ITClientChangesEnumerator extends AbstractSyncTest {

    private List<Change> clientChanges;
    private TableSyncStrategies tableStrategies;

    public ITClientChangesEnumerator(TestDatabase db) {
        super(db);
    }

    @Test
    public void getChangesServerToClient() throws ContextException, SyncException, DatabaseAdapterException,
        SQLException {
        initClientAndServerWithoutSync("category8_b_insert.xml", "category8_a_insert.xml");

        tableStrategies = setGlobalStrategy(ConflictStrategy.SERVER_WINS, SyncDirection.SERVER_TO_CLIENT);
        clientChanges = getClientChanges(tableStrategies);

        assertTrue(clientChanges.isEmpty());
    }

    @Test
    public void getChangesClientToServer() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        initClientAndServerWithoutSync("category8_b_insert.xml", "category8_a_insert.xml");

        tableStrategies = setGlobalStrategy(ConflictStrategy.CLIENT_WINS, SyncDirection.CLIENT_TO_SERVER);
        clientChanges = getClientChanges(tableStrategies);

        assertTrue(clientChanges.size() == 1);
    }

    @Test
    public void getChangesBidirectional() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        initClientAndServerWithoutSync("category8_b_insert.xml", "category8_a_insert.xml");

        tableStrategies = setGlobalStrategy(ConflictStrategy.CLIENT_WINS, SyncDirection.BIDIRECTIONAL);
        clientChanges = getClientChanges(tableStrategies);

        assertTrue(clientChanges.size() == 1);
    }

    @Test
    public void getChangesServerToClientPerTable() throws ContextException, SyncException, DatabaseAdapterException,
        SQLException {
        initClientAndServerWithoutSync("category9_b_insert.xml", "category9_a_insert.xml");

        tableStrategies = setStrategyForTable("categories", ConflictStrategy.SERVER_WINS, SyncDirection.SERVER_TO_CLIENT);
        clientChanges = getClientChanges(tableStrategies);

        assertTrue(clientChanges.size() == 1);
    }

    @Test
    public void getChangesClientToServerPerTable() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        initClientAndServerWithoutSync("category9_b_insert.xml", "category9_a_insert.xml");

        tableStrategies = setStrategyForTable("categories", ConflictStrategy.CLIENT_WINS, SyncDirection.CLIENT_TO_SERVER);
        clientChanges = getClientChanges(tableStrategies);

        assertTrue(clientChanges.size() == 2);
    }

    @Test
    public void getChangesBidirectionalPerTable() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        initClientAndServerWithoutSync("category9_b_insert.xml", "category9_a_insert.xml");

        tableStrategies = setStrategyForTable("categories", ConflictStrategy.CLIENT_WINS, SyncDirection.BIDIRECTIONAL);
        clientChanges = getClientChanges(tableStrategies);

        assertTrue(clientChanges.size() == 2);
    }
}
