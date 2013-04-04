package de.consistec.doubleganger.common.conflict;

/*
 * #%L
 * Project - doubleganger
 * File - ServerToClientConflictStrategyTest.java
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
import static de.consistec.doubleganger.common.util.CollectionsUtil.newHashMap;
import static org.junit.Assert.assertTrue;

import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.IConflictListener;
import de.consistec.doubleganger.common.SyncDirection;
import de.consistec.doubleganger.common.adapter.IDatabaseAdapter;
import de.consistec.doubleganger.common.client.ConflictHandlingData;
import de.consistec.doubleganger.common.data.Change;
import de.consistec.doubleganger.common.data.MDEntry;
import de.consistec.doubleganger.common.data.ResolvedChange;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterInstantiationException;

import java.security.NoSuchAlgorithmException;

import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 13.12.12 16:52
 */
public class ServerToClientConflictStrategyTest {

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

    public ServerToClientConflictStrategyTest() throws DatabaseAdapterInstantiationException {
        conflictStrategy = ConflictStrategyFactory.newInstance(SyncDirection.SERVER_TO_CLIENT);
    }

    @Before
    public void before() {
        // related to the above defined mock annotations
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createConflictStrategy() {
        assertTrue(conflictStrategy instanceof ServerToClientConflictStrategy);
    }

    @Test(expected = IllegalStateException.class)
    public void resolveByServerWinsStrategy() throws DatabaseAdapterException, NoSuchAlgorithmException {

        Config confInstance = Config.getInstance();
        confInstance.setGlobalConflictStrategy(ConflictStrategy.CLIENT_WINS);

        MDEntry remoteEntry = new MDEntry(Integer.valueOf(1), false, 1, TEST_TABLE_NAME, TEST_MDV);
        Change remoteChange = new Change(remoteEntry, createRowData());

        ConflictHandlingData conflictHandlingData = new ConflictHandlingData(remoteChange);
        conflictStrategy.resolveByClientWinsStrategy(databaseAdapterMock, conflictHandlingData);
    }

    @Test(expected = IllegalStateException.class)
    public void resolveByFireEventStrategy() throws DatabaseAdapterException, NoSuchAlgorithmException, SyncException {

        Config confInstance = Config.getInstance();
        confInstance.setGlobalConflictStrategy(ConflictStrategy.FIRE_EVENT);

        MDEntry remoteEntry = new MDEntry(Integer.valueOf(1), false, 1, TEST_TABLE_NAME, TEST_MDV);
        Change remoteChange = new Change(remoteEntry, createRowData());

        ConflictHandlingData conflictHandlingData = new ConflictHandlingData(remoteChange);
        Map<String, Object> clientData = newHashMap();
        conflictStrategy.resolveByFireEvent(databaseAdapterMock, conflictHandlingData, clientData,
            new IConflictListener() {
                @Override
                public ResolvedChange resolve(final Map<String, Object> serverData,
                    final Map<String, Object> clientData) {
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
