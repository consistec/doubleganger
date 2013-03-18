package de.consistec.doubleganger.common.conflict;

/*
 * #%L
 * Project - doppelganger
 * File - ConflictTypeTest.java
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

import static de.consistec.doubleganger.common.conflict.ConflictType.CLIENT_ADD_SERVER_ADD_OR_SERVER_MOD;
import static de.consistec.doubleganger.common.conflict.ConflictType.CLIENT_ADD_SERVER_DEL;
import static de.consistec.doubleganger.common.conflict.ConflictType.CLIENT_DEL_SERVER_ADD_OR_SERVER_MOD;
import static de.consistec.doubleganger.common.conflict.ConflictType.CLIENT_DEL_SERVER_DEL;
import static de.consistec.doubleganger.common.conflict.ConflictType.CLIENT_MOD_SERVER_ADD_OR_SERVER_MOD;
import static de.consistec.doubleganger.common.conflict.ConflictType.CLIENT_MOD_SERVER_DEL;
import static de.consistec.doubleganger.common.util.CollectionsUtil.newHashMap;
import static org.junit.Assert.assertTrue;

import de.consistec.doubleganger.common.TestBase;
import de.consistec.doubleganger.common.client.ConflictHandlingData;
import de.consistec.doubleganger.common.data.Change;
import de.consistec.doubleganger.common.data.MDEntry;

import java.util.Date;
import java.util.Map;
import org.junit.Test;

/**
 * Tests of {@link de.consistec.doubleganger.common.conflict.ConflictType.Resolver Resolver} instance of
 * {@link de.consistec.doubleganger.common.conflict.ConflictType ConflictType} enumeration.
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date 20.07.12 15:09
 * @since 0.0.1-SNAPSHOT
 */
public class ConflictTypeTest extends TestBase {

    private static final String TEST_STRING = "testString";
    private static final String TEST_TABLE_NAME = "testTablename";
    private static final String TEST_COLUMN1 = "column1";
    private static final String TEST_COLUMN2 = "column2";
    private static final String TEST_COLUMN3 = "column3";
    private static final String TEST_COLUMN4 = "column4";
    private static final String TEST_COLUMN5 = "column5";
    private static final String TEST_MDV = "6767e648767786786dsffdsa786dfsaf";

    private enum CHANGE_CREATION_TYPE {
        ADD, MOD, DEL;
    }

    private Change createChange(CHANGE_CREATION_TYPE creationType, boolean isClient, int revision) {

        MDEntry entry = null;
        Map<String, Object> rowData = newHashMap();
        String testString = !isClient ? TEST_STRING : TEST_STRING + "_updateClient";

        switch (creationType) {
            case ADD:
                entry = new MDEntry(1, true, revision, TEST_TABLE_NAME, TEST_MDV);
                rowData.put(TEST_COLUMN1, 1);
                rowData.put(TEST_COLUMN2, testString);
                rowData.put(TEST_COLUMN3, true);
                rowData.put(TEST_COLUMN4, new Date(System.currentTimeMillis()));
                rowData.put(TEST_COLUMN5, 4.5);
                break;
            case MOD:
                entry = new MDEntry(1, true, revision, TEST_TABLE_NAME, TEST_MDV);
                rowData.put(TEST_COLUMN1, 1);
                rowData.put(TEST_COLUMN2, testString);
                rowData.put(TEST_COLUMN3, true);
                rowData.put(TEST_COLUMN4, new Date(System.currentTimeMillis()));
                rowData.put(TEST_COLUMN5, 4.5);
                break;
            case DEL:
                entry = new MDEntry(1, false, 1, TEST_TABLE_NAME, null);
                // no rowData, they were deleted
                break;
            default:
                entry = new MDEntry(1, true, revision, TEST_TABLE_NAME, TEST_MDV);
                rowData.put(TEST_COLUMN1, 1);
                rowData.put(TEST_COLUMN2, testString);
                rowData.put(TEST_COLUMN3, true);
                rowData.put(TEST_COLUMN4, new Date(System.currentTimeMillis()));
                rowData.put(TEST_COLUMN5, 4.5);
                break;
        }

        Change change = new Change(entry, rowData);
        return change;
    }

    @Test
    public void testIsDelAddConflict() {
        Change clientChange = createChange(CHANGE_CREATION_TYPE.DEL, true, 1);
        Change serverChange = createChange(CHANGE_CREATION_TYPE.ADD, false, 0);

        ConflictHandlingData data = new ConflictHandlingData(clientChange, serverChange);
        assertTrue(CLIENT_DEL_SERVER_ADD_OR_SERVER_MOD.isTheCase(data));
    }

    @Test
    public void testIsDelModConflict() {
        Change clientChange = createChange(CHANGE_CREATION_TYPE.DEL, true, 1);
        Change serverChange = createChange(CHANGE_CREATION_TYPE.MOD, false, 1);

        ConflictHandlingData data = new ConflictHandlingData(clientChange, serverChange);

        assertTrue(CLIENT_DEL_SERVER_ADD_OR_SERVER_MOD.isTheCase(data));
    }

    @Test
    public void testIsDelDelConflict() {
        Change clientChange = createChange(CHANGE_CREATION_TYPE.DEL, true, 1);
        Change serverChange = createChange(CHANGE_CREATION_TYPE.DEL, false, 1);

        ConflictHandlingData data = new ConflictHandlingData(clientChange, serverChange);

        assertTrue(CLIENT_DEL_SERVER_DEL.isTheCase(data));
    }

    @Test
    public void testIsAddAddConflict() {
        Change clientChange = createChange(CHANGE_CREATION_TYPE.ADD, true, 0);
        Change serverChange = createChange(CHANGE_CREATION_TYPE.ADD, false, 0);

        ConflictHandlingData data = new ConflictHandlingData(clientChange, serverChange);
        assertTrue(CLIENT_ADD_SERVER_ADD_OR_SERVER_MOD.isTheCase(data));
    }

    @Test
    public void testIsAddModConflict() {
        Change clientChange = createChange(CHANGE_CREATION_TYPE.ADD, true, 0);
        Change serverChange = createChange(CHANGE_CREATION_TYPE.MOD, false, 1);

        ConflictHandlingData data = new ConflictHandlingData(clientChange, serverChange);

        assertTrue(CLIENT_ADD_SERVER_ADD_OR_SERVER_MOD.isTheCase(data));
    }

    @Test
    public void testIsAddDelConflict() {
        Change clientChange = createChange(CHANGE_CREATION_TYPE.ADD, true, 0);
        Change serverChange = createChange(CHANGE_CREATION_TYPE.DEL, false, 1);

        ConflictHandlingData data = new ConflictHandlingData(clientChange, serverChange);

        assertTrue(CLIENT_ADD_SERVER_DEL.isTheCase(data));
    }

    @Test
    public void testIsModAddConflict() {
        Change clientChange = createChange(CHANGE_CREATION_TYPE.MOD, true, 1);
        Change serverChange = createChange(CHANGE_CREATION_TYPE.ADD, false, 0);

        ConflictHandlingData data = new ConflictHandlingData(clientChange, serverChange);
        assertTrue(CLIENT_MOD_SERVER_ADD_OR_SERVER_MOD.isTheCase(data));
    }

    @Test
    public void testIsModModConflict() {
        Change clientChange = createChange(CHANGE_CREATION_TYPE.MOD, true, 1);
        Change serverChange = createChange(CHANGE_CREATION_TYPE.MOD, false, 1);

        ConflictHandlingData data = new ConflictHandlingData(clientChange, serverChange);

        assertTrue(CLIENT_MOD_SERVER_ADD_OR_SERVER_MOD.isTheCase(data));
    }

    @Test
    public void testIsModDelConflict() {
        Change clientChange = createChange(CHANGE_CREATION_TYPE.MOD, true, 1);
        Change serverChange = createChange(CHANGE_CREATION_TYPE.DEL, false, 1);

        ConflictHandlingData data = new ConflictHandlingData(clientChange, serverChange);

        assertTrue(CLIENT_MOD_SERVER_DEL.isTheCase(data));
    }
}
