package de.consistec.doubleganger.common.server;

/*
 * #%L
 * Project - doppelganger
 * File - ServerSyncProviderTest.java
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

import static de.consistec.doubleganger.common.SyncDirection.BIDIRECTIONAL;
import static de.consistec.doubleganger.common.SyncDirection.CLIENT_TO_SERVER;
import static de.consistec.doubleganger.common.SyncDirection.SERVER_TO_CLIENT;
import static de.consistec.doubleganger.common.conflict.ConflictStrategy.CLIENT_WINS;
import static de.consistec.doubleganger.common.conflict.ConflictStrategy.SERVER_WINS;
import static de.consistec.doubleganger.common.util.CollectionsUtil.newHashSet;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.SyncDirection;
import de.consistec.doubleganger.common.SyncSettings;
import de.consistec.doubleganger.common.TableSyncStrategies;
import de.consistec.doubleganger.common.TableSyncStrategy;
import de.consistec.doubleganger.common.adapter.DumbDbAdapter;
import de.consistec.doubleganger.common.adapter.IDatabaseAdapter;
import de.consistec.doubleganger.common.conflict.ConflictStrategy;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterException;

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
 * @company consistec Engineering and Consulting GmbH
 * @date 14.01.13 09:19
 */
public class ServerSyncProviderTest {

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

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        Config config = Config.getInstance();
        config.setServerDatabaseAdapter(DumbDbAdapter.class);
        config.setGlobalConflictStrategy(ConflictStrategy.SERVER_WINS);
        config.setGlobalSyncDirection(SyncDirection.BIDIRECTIONAL);
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
