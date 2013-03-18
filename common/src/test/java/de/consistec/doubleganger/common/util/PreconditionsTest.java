package de.consistec.doubleganger.common.util;

/*
 * #%L
 * Project - doppelganger
 * File - PreconditionsTest.java
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
import static de.consistec.doubleganger.common.util.CollectionsUtil.newArrayList;
import static de.consistec.doubleganger.common.util.CollectionsUtil.newHashMap;

import de.consistec.doubleganger.common.TableSyncStrategies;
import de.consistec.doubleganger.common.TableSyncStrategy;
import de.consistec.doubleganger.common.data.Change;
import de.consistec.doubleganger.common.data.MDEntry;
import de.consistec.doubleganger.common.exception.SyncException;

import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 16.01.13 10:05
 */
public class PreconditionsTest {

    @Test(expected = SyncException.class)
    public void checkSyncDirectionOfServerChangesFail() throws SyncException {
        List<Change> serverChanges = newArrayList();

        MDEntry entry = new MDEntry(Integer.valueOf(1), true, 1, "categories", "7679869786hg76786hjhgf");
        Map<String, Object> rowData = newHashMap();
        rowData.put("categoryid", Integer.valueOf(1));
        rowData.put("description", "testdescription");
        rowData.put("categoryname", "testname");

        Change serverChange = new Change();
        serverChange.setMdEntry(entry);
        serverChange.setRowData(rowData);
        serverChanges.add(serverChange);


        TableSyncStrategies clientSyncStrategies = new TableSyncStrategies();
        clientSyncStrategies.addSyncStrategyForTable("categories",
            new TableSyncStrategy(CLIENT_TO_SERVER, CLIENT_WINS));

        Preconditions.checkSyncDirectionOfServerChanges(serverChanges, clientSyncStrategies);
    }

    @Test(expected = SyncException.class)
    public void checkSyncDirectionOfClientChangesFail() throws SyncException {
        List<Change> clientChanges = newArrayList();

        MDEntry entry = new MDEntry(Integer.valueOf(1), true, 1, "categories", "7679869786hg76786hjhgf");
        Map<String, Object> rowData = newHashMap();
        rowData.put("categoryid", Integer.valueOf(1));
        rowData.put("description", "testdescription");
        rowData.put("categoryname", "testname");

        Change clientChange = new Change();
        clientChange.setMdEntry(entry);
        clientChange.setRowData(rowData);
        clientChanges.add(clientChange);


        TableSyncStrategies serverSyncStrategies = new TableSyncStrategies();
        serverSyncStrategies.addSyncStrategyForTable("categories",
            new TableSyncStrategy(SERVER_TO_CLIENT, SERVER_WINS));

        Preconditions.checkSyncDirectionOfClientChanges(clientChanges, serverSyncStrategies);
    }

    @Test
    public void checkSyncDirectionOfServerChanges() throws SyncException {
        List<Change> serverChanges = newArrayList();

        MDEntry entry = new MDEntry(Integer.valueOf(1), true, 1, "categories", "7679869786hg76786hjhgf");
        Map<String, Object> rowData = newHashMap();
        rowData.put("categoryid", Integer.valueOf(1));
        rowData.put("description", "testdescription");
        rowData.put("categoryname", "testname");

        Change serverChange = new Change();
        serverChange.setMdEntry(entry);
        serverChange.setRowData(rowData);
        serverChanges.add(serverChange);


        TableSyncStrategies clientSyncStrategies = new TableSyncStrategies();
        clientSyncStrategies.addSyncStrategyForTable("categories",
            new TableSyncStrategy(BIDIRECTIONAL, SERVER_WINS));

        Preconditions.checkSyncDirectionOfServerChanges(serverChanges, clientSyncStrategies);
    }

    @Test
    public void checkSyncDirectionOfClientChanges() throws SyncException {
        List<Change> clientChanges = newArrayList();

        MDEntry entry = new MDEntry(Integer.valueOf(1), true, 1, "categories", "7679869786hg76786hjhgf");
        Map<String, Object> rowData = newHashMap();
        rowData.put("categoryid", Integer.valueOf(1));
        rowData.put("description", "testdescription");
        rowData.put("categoryname", "testname");

        Change clientChange = new Change();
        clientChange.setMdEntry(entry);
        clientChange.setRowData(rowData);
        clientChanges.add(clientChange);


        TableSyncStrategies serverSyncStrategies = new TableSyncStrategies();
        serverSyncStrategies.addSyncStrategyForTable("categories",
            new TableSyncStrategy(BIDIRECTIONAL, SERVER_WINS));

        Preconditions.checkSyncDirectionOfClientChanges(clientChanges, serverSyncStrategies);
    }
}
