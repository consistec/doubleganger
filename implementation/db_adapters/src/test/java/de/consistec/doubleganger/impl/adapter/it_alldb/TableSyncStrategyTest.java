package de.consistec.doubleganger.impl.adapter.it_alldb;

/*
 * #%L
 * Project - doubleganger
 * File - TableSyncStrategyTest.java
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
import static de.consistec.doubleganger.common.MdTableDefaultValues.FLAG_COLUMN_NAME;
import static de.consistec.doubleganger.common.MdTableDefaultValues.MDV_COLUMN_NAME;
import static de.consistec.doubleganger.common.MdTableDefaultValues.REV_COLUMN_NAME;
import static de.consistec.doubleganger.common.adapter.DatabaseAdapterFactory.AdapterPurpose.SERVER;
import static de.consistec.doubleganger.common.util.CollectionsUtil.newArrayList;
import static de.consistec.doubleganger.common.util.CollectionsUtil.newHashMap;
import static de.consistec.doubleganger.impl.adapter.DummyDataSource.SupportedDatabases.POSTGRESQL;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.SyncContext;
import de.consistec.doubleganger.common.SyncData;
import de.consistec.doubleganger.common.SyncDataHolder;
import de.consistec.doubleganger.common.SyncDirection;
import de.consistec.doubleganger.common.TableSyncStrategies;
import de.consistec.doubleganger.common.TableSyncStrategy;
import de.consistec.doubleganger.common.adapter.IDatabaseAdapter;
import de.consistec.doubleganger.common.client.ClientHashProcessor;
import de.consistec.doubleganger.common.conflict.ConflictStrategy;
import de.consistec.doubleganger.common.data.Change;
import de.consistec.doubleganger.common.data.MDEntry;
import de.consistec.doubleganger.common.exception.ContextException;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.util.HashCalculator;
import de.consistec.doubleganger.impl.TestDatabase;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

/**
 * Tests if the sync stategy for a specific table, added to the {@link Config} instance
 * is really used in the resolveConflict method in the {@link ClientHashProcessor}.
 *
 * @author Marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 31.10.12 09:56
 * @since 0.0.1-SNAPSHOT
 */
public class TableSyncStrategyTest {

    private static final String TEST_STRING = "testString";
    private static final String TEST_TABLE_NAME = "testTablename";
    private static final String TEST_COLUMN1 = "column1";
    private static final String TEST_COLUMN2 = "column2";
    private static final String TEST_COLUMN3 = "column3";
    private static final String TEST_COLUMN4 = "column4";
    private static final String TEST_COLUMN5 = "column5";
    private static final String TEST_MDV = "6767e648767786786dsffdsa786dfsaf";
    private TestDatabase db = new TestDatabase(POSTGRESQL, SERVER, false);
    @Mock
    private ResultSet localDataResultSet;
    @Mock
    private ResultSet localHashResultSet;
    @Mock
    private IDatabaseAdapter dbAdapter;

    @Before
    public void before() throws IOException, SyncException, SQLException, ContextException {
        MockitoAnnotations.initMocks(this);
        db.init();
    }

    @Test
    public void resolveConflictRemoveClientChange() throws Exception {

        SyncData serverData = new SyncData();
        serverData.setRevision(1);
        MDEntry entry = new MDEntry(1, true, 1, TEST_TABLE_NAME, TEST_MDV);
        Map<String, Object> rowServerData = newHashMap();
        rowServerData.put(TEST_COLUMN1, 1);
        rowServerData.put(TEST_COLUMN2, TEST_STRING);
        rowServerData.put(TEST_COLUMN3, true);
        rowServerData.put(TEST_COLUMN4, new Date(System.currentTimeMillis()));
        rowServerData.put(TEST_COLUMN5, 4.5);
        Change remoteChange = new Change(entry, rowServerData);

        serverData.addChange(remoteChange);

        MDEntry clientEntry = new MDEntry(1, true, 1, TEST_TABLE_NAME, TEST_MDV);
        Map<String, Object> rowClientData = newHashMap();
        rowClientData.put(TEST_COLUMN1, 1);
        rowClientData.put(TEST_COLUMN2, TEST_STRING + "_updateClient");
        rowClientData.put(TEST_COLUMN3, true);
        rowClientData.put(TEST_COLUMN4, new Date(System.currentTimeMillis()));
        rowClientData.put(TEST_COLUMN5, 4.5);
        Change localChange = new Change(clientEntry, rowClientData);

        SyncData clientData = new SyncData();
        clientData.addChange(localChange);

        when(localDataResultSet.next()).thenReturn(false);

        when(localHashResultSet.getInt(REV_COLUMN_NAME)).thenReturn(7);
        when(localHashResultSet.getInt(FLAG_COLUMN_NAME)).thenReturn(1); // Client change
        HashCalculator clc = new HashCalculator();
        String hashValue = clc.calculateHash(remoteChange, false);
        when(localHashResultSet.getString(MDV_COLUMN_NAME)).thenReturn(hashValue);

        TableSyncStrategy strategy = new TableSyncStrategy(SyncDirection.SERVER_TO_CLIENT,
            ConflictStrategy.SERVER_WINS);

        TableSyncStrategies strategies = new TableSyncStrategies();
        strategies.addSyncStrategyForTable(TEST_TABLE_NAME, strategy);

        final SyncContext.LocalContext ctx = SyncContext.local(strategies);

        when(dbAdapter.getHashCalculator()).thenReturn(clc);

        SyncDataHolder dataHolder = Whitebox.<SyncDataHolder>invokeMethod(
            new ClientHashProcessor(dbAdapter, strategies, null), "resolveConflicts", clientData, serverData);

        TableSyncStrategy tableSyncStrategy = ctx.getStrategies().getSyncStrategyForTable(TEST_TABLE_NAME);

        assertTrue(
            tableSyncStrategy.getConflictStrategy() == ConflictStrategy.SERVER_WINS);
        assertTrue(
            tableSyncStrategy.getDirection() == SyncDirection.SERVER_TO_CLIENT);
        assertTrue(dataHolder.getClientSyncData().getChanges().size() == 0);

        // model.run(1) should be executed exactly one time.
        verify(dbAdapter, times(1)).updateMdRow(entry.getRevision(), 0, entry.getPrimaryKey(), hashValue,
            entry.getTableName());

    }

    @Test
    public void resolveConflict() throws Exception {

        List<Change> changeList = newArrayList();
        MDEntry entry = new MDEntry(1, true, 1, TEST_TABLE_NAME, TEST_MDV);
        Map<String, Object> rowServerData = newHashMap();
        rowServerData.put(TEST_COLUMN1, 1);
        rowServerData.put(TEST_COLUMN2, TEST_STRING);
        rowServerData.put(TEST_COLUMN3, true);
        rowServerData.put(TEST_COLUMN4, new Date(System.currentTimeMillis()));
        rowServerData.put(TEST_COLUMN5, 4.5);
        Change remoteChange = new Change(entry, rowServerData);

        SyncData serverData = new SyncData();
        serverData.setRevision(1);
        serverData.addChange(remoteChange);


        MDEntry clientEntry = new MDEntry(1, true, 1, TEST_TABLE_NAME, TEST_MDV);
        Map<String, Object> rowClientData = newHashMap();
        rowClientData.put(TEST_COLUMN1, 1);
        rowClientData.put(TEST_COLUMN2, TEST_STRING + "_updateClient");
        rowClientData.put(TEST_COLUMN3, true);
        rowClientData.put(TEST_COLUMN4, new Date(System.currentTimeMillis()));
        rowClientData.put(TEST_COLUMN5, 4.5);
        Change localChange = new Change(clientEntry, rowClientData);

        SyncData clientData = new SyncData();
        clientData.addChange(localChange);


        when(localDataResultSet.next()).thenReturn(false);

        when(localHashResultSet.getInt(REV_COLUMN_NAME)).thenReturn(7);
        when(localHashResultSet.getInt(FLAG_COLUMN_NAME)).thenReturn(1); // Client change
        HashCalculator clc = new HashCalculator();
        String hashValue = clc.calculateHash(remoteChange, false);
        when(localHashResultSet.getString(MDV_COLUMN_NAME)).thenReturn(hashValue);

        TableSyncStrategy strategy = new TableSyncStrategy(SyncDirection.BIDIRECTIONAL,
            ConflictStrategy.CLIENT_WINS);

        TableSyncStrategies strategies = new TableSyncStrategies();
        strategies.addSyncStrategyForTable(TEST_TABLE_NAME, strategy);

        final SyncContext.LocalContext ctx = SyncContext.local(strategies);

        Whitebox.<Void>invokeMethod(new ClientHashProcessor(dbAdapter, strategies, null),
            "resolveConflicts", clientData, serverData);

        TableSyncStrategy tableSyncStrategy = ctx.getStrategies().getSyncStrategyForTable(TEST_TABLE_NAME);

        assertTrue(
            tableSyncStrategy.getConflictStrategy() == ConflictStrategy.CLIENT_WINS);
        assertTrue(
            tableSyncStrategy.getDirection() == SyncDirection.BIDIRECTIONAL);
        assertTrue(clientData.getChanges().size() == 1);

        // model.run(1) should be executed exactly one time.
        verify(dbAdapter, times(1)).updateMdRow(entry.getRevision(), 1, entry.getPrimaryKey(), TEST_MDV,
            entry.getTableName());
    }

    @Test
    public void resolveConflictsWithNullableSyncStrategy() throws Exception {

        MDEntry entry = new MDEntry(1, true, 1, TEST_TABLE_NAME, TEST_MDV);
        Map<String, Object> rowData = newHashMap();
        rowData.put(TEST_COLUMN1, 1);
        rowData.put(TEST_COLUMN2, TEST_STRING);
        rowData.put(TEST_COLUMN3, true);
        rowData.put(TEST_COLUMN4, new Date(System.currentTimeMillis()));
        rowData.put(TEST_COLUMN5, 4.5);
        Change remoteChange = new Change(entry, rowData);

        SyncData serverData = new SyncData();
        serverData.setRevision(1);
        serverData.addChange(remoteChange);

        MDEntry clientEntry = new MDEntry(1, true, 1, TEST_TABLE_NAME, TEST_MDV);
        Map<String, Object> rowClientData = newHashMap();
        rowClientData.put(TEST_COLUMN1, 1);
        rowClientData.put(TEST_COLUMN2, TEST_STRING + "_updateClient");
        rowClientData.put(TEST_COLUMN3, true);
        rowClientData.put(TEST_COLUMN4, new Date(System.currentTimeMillis()));
        rowClientData.put(TEST_COLUMN5, 4.5);
        Change localChange = new Change(clientEntry, rowClientData);

        SyncData clientData = new SyncData();
        clientData.addChange(localChange);

        HashCalculator clc = new HashCalculator();
        String hashValue = clc.calculateHash(remoteChange, false);

        when(dbAdapter.getHashCalculator()).thenReturn(clc);

        when(localDataResultSet.next()).thenReturn(false);

        when(localHashResultSet.getInt(REV_COLUMN_NAME)).thenReturn(7);
        when(localHashResultSet.getInt(FLAG_COLUMN_NAME)).thenReturn(1); // Client change

        when(localHashResultSet.getString(MDV_COLUMN_NAME)).thenReturn(hashValue);

        SyncContext.LocalContext ctx = SyncContext.local(new TableSyncStrategies());

        SyncDataHolder dataHolder = Whitebox.<SyncDataHolder>invokeMethod(
            new ClientHashProcessor(dbAdapter, new TableSyncStrategies(), null),
            "resolveConflicts", clientData, serverData);

        TableSyncStrategy tableSyncStrategy = ctx.getStrategies().getSyncStrategyForTable(TEST_TABLE_NAME);

        assertTrue(tableSyncStrategy.getConflictStrategy() == ConflictStrategy.SERVER_WINS);
        assertTrue(tableSyncStrategy.getDirection() == SyncDirection.BIDIRECTIONAL);
        assertTrue(dataHolder.getClientSyncData().getChanges().size() == 0);
    }

    /**
     * Tests for nullable arguments
     */
    @Test(expected = NullPointerException.class)
    public void validateStateNullableSyncDirection() throws ContextException, SyncException {
        TableSyncStrategy syncStrategy = new TableSyncStrategy(null, ConflictStrategy.SERVER_WINS);
        TableSyncStrategies strategies = new TableSyncStrategies();
        strategies.addSyncStrategyForTable("categories", syncStrategy);
        SyncContext.local(strategies);
    }

    @Test(expected = NullPointerException.class)
    public void validateStateNullableConflictStrategy() throws ContextException, SyncException {
        TableSyncStrategy syncStrategy = new TableSyncStrategy(SyncDirection.CLIENT_TO_SERVER, null);
        TableSyncStrategies strategies = new TableSyncStrategies();
        strategies.addSyncStrategyForTable("categories", syncStrategy);
        SyncContext.local(strategies);
    }

    /**
     * Tests for the following combination of sync directions and conflict strategies:
     * <p/>
     * client->server => server.wins
     * client->server => client.wins
     * client->server => fire.event
     * server->client => server.wins
     * server->client => client.wins
     * server->client => fire.event
     */
    @Test(expected = IllegalStateException.class)
    public void validateStateClientToServerAndServerWins() throws ContextException, SyncException {
        TableSyncStrategy syncStrategy = new TableSyncStrategy(SyncDirection.CLIENT_TO_SERVER,
            ConflictStrategy.SERVER_WINS);
        TableSyncStrategies strategies = new TableSyncStrategies();
        strategies.addSyncStrategyForTable("categories", syncStrategy);
        SyncContext.local(strategies);
    }

    @Test(expected = IllegalStateException.class)
    public void validateStateClientToServerAndFireEvent() throws ContextException, SyncException {
        TableSyncStrategy syncStrategy = new TableSyncStrategy(SyncDirection.CLIENT_TO_SERVER,
            ConflictStrategy.FIRE_EVENT);
        TableSyncStrategies strategies = new TableSyncStrategies();
        strategies.addSyncStrategyForTable("categories", syncStrategy);
        SyncContext.local(strategies);
    }

    @Test(expected = IllegalStateException.class)
    public void validateStateServerToClientAndClientWins() throws ContextException, SyncException {
        TableSyncStrategy syncStrategy = new TableSyncStrategy(SyncDirection.SERVER_TO_CLIENT,
            ConflictStrategy.CLIENT_WINS);
        TableSyncStrategies strategies = new TableSyncStrategies();
        strategies.addSyncStrategyForTable("categories", syncStrategy);
        SyncContext.local(strategies);

    }

    @Test(expected = IllegalStateException.class)
    public void validateStateServerToClientAndFireEvent() throws ContextException, SyncException {
        TableSyncStrategy syncStrategy = new TableSyncStrategy(SyncDirection.SERVER_TO_CLIENT,
            ConflictStrategy.FIRE_EVENT);
        TableSyncStrategies strategies = new TableSyncStrategies();
        strategies.addSyncStrategyForTable("categories", syncStrategy);
        SyncContext.local(strategies);
    }
}
