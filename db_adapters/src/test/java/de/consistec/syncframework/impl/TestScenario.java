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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumMap;
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
    private DumpDataSource serverDs, clientDs;
    private ResultSet[] expectedResultSets;
    private String[] selectQueries;

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

    /**
     * Adds a step to the scenario, like a query to be executed on the client/server or a sync in between.
     */
    public TestScenario addStep(ConnectionType side, String query) {
        Map<ConnectionType, String> step = new EnumMap<ConnectionType, String>(ConnectionType.class);
        step.put(side, query);
        steps.add(step);
        return this;
    }

    public void setDataSources(DumpDataSource serverDs, DumpDataSource clientDs) {
        this.serverDs = serverDs;
        this.clientDs = clientDs;
    }

    /**
     * These queries will be used to assert the tables are in the right state.
     */
    public void setSelectQueries(String[] selectQueries) {
        this.selectQueries = selectQueries;
    }

    public void executeSteps() throws SQLException {
        Statement serverStmt = serverDs.getConnection().createStatement();
        Statement clientStmt = clientDs.getConnection().createStatement();

        for (Map<ConnectionType, String> step : steps) {
            // there should be exactly one entry per step
            ConnectionType side = step.keySet().iterator().next();
            String query = step.get(side);

            switch (side) {
                case CLIENT:
                    clientStmt.execute(query);
                    break;
                case SERVER:
                    serverStmt.executeQuery(query);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown connection type: " + side);
            }
        }

        serverStmt.close();
        clientStmt.close();
    }

    public void synchronize(String[] tableNames) throws SyncException, ContextException, SQLException {
        setExpectedResultSet();

        TableSyncStrategy tableSyncStrategy = new TableSyncStrategy(direction, strategy);
        TableSyncStrategies strategies = new TableSyncStrategies();
        for (String tableName : tableNames) {
            strategies.addSyncStrategyForTable(tableName, tableSyncStrategy);
        }

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

    private void setExpectedResultSet() throws SQLException {
        Statement serverStmt = serverDs.getConnection().createStatement();
        Statement clientStmt = clientDs.getConnection().createStatement();

        ResultSet[] expectedRs = new ResultSet[selectQueries.length];

        for (int i = 0; i < selectQueries.length; i++) {
            String query = selectQueries[i];

            switch (expectedState) {
                case CLIENT:
                    expectedRs[i] = clientStmt.executeQuery(query);
                    break;
                case SERVER:
                    expectedRs[i] = serverStmt.executeQuery(query);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown connection type: " + expectedState);
            }
        }

        serverStmt.close();
        clientStmt.close();

        expectedResultSets = expectedRs;
    }

    public void assertBothSidesAreInExpectedState() throws SQLException {

        ResultSet clientResultSet, serverResultSet;

        for (int i = 0; i < selectQueries.length; i++) {
            String selectQuery = selectQueries[i];
            Statement clientStmt = clientDs.getConnection().createStatement();
            clientResultSet = clientStmt.executeQuery(selectQuery);
            Statement serverStmt = serverDs.getConnection().createStatement();
            serverResultSet = serverStmt.executeQuery(selectQuery);

            ResultSetComparator.assertEquals(expectedResultSets[i], serverResultSet);
            ResultSetComparator.assertEquals(expectedResultSets[i], clientResultSet);
        }

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
}
