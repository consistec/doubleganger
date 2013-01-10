package de.consistec.syncframework.impl.adapter.it_postgres;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.impl.adapter.ConnectionType;
import de.consistec.syncframework.impl.adapter.DefaultSyncTest;
import de.consistec.syncframework.impl.adapter.DumpDataSource;

import java.io.IOException;
import java.sql.Connection;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Performs integration test with
 * {@link de.consistec.syncframework.impl.adapter.PostgresDatabaseAdapter PostgresDatabaseAdapter}
 * and PostgreSQL database.
 * <br/>This class performs test with adapter managed connections.
 *
 * @author Markus
 * @company Consistec Engineering and Consulting GmbH
 * @date 18.04.12 14:19
 * @since 0.0.1-SNAPSHOT
 */
public class ITAllOperationsPostgreSQL extends DefaultSyncTest {

    public static final String CONFIG_FILE = "/config_postgre.properties";
    protected static final DumpDataSource clientDs = new DumpDataSource(DumpDataSource.SupportedDatabases.POSTGRESQL,
        ConnectionType.CLIENT);
    protected static final DumpDataSource serverDs = new DumpDataSource(DumpDataSource.SupportedDatabases.POSTGRESQL,
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
    protected String[] getCreateTableStatement() {
        return new String[]{
            "create table categories (\"categoryid\" INTEGER NOT NULL PRIMARY KEY ,\"categoryname\" VARCHAR (30000),\"description\" VARCHAR (30000));",
            "create table items (\"itemid\" INTEGER NOT NULL PRIMARY KEY ,\"itemname\" VARCHAR (30000),\"description\" VARCHAR (30000));"};
    }

}
