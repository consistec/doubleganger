package de.consistec.doubleganger.common.client;

/*
 * #%L
 * Project - doubleganger
 * File - SyncAgentTest.java
 * %%
 * Copyright (C) 2011 - 2013 consistec GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static de.consistec.doubleganger.common.i18n.MessageReader.read;
import static de.consistec.doubleganger.common.server.ServerStatus.CLIENT_NOT_UPTODATE;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import de.consistec.doubleganger.common.SyncData;
import de.consistec.doubleganger.common.SyncDataHolder;
import de.consistec.doubleganger.common.data.Change;
import de.consistec.doubleganger.common.exception.ContextException;
import de.consistec.doubleganger.common.exception.ServerStatusException;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.i18n.Errors;
import de.consistec.doubleganger.common.server.IServerSyncProvider;

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
 * @company consistec Engineering and Consulting GmbH
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

        SyncData data1 = new SyncData();
        data1.setRevision(1);
        SyncData data2 = new SyncData();
        data1.setRevision(2);

        when(this.serverSyncProviderMock.getChanges(anyInt())).thenReturn(data1);
        // throw exception to repeat the sync
        when(this.serverSyncProviderMock.applyChanges((SyncData) anyObject())).thenThrow(new ServerStatusException(
            CLIENT_NOT_UPTODATE, read(Errors.COMMON_UPDATE_NECESSARY)));
        when(this.clientSyncProviderMock.resolveConflicts((SyncData) anyObject(), (SyncData) anyObject())).thenReturn(
            new SyncDataHolder(data1, data2));

        SyncAgent agent = new SyncAgent(this.serverSyncProviderMock, this.clientSyncProviderMock);
        agent.synchronize();
    }
}
