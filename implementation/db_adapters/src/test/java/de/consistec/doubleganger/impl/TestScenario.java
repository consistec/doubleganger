package de.consistec.doubleganger.impl;

/*
 * #%L
 * Project - doubleganger
 * File - TestScenario.java
 * %%
 * Copyright (C) 2011 - 2013 consistec GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static de.consistec.doubleganger.common.conflict.ConflictStrategy.FIRE_EVENT;
import static de.consistec.doubleganger.common.i18n.MessageReader.read;

import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.IConflictListener;
import de.consistec.doubleganger.common.SyncContext;
import de.consistec.doubleganger.common.SyncDirection;
import de.consistec.doubleganger.common.TableSyncStrategies;
import de.consistec.doubleganger.common.TableSyncStrategy;
import de.consistec.doubleganger.common.adapter.DatabaseAdapterFactory;
import de.consistec.doubleganger.common.conflict.ConflictStrategy;
import de.consistec.doubleganger.common.conflict.UserDecision;
import de.consistec.doubleganger.common.exception.ContextException;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.i18n.Errors;
import de.consistec.doubleganger.common.util.LoggingUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.slf4j.cal10n.LocLogger;

/**
 * Use this class to reflect your integration test scenario
 * <p/>
 *
 * @author davidm
 * @company consistec Engineering and Consulting GmbH
 * @date 10.01.2013 14:50:08
 */
public class TestScenario {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(TestScenario.class.getCanonicalName());
    private static final Config CONF = Config.getInstance();
    private final String name;
    private final SyncDirection direction;
    private final ConflictStrategy strategy;
    private String expectedServerState, expectedClientState;
    private List<Map<DatabaseAdapterFactory.AdapterPurpose, String>> steps = new LinkedList<Map<DatabaseAdapterFactory.AdapterPurpose, String>>();
    private TestDatabase clientDb, serverDb;
    private String[] selectTableQueries;
    // expected result sets are stored as text to avoid "ResultSet already closed" exceptions
    private String[] expectedFlatServerResultSets, expectedFlatClientResultSets;
    private String expectedErrorMsg;
    private Class expectedExceptionClass;
    private UserInformation userInformation;

    public TestScenario(String name, SyncDirection direction, ConflictStrategy strategy) {
        this.name = name;
        this.direction = direction;
        this.strategy = strategy;
        this.userInformation = new UserInformation(UserDecision.SERVER_CHANGE, null);
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

    public Class getExpectedExceptionClass() {
        return expectedExceptionClass;
    }

    public void setClientDatabase(TestDatabase db) {
        this.clientDb = db;
    }

    public void setServerDatabase(TestDatabase db) {
        this.serverDb = db;
    }

    public UserInformation getUserInformation() {
        return userInformation;
    }

    /**
     * Adds a step to the scenario, like a query to be executed on the client/server or a sync in between.
     */
    public TestScenario addStep(DatabaseAdapterFactory.AdapterPurpose side, String query) {
        Map<DatabaseAdapterFactory.AdapterPurpose, String> step = new EnumMap<DatabaseAdapterFactory.AdapterPurpose, String>(
            DatabaseAdapterFactory.AdapterPurpose.class);
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
        this.expectedExceptionClass = clazz;
        this.expectedErrorMsg = read(errorMsg).split("\\{")[0];
        return this;
    }

    public void assertNoExceptionExpected() {
        if (expectedExceptionClass != null) {
            Assert.fail("Expected exception (" + expectedExceptionClass + ") wasn't thrown.");
        }
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
        if (CONF.isSqlTriggerOnServerActivated()) {
            serverDb.connectWithExternalUser();
        }
        if (CONF.isSqlTriggerOnClientActivated()) {
            clientDb.connectWithExternalUser();
        }
        Statement serverStmt = serverDb.getConnection().createStatement();
        Statement clientStmt = clientDb.getConnection().createStatement();

        for (Map<DatabaseAdapterFactory.AdapterPurpose, String> step : steps) {
            // there should be exactly one entry per step
            DatabaseAdapterFactory.AdapterPurpose side = step.keySet().iterator().next();
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
            LOGGER.debug("Executed query on {}: {}", side, query);
        }

        serverStmt.close();
        clientStmt.close();
    }

    public void saveCurrentState() {
        if (this.expectedExceptionClass != null) {
            // An exception will be thrown, no need to save the state
            return;
        }

        expectedFlatClientResultSets = new String[selectTableQueries.length];
        expectedFlatServerResultSets = new String[selectTableQueries.length];

        try {
            Statement serverStmt = serverDb.getConnection().createStatement();
            Statement clientStmt = clientDb.getConnection().createStatement();

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

        } catch (SQLException ex) {
            throw new RuntimeException("Error: please ensure the client and the server both have data.", ex);
        }
    }

    public void synchronize(String[] tableNames, IConflictListener conflictListener) throws SyncException,
        ContextException, SQLException {
        synchronize(tableNames, conflictListener, direction);
    }

    public void synchronize(String[] tableNames, IConflictListener conflictListener, SyncDirection dir) throws
        SyncException, ContextException, SQLException {
        serverDb.connectWithSyncUser();
        clientDb.connectWithSyncUser();
        TableSyncStrategies strategies = new TableSyncStrategies();

        TableSyncStrategy tableSyncStrategy = new TableSyncStrategy(dir, strategy);
        for (int i = 0; i < tableNames.length; i += 2) {
            strategies.addSyncStrategyForTable(tableNames[i], tableSyncStrategy);
        }

        final SyncContext.LocalContext localCtx = SyncContext.local(serverDb.getDataSource(), clientDb.getDataSource(),
            strategies);

        if (strategy == FIRE_EVENT) {
            localCtx.setConflictListener(conflictListener);
        }

        localCtx.synchronize();
    }

    public void assertServerIsInExpectedState() throws SQLException {
        ResultSet serverResultSet;

        Statement serverStmt = serverDb.getConnection().createStatement();

        for (int i = 0; i < selectTableQueries.length; i += 2) {
            String flatServerRs;
            String selectQuery = selectTableQueries[i];

            serverResultSet = serverStmt.executeQuery(selectQuery);
            flatServerRs = ResultSetHelper.resultSetToString(serverResultSet);

            Assert.assertEquals("Server state is invalid.", expectedFlatServerResultSets[i], flatServerRs);
        }

        serverStmt.close();
    }

    public void assertClientIsInExpectedState() throws SQLException {
        ResultSet clientResultSet;

        Statement clientStmt = clientDb.getConnection().createStatement();

        for (int i = 0; i < selectTableQueries.length; i += 2) {
            String flatClientRs;
            String selectQuery = selectTableQueries[i];

            clientResultSet = clientStmt.executeQuery(selectQuery);
            flatClientRs = ResultSetHelper.resultSetToString(clientResultSet);

            Assert.assertEquals("Client state is invalid.", expectedFlatClientResultSets[i], flatClientRs);
        }

        clientStmt.close();
    }

    @Override
    public String toString() {
        String userAction = "";
        String selectedChange = "";

        if (userInformation != null) {
            userAction = (userInformation.getUserAction() == null) ? "no action" : userInformation.getUserAction().name();
            selectedChange = (userInformation.getSelectedChange() == null) ? "no selected change" : userInformation.getSelectedChange().name();
        }
        return name + " - " + direction + ", " + strategy + ", " + userAction + ", " + selectedChange;
    }

    public String getLongDescription() {
        String result = "TestScenario '" + name + "': \n\tsteps= ";
        for (Map<DatabaseAdapterFactory.AdapterPurpose, String> step : steps) {
            // there should be exactly one entry per step
            DatabaseAdapterFactory.AdapterPurpose side = step.keySet().iterator().next();
            String query = step.get(side);
            result += "\n\t - side=" + side + ", query=" + query;
        }
        result += "\n\tdirection=" + direction + ", \n\tstrategy=" + strategy;
        result += ", \n\texpectedServerState=" + expectedServerState + ", \n\texpectedClientState=" + expectedClientState;
        return result;
    }

    public TestScenario addUserDecision(final UserDecision userAction, final UserDecision selectedChange) {
        userInformation = new UserInformation(userAction, selectedChange);
        return this;
    }

    public static class UserInformation {
        private UserDecision userAction;
        private UserDecision selectedChange;

        public UserInformation(final UserDecision userAction, final UserDecision selectedChange) {
            this.userAction = userAction;
            this.selectedChange = selectedChange;
        }

        public UserDecision getUserAction() {
            return userAction;
        }

        public UserDecision getSelectedChange() {
            return selectedChange;
        }
    }
}
