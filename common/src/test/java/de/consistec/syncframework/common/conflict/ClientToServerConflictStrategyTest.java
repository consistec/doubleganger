package de.consistec.syncframework.common.conflict;

import static de.consistec.syncframework.common.util.CollectionsUtil.newHashMap;
import static org.junit.Assert.assertTrue;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.IConflictListener;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.client.ConflictHandlingData;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.MDEntry;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterInstantiationException;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * This class tests the correct handling of getChanges for client and server side.
 *
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 13.12.12 11:48
 */
public class ClientToServerConflictStrategyTest {

    private static final String TEST_STRING = "testString";
    private static final String TEST_TABLE_NAME = "testTableName";
    private static final String TEST_COLUMN1 = "column1";
    private static final String TEST_COLUMN2 = "column2";
    private static final String TEST_COLUMN3 = "column3";
    private static final String TEST_COLUMN4 = "column4";
    private static final String TEST_COLUMN5 = "column5";
    private static final String TEST_MDV = "6767e648767786786dsffdsa786dfsaf";

    private IConflictStrategy conflictStrategy = null;

    @Mock
    private IDatabaseAdapter databaseAdapterMock;
    @Mock
    private ConflictHandlingData conflictHandlingData;

    public ClientToServerConflictStrategyTest() throws DatabaseAdapterInstantiationException {
        conflictStrategy = ConflictStrategyFactory.newInstance(SyncDirection.CLIENT_TO_SERVER);
    }

    @Before
    public void before() {
        // related to the above defined mock annotations
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createConflictStrategy() {
        assertTrue(conflictStrategy instanceof ClientToServerConflictStrategy);
    }

    @Test
    public void resolveByClientWinsStrategy() throws DatabaseAdapterException, NoSuchAlgorithmException {

        Config confInstance = Config.getInstance();
        confInstance.setGlobalConflictStrategy(ConflictStrategy.SERVER_WINS);

        conflictStrategy.resolveByClientWinsStrategy(databaseAdapterMock, conflictHandlingData);

        Mockito.verify(databaseAdapterMock, Mockito.never()).getConnection();
        Mockito.verify(conflictHandlingData, Mockito.never()).getRemoteChange();
        Mockito.verifyZeroInteractions(databaseAdapterMock, conflictHandlingData);
    }

    @Test(expected = IllegalStateException.class)
    public void resolveByServerWinsStrategy() throws DatabaseAdapterException, NoSuchAlgorithmException {

        Config confInstance = Config.getInstance();
        confInstance.setGlobalConflictStrategy(ConflictStrategy.SERVER_WINS);

        MDEntry remoteEntry = new MDEntry(Integer.valueOf(1), false, 1, TEST_TABLE_NAME, TEST_MDV);
        Change remoteChange = new Change(remoteEntry, createRowData());

        ConflictHandlingData conflictHandlingData = new ConflictHandlingData(0, 0, "34drf324324h4jkhkjhjh",
            remoteChange);
        conflictStrategy.resolveByServerWinsStrategy(databaseAdapterMock, conflictHandlingData);
    }

    @Test(expected = IllegalStateException.class)
    public void resolveByFireEventStrategy() throws DatabaseAdapterException, NoSuchAlgorithmException, SyncException {

        Config confInstance = Config.getInstance();
        confInstance.setGlobalConflictStrategy(ConflictStrategy.FIRE_EVENT);

        MDEntry remoteEntry = new MDEntry(Integer.valueOf(1), false, 1, TEST_TABLE_NAME, TEST_MDV);
        Change remoteChange = new Change(remoteEntry, createRowData());

        ConflictHandlingData conflictHandlingData = new ConflictHandlingData(0, 0, "34drf324324h4jkhkjhjh",
            remoteChange);
        Map<String, Object> clientData = newHashMap();
        conflictStrategy.resolveByFireEvent(databaseAdapterMock, conflictHandlingData, clientData,
            new IConflictListener() {
                @Override
                public Map<String, Object> resolve(final Map<String, Object> serverData,
                                                   final Map<String, Object> clientData
                ) {
                    return null;
                }
            });
    }

    private Map<String, Object> createRowData() {
        Map<String, Object> rowData = newHashMap();
        rowData.put(TEST_COLUMN1, 2);
        rowData.put(TEST_COLUMN2, TEST_STRING);
        rowData.put(TEST_COLUMN3, "false");
        rowData.put(TEST_COLUMN4, new Date(System.currentTimeMillis()));
        rowData.put(TEST_COLUMN5, 3.14);
        return rowData;
    }
}
