package de.consistec.syncframework.common.server;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.adapter.DumpDbAdapter;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.data.Change;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
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
    private DumpDbAdapter databaseAdapterMock;

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
    }

    @Test//(expected = TransactionAbortedException.class)
    public void applyChanges() throws Exception {

        ServerSyncProvider serverSyncProvider = new ServerSyncProvider(new TableSyncStrategies());

//        doNothing().when(
//            databaseAdapterMock).init(connection);
        when(databaseAdapterMock.getConnection()).thenReturn(connectionMock);
        doCallRealMethod().when(databaseAdapterMock).init(connectionMock);
        doCallRealMethod().when(databaseAdapterMock).commit();

        doThrow(TRANSACTION_EXCEPTION).when(
            databaseAdapterMock).commit();

        databaseAdapterMock.commit();

        serverSyncProvider.applyChanges(new ArrayList<Change>(), 0);

        verify(databaseAdapterMock).commit();
    }
}
