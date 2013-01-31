package de.consistec.syncframework.impl.adapter;

import static de.consistec.syncframework.common.i18n.MessageReader.read;
import static de.consistec.syncframework.common.util.CollectionsUtil.newHashSet;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.SyncData;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.adapter.DatabaseAdapterCallback;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.TransactionAbortedException;
import de.consistec.syncframework.common.exception.database_adapter.UniqueConstraintException;
import de.consistec.syncframework.common.i18n.Errors;
import de.consistec.syncframework.common.server.ServerSyncProvider;
import de.consistec.syncframework.impl.i18n.DBAdapterErrors;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 14.01.13 09:19
 */
//@RunWith(PowerMockRunner.class)
//@PrepareForTest(PostgresDatabaseAdapter.class)
public class ServerSyncProviderTest {

    private static final SQLException TRANSACTION_SQL_EXCEPTION = new SQLException(
        "test transaction aborted exception",
        "400001");
    private static final TransactionAbortedException TRANSACTION_EXCEPTION = new TransactionAbortedException(
        "test transaction aborted exception");
    private static final UniqueConstraintException UNIQUE_EXCEPTION = new UniqueConstraintException(
        read(DBAdapterErrors.CANT_INSERT_DATA_ROW, "categories"));
    @Mock
    private DataSource dataSourceMock;
    //    @Mock
//    private Properties properties;
    @Mock
    private Connection connectionMock;
    @Mock
    private PreparedStatement statementMock;
    @Mock
    private PostgresDatabaseAdapter databaseAdapterMock;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        Set<String> tableSet = newHashSet();
        tableSet.add("categories");

        Config config = Config.getInstance();
        config.init(getClass().getResourceAsStream("/config_postgre.properties"));

        config.setGlobalConflictStrategy(ConflictStrategy.SERVER_WINS);
        config.setGlobalSyncDirection(SyncDirection.BIDIRECTIONAL);
        config.setRetryNumberOfApplyChangesOnTransactionError(0);
        config.setRetryNumberOfGetChangesOnTransactionError(0);

        databaseAdapterMock.connection = connectionMock;
    }

    @Test
    public void applyChangesThrowsTransactionAbortedException() throws Exception {
        thrown.expect(SyncException.class);
        thrown.expectMessage(read(Errors.COMMON_CANT_APPLY_CLIENT_CHANGES_FOR_N_TIME, 0));

        ServerSyncProvider serverSyncProvider = new ServerSyncProvider(new TableSyncStrategies(), databaseAdapterMock);

        doNothing().when(
            databaseAdapterMock).init(connectionMock);
        when(databaseAdapterMock.getConnection()).thenReturn(connectionMock);
        when(databaseAdapterMock.getNextRevision()).thenReturn(1);
        doCallRealMethod().when(databaseAdapterMock).commit();
        doThrow(TRANSACTION_SQL_EXCEPTION).when(
            connectionMock).commit();

        serverSyncProvider.applyChanges(new SyncData(0, new ArrayList<Change>()));

        verify(databaseAdapterMock).commit();
    }

    @Test
    public void applyChangesThrowsUniqueKeyException() throws Exception {
        thrown.expect(SyncException.class);
        thrown.expectMessage(read(DBAdapterErrors.CANT_INSERT_DATA_ROW, "categories"));

        ServerSyncProvider serverSyncProvider = new ServerSyncProvider(new TableSyncStrategies(), databaseAdapterMock);

        doNothing().when(
            databaseAdapterMock).init(connectionMock);
        when(databaseAdapterMock.getConnection()).thenReturn(connectionMock);
        when(databaseAdapterMock.getNextRevision()).thenReturn(1);

        doThrow(UNIQUE_EXCEPTION).when(databaseAdapterMock).commit();

        serverSyncProvider.applyChanges(new SyncData(0, new ArrayList<Change>()));
    }

    @Test
    public void getChangesThrowsTransactionAbortedException() throws Exception {
        thrown.expect(SyncException.class);
        thrown.expectMessage(read(Errors.COMMON_CANT_GET_SERVER_CHANGES_FOR_N_TIME, 0));

        ServerSyncProvider serverSyncProvider = new ServerSyncProvider(new TableSyncStrategies(), databaseAdapterMock);

        doNothing().when(
            databaseAdapterMock).init(connectionMock);
        when(databaseAdapterMock.getConnection()).thenReturn(connectionMock);
        when(databaseAdapterMock.getLastRevision()).thenReturn(0);

        Answer throwTransactionException = new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
                throw TRANSACTION_EXCEPTION;
            }
        };
        doAnswer(throwTransactionException)
            .when(databaseAdapterMock).getAllRowsFromTable(anyString(), any(DatabaseAdapterCallback.class));
        doAnswer(throwTransactionException)
            .when(databaseAdapterMock).getChangesByFlag(anyString(), any(DatabaseAdapterCallback.class));

        serverSyncProvider.getChanges(0);
    }
}
