package de.consistec.syncframework.impl;

import static de.consistec.syncframework.common.SyncDirection.CLIENT_TO_SERVER;
import static de.consistec.syncframework.common.SyncDirection.SERVER_TO_CLIENT;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.CLIENT_WINS;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.FIRE_EVENT;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.SERVER_WINS;
import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.IConflictListener;
import de.consistec.syncframework.common.SyncContext;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.TableSyncStrategy;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.i18n.Errors;
import de.consistec.syncframework.impl.adapter.ConnectionType;
import de.consistec.syncframework.impl.adapter.DumpDataSource;

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
 *
 * @author davidm
 * @company Consistec Engineering and Consulting GmbH
 * @date 10.01.2013 14:50:08
 */
public class TestScenario {

    private final String name;
    private final SyncDirection direction;
    private final ConflictStrategy strategy;
    private String expectedServerState, expectedClientState;
    private List<Map<ConnectionType, String>> steps = new LinkedList<Map<ConnectionType, String>>();
    private DumpDataSource serverDs, clientDs;
    private Connection serverConnection, clientConnection;
    private String[] selectTableQueries;
    // expected result sets are stored as text to avoid "ResultSet already closed" exceptions
    private String[] expectedFlatServerResultSets, expectedFlatClientResultSets;
    private String expectedErrorMsg;
    private Class expectedException;

    public TestScenario(String name, SyncDirection direction, ConflictStrategy strategy) {
        this.name = name;
        this.direction = direction;
        this.strategy = strategy;
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

    public String getExpectedErrorMsg() {
        return expectedErrorMsg;
    }

    public Class getExpectedException() {
        return expectedException;
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

    /**
     * The mask defines the expected result after the sync.<br/>
     * - 'S' codes a row originating from the server <br/>
     * - 'C' from the client <br/>
     * - ' ' a blank space counts as a deleted row: <br/>
     * E.g: "C S" = 1st row is from the client, the 2nd row was deleted and replaced by the server's 3rd row
     */
    public TestScenario expectServer(String serverMask) {
        this.expectedServerState = serverMask;
        return this;
    }

    /**
     * The mask defines the expected result after the sync.<br/>
     * - 'S' codes a row originating from the server <br/>
     * - 'C' from the client <br/>
     * - ' ' a blank space counts as a deleted row: <br/>
     * E.g: "C S" = 1st row is from the client, the 2nd row was deleted and replaced by the server's 3rd row
     */
    public TestScenario expectClient(String clientMask) {
        this.expectedClientState = clientMask;
        return this;
    }

    /**
     * Tells that this scenario expects an exception of passed exception class type and
     * what error message will be expected.
     *
     * @param errorMsg expected error message
     * @return this test scenario
     */
    public TestScenario expectException(Class clazz, Errors errorMsg) {
        this.expectedException = clazz;
        this.expectedErrorMsg = read(errorMsg).split("\\{")[0];
        return this;
    }

    public boolean shouldThrowAnException() {
        return expectedException != null;
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
                    serverStmt.execute(query);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown connection type: " + side);
            }
        }

        serverStmt.close();
        clientStmt.close();
    }

    public void saveCurrentState() throws SQLException {
        if (this.expectedException != null) {
            // An exception will be thrown, no need to save the state
            return;
        }

        expectedFlatClientResultSets = new String[selectTableQueries.length];
        expectedFlatServerResultSets = new String[selectTableQueries.length];

        Statement serverStmt = serverConnection.createStatement();
        Statement clientStmt = clientConnection.createStatement();

        ResultSet serverRs, clientRs;

        for (int i = 0; i < selectTableQueries.length; i += 2) {
            String query = selectTableQueries[i];

            serverRs = serverStmt.executeQuery(query);
            clientRs = clientStmt.executeQuery(query);
            expectedFlatServerResultSets[i] = ResultSetHelper.getExpectedResultSet(serverRs, clientRs,
                expectedServerState);

            serverRs = serverStmt.executeQuery(query);
            clientRs = clientStmt.executeQuery(query);
            expectedFlatClientResultSets[i] = ResultSetHelper.getExpectedResultSet(serverRs, clientRs,
                expectedClientState);
        }

        serverStmt.close();
        clientStmt.close();
    }

    public void synchronize(String[] tableNames) throws SyncException, ContextException, SQLException {
        TableSyncStrategies strategies = new TableSyncStrategies();

        TableSyncStrategy tableSyncStrategy = new TableSyncStrategy(direction, strategy);
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

    public void assertServerIsInExpectedState() throws SQLException {
        ResultSet serverResultSet;

        Statement serverStmt = serverConnection.createStatement();

        for (int i = 0; i < selectTableQueries.length; i += 2) {
            String flatServerRs;
            String selectQuery = selectTableQueries[i];

            serverResultSet = serverStmt.executeQuery(selectQuery);
            flatServerRs = ResultSetHelper.resultSetToString(serverResultSet);

            Assert.assertEquals("Server state is invalid.", expectedFlatServerResultSets[i], flatServerRs);

//            serverResultSet.last();
//            Assert.assertEquals("Wrong row count on server", expectedServerState.length(), serverResultSet.getRow());
        }

        serverStmt.close();
    }

    public void assertClientIsInExpectedState() throws SQLException {
        ResultSet clientResultSet;

        Statement clientStmt = clientConnection.createStatement();

        for (int i = 0; i < selectTableQueries.length; i += 2) {
            String flatClientRs;
            String selectQuery = selectTableQueries[i];

            clientResultSet = clientStmt.executeQuery(selectQuery);
            flatClientRs = ResultSetHelper.resultSetToString(clientResultSet);

            Assert.assertEquals("Client state is invalid.", expectedFlatClientResultSets[i], flatClientRs);

//            clientResultSet.last();
//            Assert.assertEquals("Wrong row count on client", expectedClientState.length(), clientResultSet.getRow());
        }

        clientStmt.close();
    }

    @Override
    public String toString() {
        return name + " - " + direction + ", " + strategy;
    }

    public String getLongDescription() {
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
