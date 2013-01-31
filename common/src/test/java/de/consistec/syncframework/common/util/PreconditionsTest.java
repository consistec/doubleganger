package de.consistec.syncframework.common.util;

import static de.consistec.syncframework.common.SyncDirection.BIDIRECTIONAL;
import static de.consistec.syncframework.common.SyncDirection.CLIENT_TO_SERVER;
import static de.consistec.syncframework.common.SyncDirection.SERVER_TO_CLIENT;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.CLIENT_WINS;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.SERVER_WINS;
import static de.consistec.syncframework.common.util.CollectionsUtil.newArrayList;
import static de.consistec.syncframework.common.util.CollectionsUtil.newHashMap;

import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.TableSyncStrategy;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.MDEntry;
import de.consistec.syncframework.common.exception.SyncException;

import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 16.01.13 10:05
 */
public class PreconditionsTest {

    @Test(expected = SyncException.class)
    public void checkSyncDirectionOfServerChangesFail() throws SyncException {
        List<Change> serverChanges = newArrayList();

        MDEntry entry = new MDEntry(Integer.valueOf(1), true, 1, "categories", "7679869786hg76786hjhgf");
        Map<String, Object> rowData = newHashMap();
        rowData.put("categoryid", Integer.valueOf(1));
        rowData.put("description", "testdescription");
        rowData.put("categoryname", "testname");

        Change serverChange = new Change();
        serverChange.setMdEntry(entry);
        serverChange.setRowData(rowData);
        serverChanges.add(serverChange);


        TableSyncStrategies clientSyncStrategies = new TableSyncStrategies();
        clientSyncStrategies.addSyncStrategyForTable("categories",
            new TableSyncStrategy(CLIENT_TO_SERVER, CLIENT_WINS));

        Preconditions.checkSyncDirectionOfServerChanges(serverChanges, clientSyncStrategies);
    }

    @Test(expected = SyncException.class)
    public void checkSyncDirectionOfClientChangesFail() throws SyncException {
        List<Change> clientChanges = newArrayList();

        MDEntry entry = new MDEntry(Integer.valueOf(1), true, 1, "categories", "7679869786hg76786hjhgf");
        Map<String, Object> rowData = newHashMap();
        rowData.put("categoryid", Integer.valueOf(1));
        rowData.put("description", "testdescription");
        rowData.put("categoryname", "testname");

        Change clientChange = new Change();
        clientChange.setMdEntry(entry);
        clientChange.setRowData(rowData);
        clientChanges.add(clientChange);


        TableSyncStrategies serverSyncStrategies = new TableSyncStrategies();
        serverSyncStrategies.addSyncStrategyForTable("categories",
            new TableSyncStrategy(SERVER_TO_CLIENT, SERVER_WINS));

        Preconditions.checkSyncDirectionOfClientChanges(clientChanges, serverSyncStrategies);
    }

    @Test
    public void checkSyncDirectionOfServerChanges() throws SyncException {
        List<Change> serverChanges = newArrayList();

        MDEntry entry = new MDEntry(Integer.valueOf(1), true, 1, "categories", "7679869786hg76786hjhgf");
        Map<String, Object> rowData = newHashMap();
        rowData.put("categoryid", Integer.valueOf(1));
        rowData.put("description", "testdescription");
        rowData.put("categoryname", "testname");

        Change serverChange = new Change();
        serverChange.setMdEntry(entry);
        serverChange.setRowData(rowData);
        serverChanges.add(serverChange);


        TableSyncStrategies clientSyncStrategies = new TableSyncStrategies();
        clientSyncStrategies.addSyncStrategyForTable("categories",
            new TableSyncStrategy(BIDIRECTIONAL, SERVER_WINS));

        Preconditions.checkSyncDirectionOfServerChanges(serverChanges, clientSyncStrategies);
    }

    @Test
    public void checkSyncDirectionOfClientChanges() throws SyncException {
        List<Change> clientChanges = newArrayList();

        MDEntry entry = new MDEntry(Integer.valueOf(1), true, 1, "categories", "7679869786hg76786hjhgf");
        Map<String, Object> rowData = newHashMap();
        rowData.put("categoryid", Integer.valueOf(1));
        rowData.put("description", "testdescription");
        rowData.put("categoryname", "testname");

        Change clientChange = new Change();
        clientChange.setMdEntry(entry);
        clientChange.setRowData(rowData);
        clientChanges.add(clientChange);


        TableSyncStrategies serverSyncStrategies = new TableSyncStrategies();
        serverSyncStrategies.addSyncStrategyForTable("categories",
            new TableSyncStrategy(BIDIRECTIONAL, SERVER_WINS));

        Preconditions.checkSyncDirectionOfClientChanges(clientChanges, serverSyncStrategies);
    }
}
