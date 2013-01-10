package de.consistec.syncframework.impl.adapter.it_mysql;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.impl.adapter.AbstractSyncTest;
import de.consistec.syncframework.impl.adapter.ConnectionType;
import de.consistec.syncframework.impl.adapter.DumpDataSource;

import java.io.IOException;
import java.sql.Connection;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Performs integration test with
 * {@link de.consistec.syncframework.impl.adapter.GenericDatabaseAdapter GenericDatabaseAdapter} and MySQL database.
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 18.04.12 14:19
 * @author Markus
 * @since 0.0.1-SNAPSHOT
 */
public class ITAllOperationsMySQL extends AbstractSyncTest {

    public static final String CONFIG_FILE = "/config_mysql.properties";
    private static final DumpDataSource clientDs = new DumpDataSource(DumpDataSource.SupportedDatabases.MYSQL,
        ConnectionType.CLIENT);
    private static final DumpDataSource serverDs = new DumpDataSource(DumpDataSource.SupportedDatabases.MYSQL,
        ConnectionType.SERVER);

    @BeforeClass
    public static void setUpClass() throws Exception {
        clientConnection = clientDs.getConnection();
        serverConnection = serverDs.getConnection();
    }

    @Before
    public void setUp() throws IOException {
        Config.getInstance().loadFromFile(getClass().getResourceAsStream(CONFIG_FILE));
    }

    @Override
    public Connection getServerConnection() {
        return serverConnection;
    }

    @Override
    public Connection getClientConnection() {
        return clientConnection;
    }

    @Override
    protected String[] getCreateTableQueries() {
        return new String[]{
                "create table categories (categoryid INTEGER NOT NULL PRIMARY KEY, categoryname VARCHAR (30000), description VARCHAR (30000));",
                "create table items (itemid INTEGER NOT NULL PRIMARY KEY, itemname VARCHAR (30000), description VARCHAR (30000));" };
    }

}
