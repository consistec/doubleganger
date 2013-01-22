package de.consistec.syncframework.common.server;

import static de.consistec.syncframework.common.SyncDirection.BIDIRECTIONAL;
import static de.consistec.syncframework.common.SyncDirection.CLIENT_TO_SERVER;
import static de.consistec.syncframework.common.SyncDirection.SERVER_TO_CLIENT;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.CLIENT_WINS;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.SERVER_WINS;
import static de.consistec.syncframework.common.util.CollectionsUtil.newHashSet;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.SyncSettings;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.TableSyncStrategy;
import de.consistec.syncframework.common.adapter.DumpDbAdapter;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 14.01.13 09:19
 */
public class ServerSyncProviderTest {

//<editor-fold defaultstate="expanded" desc=" Class fields " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class constructors " >

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class methods " >

//</editor-fold>

    private static final SQLException TRANSACTION_EXCEPTION = new SQLException("test transaction aborted exception",
        "400001");
    private static final SQLException UNIQUE_EXCEPTION = new SQLException("test unique exception", "23505");

    @Mock
    private DataSource dataSourceMock;
    @Mock
    private Properties properties;
    @Mock
    private Connection connectionMock;
    @Mock
    private IDatabaseAdapter databaseAdapterMock;

//    @Mock
//    private ServerSyncProvider serverSyncProviderMock;


    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        Config config = Config.getInstance();
//        config.init(getClass().getResourceAsStream("/test_config_postgre.properties"));
        config.setServerDatabaseAdapter(DumpDbAdapter.class);

        config.setGlobalConflictStrategy(ConflictStrategy.SERVER_WINS);
        config.setGlobalSyncDirection(SyncDirection.BIDIRECTIONAL);

//        databaseAdapterMock.get.connection = connectionMock;
    }

    @Test
    public void validateClientSettings() throws DatabaseAdapterException, SyncException {


        Config.getInstance().addSyncTable("categories", "items");
        Config.getInstance().setMdTableSuffix("_md");

        TableSyncStrategies serverSyncStrategies = new TableSyncStrategies();
        serverSyncStrategies.addSyncStrategyForTable("categories",
            new TableSyncStrategy(SERVER_TO_CLIENT, SERVER_WINS));
        serverSyncStrategies.addSyncStrategyForTable("items",
            new TableSyncStrategy(BIDIRECTIONAL, SERVER_WINS));

        ServerSyncProvider serverSyncProvider = new ServerSyncProvider(serverSyncStrategies, databaseAdapterMock);

        Set<String> tablesToSync = newHashSet();
        tablesToSync.add("categories");
        tablesToSync.add("items");

        TableSyncStrategies clientSyncStrategies = new TableSyncStrategies();
        clientSyncStrategies.addSyncStrategyForTable("categories",
            new TableSyncStrategy(SERVER_TO_CLIENT, SERVER_WINS));
        clientSyncStrategies.addSyncStrategyForTable("items",
            new TableSyncStrategy(BIDIRECTIONAL, SERVER_WINS));


        when(databaseAdapterMock.getConnection()).thenReturn(connectionMock);
        when(databaseAdapterMock.existsMDTable(any(String.class))).thenReturn(true);

        SyncSettings clientSettings = new SyncSettings(tablesToSync, clientSyncStrategies);
        serverSyncProvider.validate(clientSettings);
    }

    @Test(expected = SyncException.class)
    public void validateClientSettingsFail() throws DatabaseAdapterException, SyncException {

        TableSyncStrategies serverSyncStrategies = new TableSyncStrategies();
        serverSyncStrategies.addSyncStrategyForTable("categories",
            new TableSyncStrategy(SERVER_TO_CLIENT, SERVER_WINS));
        serverSyncStrategies.addSyncStrategyForTable("items",
            new TableSyncStrategy(BIDIRECTIONAL, SERVER_WINS));

        ServerSyncProvider serverSyncProvider = new ServerSyncProvider(serverSyncStrategies, databaseAdapterMock);

        Set<String> tablesToSync = newHashSet();
        tablesToSync.add("categories");
        tablesToSync.add("items");

        TableSyncStrategies clientSyncStrategies = new TableSyncStrategies();
        clientSyncStrategies.addSyncStrategyForTable("categories",
            new TableSyncStrategy(CLIENT_TO_SERVER, CLIENT_WINS));
        clientSyncStrategies.addSyncStrategyForTable("items",
            new TableSyncStrategy(BIDIRECTIONAL, SERVER_WINS));

        SyncSettings clientSettings = new SyncSettings(tablesToSync, clientSyncStrategies);
        serverSyncProvider.validate(clientSettings);
    }
}
