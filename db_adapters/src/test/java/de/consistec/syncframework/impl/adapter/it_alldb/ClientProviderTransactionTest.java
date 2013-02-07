package de.consistec.syncframework.impl.adapter.it_alldb;

/*
 * #%L
 * Project - doppelganger
 * File - ClientProviderTransactionTest.java
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
import static de.consistec.syncframework.common.conflict.ConflictStrategy.SERVER_WINS;

import de.consistec.syncframework.common.SyncData;
import de.consistec.syncframework.common.SyncDataHolder;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.client.ClientSyncProvider;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.schema.Column;
import de.consistec.syncframework.common.data.schema.Constraint;
import de.consistec.syncframework.common.data.schema.ConstraintType;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.data.schema.Table;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.server.ServerSyncProvider;
import de.consistec.syncframework.impl.TestDatabase;
import de.consistec.syncframework.impl.TestScenario;
import de.consistec.syncframework.impl.adapter.MySqlDatabaseAdapter;
import de.consistec.syncframework.impl.adapter.PostgresDatabaseAdapter;
import de.consistec.syncframework.impl.adapter.it_mysql.MySqlDatabase;
import de.consistec.syncframework.impl.adapter.it_postgres.PostgresDatabase;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import javax.sql.DataSource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.postgresql.jdbc2.optional.PoolingDataSource;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 30.01.13 11:28
 */
@RunWith(value = Parameterized.class)
public class ClientProviderTransactionTest {

    protected static String[] tableNames = new String[]{"categories", "items", "categories_md", "items_id"};
    private TestDatabase db;
    private static boolean isApplyingChange = false;
    private IDatabaseAdapter dbAdapter;

    private static DataSource pooledDataSource;

    static {
        pooledDataSource = createDatasource();
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<TestDatabaseWithAdapter[]> AllDatabases() {
        return Arrays.asList(new TestDatabaseWithAdapter[][]{
            {new PostgresDatabaseWithAdapter()},
            {new MySqlDatabaseWithAdapter()},});
    }

    public ClientProviderTransactionTest(TestDatabaseWithAdapter dbWithAdapter) {
        this.db = dbWithAdapter.getDB();
        this.dbAdapter = dbWithAdapter.getDatabaseAdatper();
    }

//    @BeforeClass
//    public static void setUpClass() {
//        pooledDataSource = createDatasource();
//    }

    @Before
    public void setUp() throws IOException, SQLException, DatabaseAdapterException {
        db.init();

        db.dropTablesOnServer(tableNames);
        db.dropTablesOnClient(tableNames);

        Schema dbSchema = buildSchema();

        db.createSchemaOnClient(dbSchema);
        db.createSchemaOnServer(dbSchema);
    }

    private Schema buildSchema() throws DatabaseAdapterException {
        Schema schema = new Schema();
        Table table;
        Constraint constraint;

        // create table categories
        Column categoryid = new Column("categoryid", Types.INTEGER);
        categoryid.setNullable(false);
        categoryid.setSize(10);
        categoryid.setDecimalDigits(0);

        Column categoryname = new Column("categoryname", Types.VARCHAR);
        categoryname.setNullable(true);
        categoryname.setSize(300);
        categoryname.setDecimalDigits(0);

        Column categorydescription = new Column("description", Types.VARCHAR);
        categorydescription.setNullable(true);
        categorydescription.setSize(300);
        categorydescription.setDecimalDigits(0);

        table = new Table("categories");
        table.add(categoryid, categoryname, categorydescription);

        constraint = new Constraint(ConstraintType.PRIMARY_KEY, "DATAPK", "categoryid");
        table.add(constraint);

        schema.addTables(table);

        // create table items
        Column itemsid = new Column("id", Types.INTEGER);
        itemsid.setNullable(false);
        itemsid.setSize(10);
        itemsid.setDecimalDigits(0);

        Column itemsname = new Column("name", Types.VARCHAR);
        itemsname.setNullable(true);
        itemsname.setSize(300);
        itemsname.setDecimalDigits(0);

        Column itemsdescription = new Column("description", Types.VARCHAR);
        itemsdescription.setNullable(true);
        itemsdescription.setSize(300);
        itemsdescription.setDecimalDigits(0);

        table = new Table("items");
        table.add(itemsid, itemsname, itemsdescription);

        constraint = new Constraint(ConstraintType.PRIMARY_KEY, "DATAID", "id");
        table.add(constraint);

        schema.addTables(table);

        return schema;
    }

    /**
     * Closes server and client connection.
     *
     * @throws java.sql.SQLException
     */
    @After
    public void tearDown() throws SQLException {
        db.closeConnectionsOnServer();
        db.closeConnectionsOnClient();
    }

    @Test(expected = DatabaseAdapterException.class)
    public void transactionFailed() throws DatabaseAdapterException, SQLException, SyncException, IOException {

        TestScenario scenario = new TestScenario("transaction failed", BIDIRECTIONAL, SERVER_WINS)
            .expectServer("S")
            .expectClient("C");


        String[] insertClientQuery = new String[]{
            "INSERT INTO categories (categoryid, categoryname, description) VALUES (1, 'Beverages', 'Soft drinks')",
            "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (1, '8F3CCBD3FE5C9106253D472F6E36F0E1', 1, 1)",};


        String[] insertServerQuery = new String[]{
            "INSERT INTO categories (categoryid, categoryname, description) VALUES (2, 'Condiments', 'Sweet and ')",
            "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (2, null, 1, 0)",
            "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (2, '75901F57520C09EB990837C7AA93F717', 2, 0)",};

        db.executeQueriesOnClient(insertClientQuery);
        db.executeQueriesOnServer(insertServerQuery);

        scenario.setDatabase(db);

        scenario.setSelectQueries(new String[]{
            "select * from categories order by categoryid asc",
            "select * from categories_md order by pk asc"
        });

        scenario.saveCurrentState();

        ClientSyncProvider clientProvider = new ClientSyncProvider(new TableSyncStrategies(), dbAdapter);

        ServerSyncProvider serverProvider = new ServerSyncProvider(new TableSyncStrategies(), db.getServerDs());

//        createAndSetNewConnection(dbAdapter, true);

        int clientRevision = clientProvider.getLastRevision();

//        createAndSetNewConnection(dbAdapter, true);
        SyncData serverData = serverProvider.getChanges(clientRevision);

        try {
//            createAndSetNewConnection(dbAdapter, false);
            clientProvider.beginTransaction();
            SyncData clientData = clientProvider.getChanges();
            Change cachedChange = serverData.getChanges().get(0);
            SyncDataHolder dataHolder = clientProvider.resolveConflicts(serverData, clientData);
            SyncData clientChangesToApply = dataHolder.getClientSyncData();

            // insert serverChanged again to server changeset to force Exception
            dataHolder.getServerSyncData().getChanges().add(cachedChange);
            isApplyingChange = true;
            int currentRevision = clientProvider.applyChanges(dataHolder.getServerSyncData());
            clientProvider.commit();
        } catch (SyncException e) {
            if (e.getCause() instanceof DatabaseAdapterException) {
                db.init();
                // compare db content and test rollback
                scenario.assertClientIsInExpectedState();
                scenario.assertServerIsInExpectedState();
                throw (DatabaseAdapterException) e.getCause();
            } else {
                Assert.fail("Test should throw DatabaseAdapterException!");
            }
            throw e;
        }

        Assert.fail("Test should throw UniqueConstraintException!");
    }

//    private void createAndSetNewConnection(IDatabaseAdapter dbAdapter, boolean autocommit) throws SQLException,
//        DatabaseAdapterInstantiationException {
//        DumpDataSource ds = new DumpDataSource(DumpDataSource.SupportedDatabases.POSTGRESQL, ConnectionType.CLIENT);
//        Connection connection = ds.getConnection();
//        connection.setAutoCommit(autocommit);
//        dbAdapter.init(connection);
//    }

    @Test
    public void transactionCommitted() throws DatabaseAdapterException, SQLException, SyncException {

        TestScenario scenario = new TestScenario("transaction failed", BIDIRECTIONAL, SERVER_WINS)
            .expectServer("SS")
            .expectClient("SS");


        String[] insertClientQuery = new String[]{
            "INSERT INTO categories (categoryid, categoryname, description) VALUES (1, 'Beverages', 'Soft drinks')",
            "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (1, '8F3CCBD3FE5C9106253D472F6E36F0E1', 1, 1)",};


        String[] insertServerQuery = new String[]{
            "INSERT INTO categories (categoryid, categoryname, description) VALUES (1, 'Beverages', 'Soft drinks')",
            "INSERT INTO categories (categoryid, categoryname, description) VALUES (2, 'Condiments', 'Sweet and ')",
            "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (1, '8F3CCBD3FE5C9106253D472F6E36F0E1', 1, 1)",
            "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (2, '75901F57520C09EB990837C7AA93F717', 2, 0)",};

        db.executeQueriesOnClient(insertClientQuery);
        db.executeQueriesOnServer(insertServerQuery);

        scenario.setDatabase(db);

        scenario.setSelectQueries(new String[]{
            "select * from categories order by categoryid asc",
            "select * from categories_md order by pk asc"
        });

        scenario.saveCurrentState();

        ClientSyncProvider clientProvider = new ClientSyncProvider(new TableSyncStrategies(), db.getClientDs());
        ServerSyncProvider serverProvider = new ServerSyncProvider(new TableSyncStrategies(), db.getServerDs());

        int clientRevision = clientProvider.getLastRevision();

        SyncData serverData = serverProvider.getChanges(clientRevision);

        clientProvider.beginTransaction();
        SyncData clientData = clientProvider.getChanges();
        SyncDataHolder dataHolder = clientProvider.resolveConflicts(serverData, clientData);
        clientProvider.applyChanges(dataHolder.getServerSyncData());
        clientProvider.commit();

        scenario.assertClientIsInExpectedState();
        scenario.assertServerIsInExpectedState();
    }

    private static DataSource createDatasource() {
        PoolingDataSource source = new PoolingDataSource();
        source.setDataSourceName("datasource for pooling db connections");
        source.setServerName("localhost");
        source.setPortNumber(5432);
        source.setDatabaseName("client");
        source.setUser("syncuser");
        source.setPassword("syncuser");
        source.setMaxConnections(10);

        return source;
    }

    private static class PostgresDatabaseWithAdapter extends TestDatabaseWithAdapter {
        public PostgresDatabaseWithAdapter() {
            super(new PostgresDatabase());
            MockClientPostgresDatabaseAdapter dbAdapter = new MockClientPostgresDatabaseAdapter();
            try {
                dbAdapter.init(pooledDataSource.getConnection());
            } catch (SQLException e) {
                e.printStackTrace(
                    System.err);  //To change body of catch statement use File | Settings | File Templates.
            }
            setDatabaseAdapter(dbAdapter);
        }

        private class MockClientPostgresDatabaseAdapter extends PostgresDatabaseAdapter {
            @Override
            public void updateMdRow(final int rev, final int flag, final Object pk, final String mdv,
                                    final String tableName
            ) throws
                DatabaseAdapterException {
                if (isApplyingChange) {
                    // to force an exception we just exchange the update method with insert
                    super.insertMdRow(rev, flag, pk, mdv, tableName);
                } else {
                    super.updateMdRow(rev, flag, pk, mdv, tableName);
                }
            }
        }
    }

    private static class MySqlDatabaseWithAdapter extends TestDatabaseWithAdapter {

        public MySqlDatabaseWithAdapter() {
            super(new MySqlDatabase());
            MockClientMySqlDatabaseAdapter dbAdapter = new MockClientMySqlDatabaseAdapter();
            try {
                dbAdapter.init(pooledDataSource.getConnection());
            } catch (SQLException e) {
                e.printStackTrace(
                    System.err);  //To change body of catch statement use File | Settings | File Templates.
            }
            setDatabaseAdapter(dbAdapter);
        }

        private class MockClientMySqlDatabaseAdapter extends MySqlDatabaseAdapter {
            @Override
            public void updateMdRow(final int rev, final int flag, final Object pk, final String mdv,
                                    final String tableName
            ) throws
                DatabaseAdapterException {
                if (isApplyingChange) {
                    // to force an exception we just exchange the update method with insert
                    super.insertMdRow(rev, flag, pk, mdv, tableName);
                } else {
                    super.updateMdRow(rev, flag, pk, mdv, tableName);
                }
            }
        }
    }

    private static class TestDatabaseWithAdapter {
        private IDatabaseAdapter adapter;
        private TestDatabase db;

        public TestDatabaseWithAdapter(final TestDatabase db) {
            this.db = db;
            this.adapter = adapter;
        }

        public void setDatabaseAdapter(IDatabaseAdapter adapter) {
            this.adapter = adapter;
        }

        public IDatabaseAdapter getDatabaseAdatper() {
            return adapter;
        }

        public TestDatabase getDB() {
            return db;
        }
    }
}
