package de.consistec.syncframework.client.it_postgres;

/*
 * #%L
 * Project - doppelganger
 * File - RepeatSyncTest.java
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
import static de.consistec.syncframework.common.SyncDirection.BIDIRECTIONAL;
import static de.consistec.syncframework.common.adapter.DatabaseAdapterFactory.AdapterPurpose.CLIENT;
import static de.consistec.syncframework.common.adapter.DatabaseAdapterFactory.AdapterPurpose.SERVER;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.SERVER_WINS;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.client.ClientSyncProvider;
import de.consistec.syncframework.common.client.IClientSyncProvider;
import de.consistec.syncframework.common.client.SyncAgent;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.server.IServerSyncProvider;
import de.consistec.syncframework.common.server.ServerSyncProvider;
import de.consistec.syncframework.impl.TestDatabase;
import de.consistec.syncframework.impl.TestScenario;
import de.consistec.syncframework.impl.adapter.it_postgres.PostgresDatabase;

import java.io.IOException;
import java.sql.SQLException;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 21.01.13 13:20
 */
public class RepeatSyncTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RepeatSyncTest.class.getCanonicalName());
    protected static String[] tableNames = new String[]{"categories", "categories_md", "items", "items_md"};
    protected static String[] createQueries = new String[]{
        "CREATE TABLE categories (categoryid INTEGER NOT NULL PRIMARY KEY ,categoryname VARCHAR (300),description VARCHAR (300));",
        "CREATE TABLE categories_md (pk INTEGER NOT NULL PRIMARY KEY, mdv VARCHAR (300), rev INTEGER DEFAULT 1, f INTEGER DEFAULT 0);",
        "CREATE TABLE items (id INTEGER NOT NULL PRIMARY KEY ,name VARCHAR (300),description VARCHAR (300));",
        "CREATE TABLE items_md (pk INTEGER NOT NULL PRIMARY KEY, mdv VARCHAR (300), rev INTEGER DEFAULT 1, f INTEGER DEFAULT 0);",
        "INSERT INTO categories (categoryid, categoryname, description) VALUES (1, 'Beverages', 'Soft drinks')",
        "INSERT INTO categories (categoryid, categoryname, description) VALUES (2, 'Condiments', 'Sweet and ')",
        "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (1, '8F3CCBD3FE5C9106253D472F6E36F0E1', 1, 0)",
        "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (1, '75901F57520C09EB990837C7AA93F717', 2, 0)",};
    protected static String deleteRow1 = "DELETE FROM categories WHERE categoryid = 1";
    protected static String insertRow3 = "INSERT INTO categories (categoryid, categoryname, description) VALUES (3, 'Cat3a', '3a')";
    protected static String[] updateMdRow2 = new String[]{"UPDATE categories_md SET rev = '4' WHERE pk = 2"};
    protected static String updateRow1 = "UPDATE categories SET categoryname = 'Cat1c', description = '1c' WHERE categoryid = 1";
    private TestDatabase clientDb, serverDb;

    public RepeatSyncTest() {
        serverDb = new PostgresDatabase(SERVER);
        clientDb = new PostgresDatabase(CLIENT);
    }

    @BeforeClass
    public static void setUpClass() {
        // initialize logging framework
        DOMConfigurator.configure(ClassLoader.getSystemResource("log4j.xml"));
    }

    @Before
    public void setUp() throws IOException, SQLException {
        Config.getInstance().init(getClass().getResourceAsStream(clientDb.getConfigFile()));

        MockitoAnnotations.initMocks(this);
    }

    @BeforeClass
    public static void initClass() throws SQLException {
        // initialize logging framework
        DOMConfigurator.configure(ClassLoader.getSystemResource("log4j.xml"));
    }

    @Before
    public void init() throws SyncException, ContextException, SQLException, IOException {

        Config.getInstance().init(getClass().getResourceAsStream(serverDb.getConfigFile()));

        clientDb.init();
        clientDb.dropTables(tableNames);
        clientDb.executeQueries(createQueries);

        serverDb.init();
        serverDb.dropTables(tableNames);
        serverDb.executeQueries(createQueries);

    }

    @After
    public void tearDown() throws SQLException {
        serverDb.closeConnections();
        clientDb.closeConnections();
    }

    @Test
    public void clientUpdate() throws ContextException, SyncException, DatabaseAdapterException, SQLException {

        TestScenario scenario = new TestScenario("client update", BIDIRECTIONAL, SERVER_WINS)
            .addStep(CLIENT, updateRow1)
            .expectServer("CS")
            .expectClient("CS");

        scenario.setServerDatabase(serverDb);
        scenario.setClientDatabase(clientDb);

        scenario.setSelectQueries(new String[]{
                "select * from categories order by categoryid asc",
                "select * from categories_md order by pk asc"
            });

        scenario.executeSteps();

        scenario.saveCurrentState();

        IServerSyncProvider serverSyncProvider = new ServerSyncProvider(new TableSyncStrategies());
        IClientSyncProvider clientSyncProvider = new ClientSyncProvider(new TableSyncStrategies());
        SyncAgent agent = new SyncAgent(serverSyncProvider, clientSyncProvider) {
            boolean firstSync = true;

            @Override
            protected void doAfterGetServerChanges() {

                if (firstSync) {
                    // change revision from server entry to force a client not up to date exception
                    try {
                        serverDb.executeQueries(updateMdRow2);
                    } catch (SQLException e) {
                        e.printStackTrace(
                            System.err);  //To change body of catch statement use File | Settings | File Templates.
                    }
                    super.doAfterGetServerChanges();
                    firstSync = false;
                }
            }
        };
        agent.synchronize();

        scenario.assertServerIsInExpectedState();
        scenario.assertClientIsInExpectedState();

    }

    @Test
    public void clientInsert() throws ContextException, SyncException, DatabaseAdapterException, SQLException {

        TestScenario scenario = new TestScenario("client insert", BIDIRECTIONAL, SERVER_WINS)
            .addStep(CLIENT, insertRow3)
            .expectServer("SSC")
            .expectClient("SSC");

        scenario.setServerDatabase(serverDb);
        scenario.setClientDatabase(clientDb);

        scenario.setSelectQueries(new String[]{
                "select * from categories order by categoryid asc",
                "select * from categories_md order by pk asc"
            });

        scenario.executeSteps();

        scenario.saveCurrentState();

        IServerSyncProvider serverSyncProvider = new ServerSyncProvider(new TableSyncStrategies());
        IClientSyncProvider clientSyncProvider = new ClientSyncProvider(new TableSyncStrategies());
        SyncAgent agent = new SyncAgent(serverSyncProvider, clientSyncProvider) {
            boolean firstSync = true;

            @Override
            protected void doAfterGetServerChanges() {

                if (firstSync) {
                    // change revision from server entry to force a client not up to date exception
                    try {
                        serverDb.executeQueries(updateMdRow2);
                    } catch (SQLException e) {
                        e.printStackTrace(
                            System.err);  //To change body of catch statement use File | Settings | File Templates.
                    }
                    super.doAfterGetServerChanges();
                    firstSync = false;
                }
            }
        };
        agent.synchronize();

        scenario.assertServerIsInExpectedState();
        scenario.assertClientIsInExpectedState();
    }

    @Test
    public void clientDelete() throws ContextException, SyncException, DatabaseAdapterException, SQLException {

        TestScenario scenario = new TestScenario("client delete", BIDIRECTIONAL, SERVER_WINS)
            .addStep(CLIENT, deleteRow1)
            .expectServer("C")
            .expectClient("C");

        scenario.setServerDatabase(serverDb);
        scenario.setClientDatabase(clientDb);

        scenario.setSelectQueries(new String[]{
                "select * from categories order by categoryid asc",
                "select * from categories_md order by pk asc"
            });

        scenario.executeSteps();

        scenario.saveCurrentState();

        IServerSyncProvider serverSyncProvider = new ServerSyncProvider(new TableSyncStrategies());
        IClientSyncProvider clientSyncProvider = new ClientSyncProvider(new TableSyncStrategies());
        SyncAgent agent = new SyncAgent(serverSyncProvider, clientSyncProvider) {
            boolean firstSync = true;

            @Override
            protected void doAfterGetServerChanges() {

                if (firstSync) {
                    // change revision from server entry to force a client not up to date exception
                    try {
                        serverDb.executeQueries(updateMdRow2);
                    } catch (SQLException e) {
                        e.printStackTrace(
                            System.err);  //To change body of catch statement use File | Settings | File Templates.
                    }
                    super.doAfterGetServerChanges();
                    firstSync = false;
                }
            }
        };
        agent.synchronize();

        scenario.assertServerIsInExpectedState();
        scenario.assertClientIsInExpectedState();
    }
}
