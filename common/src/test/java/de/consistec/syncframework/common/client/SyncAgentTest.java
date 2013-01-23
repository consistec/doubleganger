package de.consistec.syncframework.common.client;

import static de.consistec.syncframework.common.i18n.MessageReader.read;
import static de.consistec.syncframework.common.server.ServerStatus.CLIENT_NOT_UPTODATE;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import de.consistec.syncframework.common.SyncData;
import de.consistec.syncframework.common.SyncDataHolder;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.ServerStatusException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.i18n.Errors;
import de.consistec.syncframework.common.server.IServerSyncProvider;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 23.01.13 11:08
 */
public class SyncAgentTest {


    protected static final Logger LOGGER = LoggerFactory.getLogger(SyncAgentTest.class.getCanonicalName());


    @Mock
    private IServerSyncProvider serverSyncProviderMock;

    @Mock
    private IClientSyncProvider clientSyncProviderMock;


    @BeforeClass
    public static void setUpClass() {
        // initialize logging framework
        DOMConfigurator.configure(ClassLoader.getSystemResource("log4j.xml"));
    }

    @Before
    public void setUp() throws IOException, SQLException {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = ServerStatusException.class)
    public void repeatSyncDueToClientNotUptoDate() throws ContextException, SyncException {

        when(this.serverSyncProviderMock.getChanges(anyInt())).thenReturn(new SyncData(1, new ArrayList<Change>()));
        // throw exception to repeat the sync
        when(this.serverSyncProviderMock.applyChanges((SyncData) anyObject())).thenThrow(new ServerStatusException(
            CLIENT_NOT_UPTODATE, read(Errors.COMMON_UPDATE_NECESSARY)));
        when(this.clientSyncProviderMock.resolveConflicts((SyncData) anyObject(), (SyncData) anyObject())).thenReturn(
            new SyncDataHolder(new SyncData(1, new ArrayList<Change>()), new SyncData(2, new ArrayList<Change>())));

        SyncAgent agent = new SyncAgent(this.serverSyncProviderMock, this.clientSyncProviderMock);
        agent.synchronize();
    }
}
