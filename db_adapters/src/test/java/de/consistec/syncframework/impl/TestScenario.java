package de.consistec.syncframework.impl;

import de.consistec.syncframework.common.IConflictListener;
import de.consistec.syncframework.common.SyncContext;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.TableSyncStrategy;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.impl.adapter.ConnectionType;
import de.consistec.syncframework.impl.adapter.DumpDataSource;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.FIRE_EVENT;

import de.consistec.syncframework.impl.adapter.ResultSetComparator;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;

/**
 * Use this class to reflect your integration test scenario
 * <p/>
 * @company Consistec Engineering and Consulting GmbH
 * @date 10.01.2013 14:50:08
 * @author davidm
 * @since
 */
public class TestScenario {

    private String name;
    private SyncDirection direction;
    private ConflictStrategy strategy;
    private ConnectionType expectedState;
    private List<Map<ConnectionType, String>> steps = new LinkedList<Map<ConnectionType, String>>();

    public TestScenario(String name, SyncDirection direction, ConflictStrategy strategy, ConnectionType expectedState) {
        this.name = name;
        this.direction = direction;
        this.strategy = strategy;
        this.expectedState = expectedState;
    }

    public String getName() {
        return name;
    }

    public SyncDirection getDirection() {
        return direction;
    }

    public ConflictStrategy getStrategy() {
        return strategy;
    }

    public ConnectionType getExpectedState() {
        return expectedState;
    }

    public TestScenario addStep(ConnectionType side, String query) {
        Map<ConnectionType, String> step = new EnumMap<ConnectionType, String>(ConnectionType.class);
        step.put(side, query);
        steps.add(step);
        return this;
    }

    public void executeSteps(Connection serverConnection, Connection clientConnection) throws SQLException {
        Statement serverStmt = serverConnection.createStatement();
        Statement clientStmt = clientConnection.createStatement();

        for (Map<ConnectionType, String> step : steps) {
            // there should be exactly one entry per step
            ConnectionType side = step.keySet().iterator().next();
            String query = step.get(side);

            switch (side) {
                case CLIENT:
                    clientStmt.addBatch(query);
                    break;
                case SERVER:
                    serverStmt.addBatch(query);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown connection type: " + side);
            }
        }

        serverStmt.executeBatch();
        serverStmt.close();
        clientStmt.executeBatch();
        clientStmt.close();
    }

    public void synchronize(String tableName, DumpDataSource serverDs, DumpDataSource clientDs) throws SyncException,
        ContextException {
        TableSyncStrategy tableSyncStrategy = new TableSyncStrategy(direction, strategy);
        TableSyncStrategies strategies = new TableSyncStrategies();
        strategies.addSyncStrategyForTable(tableName, tableSyncStrategy);

        final SyncContext.LocalContext localCtx = SyncContext.local(serverDs, clientDs, strategies);

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

    public void assertBothSidesAreInExpectedState() throws SQLException {
        Assert.assertEquals(true, false);
//        ResultSet clientResultSet, serverResultSet;
//        ResultSetComparator comparator = new ResultSetComparator();
//
//        Map<String, String> selectQueries = new HashMap<String, String>();
//        selectQueries.put("categories", "select * from categories order by id asc");
//        selectQueries.put("categories" + getMdTableSuffix(),
//            String.format("select pk, mdv, rev from categories%s  order by pk asc", getMdTableSuffix()));
//
//        for (String tableName : selectQueries.keySet()) {
//            String query = selectQueries.get(tableName);
//
//            if (tableName.endsWith(getMdTableSuffix())) {
//                continue;
//            }
//
//            Statement clientStmt = clientDs.getConnection().createStatement();
//            clientResultSet = clientStmt.executeQuery(query);
//
//            Statement serverStmt = serverDs.getConnection().createStatement();
//            serverResultSet = serverStmt.executeQuery(query);
//
//            String tableContentToCompare = getContentToCompare(tableName, type);
//            String clientTableContent = resultSetToString(clientResultSet);
//            comparator.compare(tableContentToCompare, clientTableContent);
//
//            if (type2 != null) {
//                tableContentToCompare = getContentToCompare(tableName, type2);
//            }
//            String serverTableContent = resultSetToString(serverResultSet);
//
//            comparator.compare(tableContentToCompare, serverTableContent);
//
//            comparator.compare(clientResultSet, serverResultSet);
//
//            Assert.assertEquals(clientResultSet, serverResultSet);
//
//            clientStmt.close();
//            serverStmt.close();
//        }
//    }
//
//    private String getContentToCompare(String tableName, ConnectionType type) {
//        if (type == ConnectionType.CLIENT) {
//            return clientTableContentMap.get(tableName);
//        } else {
//            return serverTableContentMap.get(tableName);
//        }
    }

    private String resultSetToString(final ResultSet rs) throws SQLException {
        if (rs == null) {
            throw new IllegalArgumentException("Passed result set can't be null!!!");
        }
        StringBuilder strBuilder = new StringBuilder();

        while (rs.next()) {
            ResultSetMetaData metaData = rs.getMetaData();

            List<String> sortedList = new ArrayList<String>();
            sortColumnNames(metaData, sortedList);

            for (String listEntry : sortedList) {
                strBuilder.append(listEntry).append("(").append(rs.getObject(listEntry)).append("),");
            }
        }
        return strBuilder.toString();
    }

    private List<String> sortColumnNames(ResultSetMetaData metaData, List<String> sortedList) throws SQLException {

        int columnCount = metaData.getColumnCount();
        for (int j = 1; j <= columnCount; j++) {

            sortedList.add(metaData.getColumnName(j));
        }

        Collections.sort(sortedList);
        return sortedList;
    }

    @Override
    public String toString() {
        String result = "TestScenario '" + name + "': \n\tsteps= ";
        for (Map<ConnectionType, String> step : steps) {
            // there should be exactly one entry per step
            ConnectionType side = step.keySet().iterator().next();
            String query = step.get(side);
            result += "\n\t - side=" + side + ", query=" + query;
        }
        result += "\n\tdirection=" + direction + ", \n\tstrategy=" + strategy + ", \n\texpectedState=" + expectedState;
        return result;
    }

    private String getMdTableSuffix() {
        return "_md";
    }
}
