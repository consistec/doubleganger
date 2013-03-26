package de.consistec.doubleganger.android.test;

/*
 * #%L
 * doppelganger
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

import de.consistec.doubleganger.common.SyncData;
import de.consistec.doubleganger.common.data.Change;
import de.consistec.doubleganger.common.data.MDEntry;
import de.consistec.doubleganger.common.exception.SerializationException;
import de.consistec.doubleganger.impl.adapter.JSONSerializationAdapter;

import android.test.InstrumentationTestCase;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: markus
 * Date: 12.07.12
 * Time: 10:23
 * To change this template use File | Settings | File Templates.
 */
public class AndroidSerializationTest extends InstrumentationTestCase {

    private static final String TEST_STRING = "testString";
    private static final String TEST_TABLE_NAME = "testTableName";
    private static final String TEST_COLUMN1 = "column1";
    private static final String TEST_COLUMN2 = "column2";
    private static final String TEST_COLUMN3 = "column3";
    private static final String TEST_COLUMN4 = "column4";
    private static final String TEST_COLUMN5 = "column5";


    public void testSerialization() throws SerializationException {
        SyncData changeList = new SyncData();
        MDEntry entry = new MDEntry(1, true, 1, TEST_TABLE_NAME, "");
        Map<String, Object> rowData = newHashMap();
        rowData.put(TEST_COLUMN1, 1);
        rowData.put(TEST_COLUMN2, TEST_STRING);
        rowData.put(TEST_COLUMN3, true);
        rowData.put(TEST_COLUMN4, new Date(System.currentTimeMillis()));
        rowData.put(TEST_COLUMN5, 4.5);
        changeList.addChange(new Change(entry, rowData));

        entry = new MDEntry(2, false, 2, TEST_TABLE_NAME, null);
        rowData = newHashMap();
        rowData.put(TEST_COLUMN1, 2);
        rowData.put(TEST_COLUMN2, TEST_STRING);
        rowData.put(TEST_COLUMN3, "false");
        rowData.put(TEST_COLUMN4, new Date(System.currentTimeMillis()));
        rowData.put(TEST_COLUMN5, 3.14);
        changeList.addChange(new Change(entry, rowData));

        JSONSerializationAdapter adapter = new JSONSerializationAdapter();
        String jsonChangeList = adapter.serializeChangeList(changeList.getChanges());
        SyncData deserializedChangeList = adapter.deserializeChangeList(jsonChangeList);

        assertEquals(changeList, deserializedChangeList);
    }
}
