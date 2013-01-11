package de.consistec.syncframework.impl;

import static de.consistec.syncframework.common.conflict.ConflictStrategy.FIRE_EVENT;

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
import de.consistec.syncframework.impl.adapter.ExecuteStatementHelper;

import java.sql.Connection;
import java.sql.ResultSet;
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

    private final String name;
    private final SyncDirection direction;
    private final ConflictStrategy strategy;
    private final ConnectionType expectedServerState;
    private final ConnectionType expectedClientState;
    private List<Map<ConnectionType, String>> steps = new LinkedList<Map<ConnectionType, String>>();
    private DumpDataSource serverDs, clientDs;
    private Connection serverConnection, clientConnection;
    private String[] selectTableQueries;
    // expected result sets are stored as text to avoid "ResultSet already closed" exceptions
    private String[] expectedFlatServerResultSets;
    private String[] expectedFlatClientResultSets;

    public TestScenario(String name, SyncDirection direction, ConflictStrategy strategy,
        ConnectionType expectedServerState, ConnectionType expectedClientState) {
        this.name = name;
        this.direction = direction;
        this.strategy = strategy;
        this.expectedServerState = expectedServerState;
        this.expectedClientState = expectedClientState;
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

    void setConnections(Connection serverConnection, Connection clientConnection) {
        this.serverConnection = serverConnection;
        this.clientConnection = clientConnection;
    }

    /**
     * These queries will be used to assert the tables are in the right state.
     */
    public void setSelectQueries(String[] selectQueries) {
        this.selectTableQueries = selectQueries;
    }

    /**
     * Executes all queries and possible synchronization as they were inserted in the queue.
     */
    public void executeSteps() throws SQLException {
        Statement serverStmt = serverConnection.createStatement();
        Statement clientStmt = clientConnection.createStatement();

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

    public void setExpectedServerResultSets(String[] expectedFlatServerResultSets) {
        this.expectedFlatServerResultSets = expectedFlatServerResultSets;
    }

    public void saveCurrentStateAsExpectedServerResult() throws SQLException {
        expectedFlatServerResultSets =new String[selectTableQueries.length];
        Statement serverStmt = serverConnection.createStatement();
        Statement clientStmt = clientConnection.createStatement();

        ResultSet expectedRs;

        for (int i = 0; i < selectTableQueries.length; i += 2) {
            String query = selectTableQueries[i];

            switch (expectedServerState) {
                case CLIENT:
                    expectedRs = clientStmt.executeQuery(query);
                    break;
                case SERVER:
                    expectedRs = serverStmt.executeQuery(query);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown connection type: " + expectedServerState);
            }
            expectedFlatServerResultSets[i] = ResultSetHelper.resultSetToString(expectedRs);
        }

        serverStmt.close();
        clientStmt.close();
    }

    public void setExpectedClientResultSets(String[] expectedFlatClientResultSets) {
        this.expectedFlatClientResultSets = expectedFlatClientResultSets;
    }

    public void saveCurrentStateAsExpectedClientResult() throws SQLException {
        expectedFlatClientResultSets =new String[selectTableQueries.length];
        Statement serverStmt = serverConnection.createStatement();
        Statement clientStmt = clientConnection.createStatement();

        ResultSet expectedRs;

        for (int i = 0; i < selectTableQueries.length; i += 2) {
            String query = selectTableQueries[i];

            switch (expectedClientState) {
                case CLIENT:
                    expectedRs = clientStmt.executeQuery(query);
                    break;
                case SERVER:
                    expectedRs = serverStmt.executeQuery(query);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown connection type: " + expectedClientState);
            }
            expectedFlatClientResultSets[i] = ResultSetHelper.resultSetToString(expectedRs);
        }

        serverStmt.close();
        clientStmt.close();
    }

    public void synchronize(String[] tableNames) throws SyncException, ContextException, SQLException {

        TableSyncStrategy tableSyncStrategy = new TableSyncStrategy(direction, strategy);
        TableSyncStrategies strategies = new TableSyncStrategies();
        for (int i = 0; i < tableNames.length; i += 2) {
            strategies.addSyncStrategyForTable(tableNames[i], tableSyncStrategy);
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

    public void assertBothSidesAreInExpectedState() throws SQLException {

        ResultSet serverResultSet, clientResultSet;
        Statement clientStmt = clientConnection.createStatement();
        Statement serverStmt = serverConnection.createStatement();

        for (int i = 0; i < selectTableQueries.length; i += 2) {
            String flatServerRs, flatClientRs;
            String selectQuery = selectTableQueries[i];

            clientResultSet = clientStmt.executeQuery(selectQuery);
            flatClientRs = ExecuteStatementHelper.resultSetToString(clientResultSet);

            serverResultSet = serverStmt.executeQuery(selectQuery);
            flatServerRs = ExecuteStatementHelper.resultSetToString(serverResultSet);

            Assert.assertEquals(expectedFlatServerResultSets[i], flatServerRs);
            Assert.assertEquals(expectedFlatClientResultSets[i], flatClientRs);

            ResultSetHelper.assertEquals(clientResultSet, serverResultSet);
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
        result += "\n\tdirection=" + direction + ", \n\tstrategy=" + strategy;
        result += ", \n\texpectedServerState=" + expectedServerState + ", \n\texpectedClientState=" + expectedClientState;
        return result;
    }
}
