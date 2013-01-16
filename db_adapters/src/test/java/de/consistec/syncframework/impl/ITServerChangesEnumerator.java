package de.consistec.syncframework.impl;

import static org.junit.Assert.assertTrue;

import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.Tuple;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.impl.adapter.AbstractSyncTest;

import java.sql.SQLException;
import java.util.List;
import org.junit.Test;

/**
 * This class tests the correct handling of getChanges for server side.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 13.12.12 15:10
 */
public class ITServerChangesEnumerator extends AbstractSyncTest {

    private Tuple<Integer, List<Change>> serverChanges;
    private TableSyncStrategies tableStrategies;
    private static final String insert8b = "INSERT INTO categories SET categoryid=8, categoryname=Cat8b, description=buhahuha 8b";
    private static final String insert8c = "INSERT INTO categories SET categoryid=8, categoryname=Cat8b, description=buhahuha 8b";

    public ITServerChangesEnumerator(TestDatabase db) {
        super(db);
    }

    @Test
    public void getChangesServerToClient() throws ContextException, SyncException, DatabaseAdapterException,
        SQLException {
        initClientAndServerWithoutSync("category8_b_insert.xml", "category8_a_insert.xml");

        tableStrategies = setGlobalStrategy(ConflictStrategy.SERVER_WINS, SyncDirection.SERVER_TO_CLIENT);
        serverChanges = getServerChanges(tableStrategies);

        assertTrue(serverChanges.getValue2().size() == 1);
        assertTrue(serverChanges.getValue1() == 3);
    }

    @Test
    public void getChangesClientToServer() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        initClientAndServerWithoutSync("category8_b_insert.xml", "category8_a_insert.xml");

        tableStrategies = setGlobalStrategy(ConflictStrategy.CLIENT_WINS, SyncDirection.CLIENT_TO_SERVER);
        serverChanges = getServerChanges(tableStrategies);

        assertTrue(serverChanges.getValue2().isEmpty());
        assertTrue(serverChanges.getValue1() == 3);
    }

    @Test
    public void getChangesBidirectional() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        initClientAndServerWithoutSync("category8_b_insert.xml", "category8_a_insert.xml");

        tableStrategies = setGlobalStrategy(ConflictStrategy.CLIENT_WINS, SyncDirection.BIDIRECTIONAL);
        serverChanges = getServerChanges(tableStrategies);

        assertTrue(serverChanges.getValue2().size() == 1);
        assertTrue(serverChanges.getValue1() == 3);
    }

    @Test
    public void getChangesServerToClientPerTable() throws ContextException, SyncException, DatabaseAdapterException,
        SQLException {
        initClientAndServerWithoutSync("category9_b_insert.xml", "category9_a_insert.xml");

        tableStrategies = setStrategyForTable("categories", ConflictStrategy.SERVER_WINS, SyncDirection.SERVER_TO_CLIENT);
        serverChanges = getServerChanges(tableStrategies);

        assertTrue(serverChanges.getValue2().size() == 2);
        assertTrue(serverChanges.getValue1() == 3);
    }

    @Test
    public void getChangesClientToServerPerTable() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        initClientAndServerWithoutSync("category9_b_insert.xml", "category9_a_insert.xml");

        tableStrategies = setStrategyForTable("categories", ConflictStrategy.CLIENT_WINS, SyncDirection.CLIENT_TO_SERVER);
        serverChanges = getServerChanges(tableStrategies);

        assertTrue(serverChanges.getValue2().size() == 1);
        assertTrue(serverChanges.getValue1() == 3);
    }

    @Test
    public void getChangesBidirectionalPerTable() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        initClientAndServerWithoutSync("category9_b_insert.xml", "category9_a_insert.xml");

        tableStrategies = setStrategyForTable("categories", ConflictStrategy.CLIENT_WINS, SyncDirection.BIDIRECTIONAL);
        serverChanges = getServerChanges(tableStrategies);

        assertTrue(serverChanges.getValue2().size() == 2);
        assertTrue(serverChanges.getValue1() == 3);
    }

}
