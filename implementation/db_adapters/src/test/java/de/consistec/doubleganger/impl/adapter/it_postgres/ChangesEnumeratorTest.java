package de.consistec.doubleganger.impl.adapter.it_postgres;

/*
 * #%L
 * Project - doubleganger
 * File - ChangesEnumeratorTest.java
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
import static de.consistec.doubleganger.common.adapter.DatabaseAdapterFactory.AdapterPurpose.CLIENT;
import static de.consistec.doubleganger.common.adapter.DatabaseAdapterFactory.AdapterPurpose.SERVER;
import static de.consistec.doubleganger.impl.adapter.DummyDataSource.SupportedDatabases.MYSQL;
import static de.consistec.doubleganger.impl.adapter.DummyDataSource.SupportedDatabases.POSTGRESQL;
import static de.consistec.doubleganger.impl.adapter.DummyDataSource.SupportedDatabases.SQLITE;

import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.SyncData;
import de.consistec.doubleganger.common.SyncDirection;
import de.consistec.doubleganger.common.TableSyncStrategies;
import de.consistec.doubleganger.common.TableSyncStrategy;
import de.consistec.doubleganger.common.adapter.DatabaseAdapterFactory;
import de.consistec.doubleganger.common.adapter.IDatabaseAdapter;
import de.consistec.doubleganger.common.client.ClientChangesEnumerator;
import de.consistec.doubleganger.common.conflict.ConflictStrategy;
import de.consistec.doubleganger.common.exception.ContextException;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.doubleganger.common.server.ServerChangesEnumerator;
import de.consistec.doubleganger.impl.TestDatabase;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runners.Parameterized;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 28.01.13 15:44
 */
public class ChangesEnumeratorTest {

    protected static String[] tableNames = new String[]{"categories", "categories_md", "items", "items_md"};
    protected static String[] createQueries = new String[]{
        "CREATE TABLE categories (categoryid INTEGER NOT NULL PRIMARY KEY ,categoryname VARCHAR (300),description VARCHAR (300))",
        "CREATE TABLE categories_md (pk INTEGER NOT NULL PRIMARY KEY, mdv VARCHAR (300), rev INTEGER DEFAULT 1, f INTEGER DEFAULT 0)",
        "CREATE TABLE items (id INTEGER NOT NULL PRIMARY KEY ,name VARCHAR (300),description VARCHAR (300))",
        "CREATE TABLE items_md (pk INTEGER NOT NULL PRIMARY KEY, mdv VARCHAR (300), rev INTEGER DEFAULT 1, f INTEGER DEFAULT 0)"};
    private static ConflictStrategy savedConflictStrategy;
    private static SyncDirection savedSyncDirection;
    protected TestDatabase serverDb, clientDb;

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> AllScenarii() {

        return Arrays.asList(new Object[][]{
                {new TestDatabase(SQLITE, SERVER, false), new TestDatabase(SQLITE, CLIENT, false)},
                {new TestDatabase(MYSQL, SERVER, false), new TestDatabase(MYSQL, CLIENT, false)},
                {new TestDatabase(POSTGRESQL, SERVER, false), new TestDatabase(POSTGRESQL, CLIENT, false)}});
    }

    public ChangesEnumeratorTest(TestDatabase serverDb, TestDatabase clientDb) {
        this.serverDb = serverDb;
        this.clientDb = clientDb;
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        savedConflictStrategy = Config.getInstance().getGlobalConflictStrategy();
        savedSyncDirection = Config.getInstance().getGlobalSyncDirection();
    }

    @Before
    public void setUp() throws IOException, SQLException {
        clientDb.init();
        clientDb.dropTables(tableNames);
        clientDb.executeQueries(createQueries);

        serverDb.init();
        serverDb.dropTables(tableNames);
        serverDb.executeQueries(createQueries);



        Config.getInstance().setGlobalConflictStrategy(savedConflictStrategy);
        Config.getInstance().setGlobalSyncDirection(savedSyncDirection);
    }

    protected SyncData getChangesGlobalOnClient(ConflictStrategy strategy, SyncDirection direction) throws
        SyncException, ContextException, SQLException, DatabaseAdapterException {

        Config configInstance = Config.getInstance();
        configInstance.setGlobalConflictStrategy(strategy);
        configInstance.setGlobalSyncDirection(direction);

        return getChangesWithStrategies(new TableSyncStrategies(), direction, CLIENT);
    }

    protected SyncData getChangesGlobalOnServer(ConflictStrategy strategy, SyncDirection direction) throws
        SyncException, ContextException, SQLException, DatabaseAdapterException {

        Config configInstance = Config.getInstance();
        configInstance.setGlobalConflictStrategy(strategy);
        configInstance.setGlobalSyncDirection(direction);

        return getChangesWithStrategies(new TableSyncStrategies(), direction, SERVER);
    }

    protected SyncData getChangesPerTableOnClient(ConflictStrategy strategy, SyncDirection direction) throws
        SyncException, ContextException, SQLException, DatabaseAdapterException {

        TableSyncStrategies strategies = new TableSyncStrategies();
        TableSyncStrategy tablsSyncStrategy = new TableSyncStrategy(direction, strategy);
        strategies.addSyncStrategyForTable("categories", tablsSyncStrategy);

        return getChangesWithStrategies(strategies, direction, CLIENT);
    }

    protected SyncData getChangesPerTableOnServer(ConflictStrategy strategy, SyncDirection direction) throws
        SyncException, ContextException, SQLException, DatabaseAdapterException {

        TableSyncStrategies strategies = new TableSyncStrategies();
        TableSyncStrategy tablsSyncStrategy = new TableSyncStrategy(direction, strategy);
        strategies.addSyncStrategyForTable("categories", tablsSyncStrategy);

        return getChangesWithStrategies(strategies, direction, SERVER);
    }

    private SyncData getChangesWithStrategies(TableSyncStrategies strategies, SyncDirection direction,
        DatabaseAdapterFactory.AdapterPurpose side) throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        IDatabaseAdapter adapter = null;
        try {
            adapter = DatabaseAdapterFactory.newInstance(side);

            if (side.equals(CLIENT)) {
                ClientChangesEnumerator clientChangesEnumerator = new ClientChangesEnumerator(adapter, strategies);
                return clientChangesEnumerator.getChanges();
            } else {
                ServerChangesEnumerator serverChangesEnumerator = new ServerChangesEnumerator(adapter, strategies);
                return serverChangesEnumerator.getChanges(1);
            }

        } finally {
            if (adapter != null) {
                if (adapter.getConnection() != null) {
                    adapter.getConnection().close();
                }
            }
        }
    }

    /**
     * Closes server and client connection.
     */
    @After
    public void tearDownClass() throws SQLException {
        serverDb.closeConnections();
        clientDb.closeConnections();
    }
}
