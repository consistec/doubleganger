package de.consistec.syncframework.impl.adapter.it_alldb;

import static de.consistec.syncframework.common.SyncDirection.BIDIRECTIONAL;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.SERVER_WINS;

import de.consistec.syncframework.common.SyncData;
import de.consistec.syncframework.common.SyncDataHolder;
import de.consistec.syncframework.common.TableSyncStrategies;
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
import de.consistec.syncframework.impl.adapter.it_mysql.MySqlDatabase;
import de.consistec.syncframework.impl.adapter.it_postgres.PostgresDatabase;
import de.consistec.syncframework.impl.adapter.it_sqlite.SqlLiteDatabase;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 30.01.13 11:28
 */
@Ignore("This test should be checked for exception handling")
@RunWith(value = Parameterized.class)
public class ClientProviderTransactionTest {

    protected static String[] tableNames = new String[]{"categories", "items", "categories_md", "items_id"};

    private TestDatabase db;

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<TestDatabase[]> AllDatabases() {
        return Arrays.asList(new TestDatabase[][]{
                {new PostgresDatabase()},
                {new MySqlDatabase()},
                {new SqlLiteDatabase()},});
    }

    public ClientProviderTransactionTest(TestDatabase db) {
        this.db = db;
    }

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

            // insert serverChanged again to server changeset to force Exception
            dataHolder.getServerSyncData().getChanges().add(cachedChange);
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
}
