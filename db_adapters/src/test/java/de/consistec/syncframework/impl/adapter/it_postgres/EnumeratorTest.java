package de.consistec.syncframework.impl.adapter.it_postgres;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.impl.TestDatabase;
import de.consistec.syncframework.impl.adapter.TestUtil;

import java.io.IOException;
import java.sql.SQLException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 28.01.13 15:44
 */
public class EnumeratorTest {

    protected static String[] tableNames = new String[]{"categories", "categories_md", "items", "items_md"};
    protected static String[] createQueries = new String[]{
        "CREATE TABLE categories (categoryid INTEGER NOT NULL PRIMARY KEY ,categoryname VARCHAR (300),description VARCHAR (300));",
        "CREATE TABLE categories_md (pk INTEGER NOT NULL PRIMARY KEY, mdv VARCHAR (300), rev INTEGER DEFAULT 1, f INTEGER DEFAULT 0);",
        "CREATE TABLE items (id INTEGER NOT NULL PRIMARY KEY ,name VARCHAR (300),description VARCHAR (300));",
        "CREATE TABLE items_md (pk INTEGER NOT NULL PRIMARY KEY, mdv VARCHAR (300), rev INTEGER DEFAULT 1, f INTEGER DEFAULT 0);"};

    private static ConflictStrategy savedConflictStrategy;
    private static SyncDirection savedSyncDirection;

    protected static TestDatabase postgresDB;


    @BeforeClass
    public static void setUpClass() throws Exception {
        postgresDB = new PostgresDatabase();

        TestUtil.initConfig(ServerChangesEnumeratorTest.class, postgresDB.getConfigFile());

        savedConflictStrategy = Config.getInstance().getGlobalConflictStrategy();
        savedSyncDirection = Config.getInstance().getGlobalSyncDirection();
    }

    @Before
    public void setUp() throws IOException, SQLException {
        postgresDB.init();

        postgresDB.dropTablesOnServer(tableNames);
        postgresDB.dropTablesOnClient(tableNames);

        postgresDB.executeQueriesOnClient(createQueries);
        postgresDB.executeQueriesOnServer(createQueries);

        Config.getInstance().setGlobalConflictStrategy(savedConflictStrategy);
        Config.getInstance().setGlobalSyncDirection(savedSyncDirection);
    }

    /**
     * Closes server and client connection.
     *
     * @throws java.sql.SQLException
     */
    @AfterClass
    public static void tearDownClass() throws SQLException {

        postgresDB.clean();
    }

}
