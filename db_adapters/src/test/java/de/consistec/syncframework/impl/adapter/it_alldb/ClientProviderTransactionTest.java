package de.consistec.syncframework.impl.adapter.it_alldb;

import static de.consistec.syncframework.common.SyncDirection.BIDIRECTIONAL;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.SERVER_WINS;

import de.consistec.syncframework.common.SyncData;
import de.consistec.syncframework.common.SyncDataHolder;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.client.ClientSyncProvider;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.server.ServerSyncProvider;
import de.consistec.syncframework.impl.TestDatabase;
import de.consistec.syncframework.impl.TestScenario;
import de.consistec.syncframework.impl.adapter.it_mysql.MySqlDatabase;
import de.consistec.syncframework.impl.adapter.it_postgres.PostgresDatabase;
import de.consistec.syncframework.impl.adapter.it_sqlite.SqlLiteDatabase;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 30.01.13 11:28
 */
@RunWith(value = Parameterized.class)
public class ClientProviderTransactionTest {

    protected static String[] tableNames = new String[]{"categories", "categories_md", "items", "items_md"};
    protected static String[] createQueries = new String[]{
        "CREATE TABLE categories (categoryid INTEGER NOT NULL PRIMARY KEY ,categoryname VARCHAR (300),description VARCHAR (300));",
        "CREATE TABLE categories_md (pk INTEGER NOT NULL PRIMARY KEY, mdv VARCHAR (300), rev INTEGER DEFAULT 1, f INTEGER DEFAULT 0);",
        "CREATE TABLE items (id INTEGER NOT NULL PRIMARY KEY ,name VARCHAR (300),description VARCHAR (300));",
        "CREATE TABLE items_md (pk INTEGER NOT NULL PRIMARY KEY, mdv VARCHAR (300), rev INTEGER DEFAULT 1, f INTEGER DEFAULT 0);"};

    private TestDatabase db;

    public ClientProviderTransactionTest(TestDatabase db) {
        this.db = db;
    }

    @Before
    public void setUp() throws IOException, SQLException {
        db.init();

        db.dropTablesOnServer(tableNames);
        db.dropTablesOnClient(tableNames);

        db.executeQueriesOnClient(createQueries);
        db.executeQueriesOnServer(createQueries);
    }

    /**
     * Closes server and client connection.
     *
     * @throws java.sql.SQLException
     */
    @After
    public void tearDown() throws SQLException {

        db.closeConnections();
    }

    @Test(expected = DatabaseAdapterException.class)
    public void transactionFailed() throws DatabaseAdapterException, SQLException, SyncException {

        TestScenario scenario = new TestScenario("transaction failed", BIDIRECTIONAL, SERVER_WINS)
            .expectServer("S")
            .expectClient("C");


        String[] insertClientQuery = new String[]{
            "INSERT INTO categories (categoryid, categoryname, description) VALUES (1, 'Beverages', 'Soft drinks')",
            "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (1, '8F3CCBD3FE5C9106253D472F6E36F0E1', 1, 1)",};


        String[] insertServerQuery = new String[]{
//            "DELETE FROM categories WHERE pk = 1",
            "INSERT INTO categories (categoryid, categoryname, description) VALUES (2, 'Condiments', 'Sweet and ')",
            "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (2, null, 1, 0)",
            "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (2, '75901F57520C09EB990837C7AA93F717', 2, 0)",};

        db.executeQueriesOnClient(insertClientQuery);
        db.executeQueriesOnServer(insertServerQuery);

        scenario.setDataSources(db.getServerDs(), db.getClientDs());
        scenario.setConnections(db.getServerConnection(), db.getClientConnection());
        scenario.setSelectQueries(new String[]{
            "select * from categories order by categoryid asc",
            "select * from categories_md order by pk asc"
        });

        scenario.saveCurrentState();

        ClientSyncProvider clientProvider = new ClientSyncProvider(new TableSyncStrategies(), db.getClientDs());
        ServerSyncProvider serverProvider = new ServerSyncProvider(new TableSyncStrategies(), db.getServerDs());

        int clientRevision = clientProvider.getLastRevision();

        SyncData serverData = serverProvider.getChanges(clientRevision);

        try {
            clientProvider.beginTransaction();
            SyncData clientData = clientProvider.getChanges();
            Change cachedChange = serverData.getChanges().get(0);
            SyncDataHolder dataHolder = clientProvider.resolveConflicts(serverData, clientData);
            SyncData clientChangesToApply = dataHolder.getClientSyncData();

            // insert serverChanged again to server changeset to force Exception
            dataHolder.getServerSyncData().getChanges().add(cachedChange);
            int currentRevision = clientProvider.applyChanges(dataHolder.getServerSyncData());
            clientProvider.commit();
        } catch (SyncException e) {
            if (e.getCause() instanceof DatabaseAdapterException) {
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

        scenario.setDataSources(db.getServerDs(), db.getClientDs());
        scenario.setConnections(db.getServerConnection(), db.getClientConnection());
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

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<TestDatabase[]> AllDatabases() {
        return Arrays.asList(new TestDatabase[][]{
            {new PostgresDatabase()}, {new MySqlDatabase()}, {new SqlLiteDatabase()},
        });
    }
}
