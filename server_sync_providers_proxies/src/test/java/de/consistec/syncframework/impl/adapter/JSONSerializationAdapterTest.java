package de.consistec.syncframework.impl.adapter;

/*
 * #%L
 * Project - doppelganger
 * File - JSONSerializationAdapterTest.java
 * %%
 * Copyright (C) 2011 - 2012 Consistec GmbH
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

import static de.consistec.syncframework.common.util.CollectionsUtil.newArrayList;
import static de.consistec.syncframework.common.util.CollectionsUtil.newHashMap;
import static org.junit.Assert.assertEquals;

import de.consistec.syncframework.common.TestBase;
import de.consistec.syncframework.common.TestUtil;
import de.consistec.syncframework.common.Tuple;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.MDEntry;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.exception.SerializationException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 * <ul> <li><b>Company:</b> consistec Engineering and Consulting GmbH</li>
 * <li><b>Date:</b> 12.07.12 09:03</li></ul>
 *
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class JSONSerializationAdapterTest extends TestBase {

    private static final String TABLENAME1 = "table1";
    private static final String TABLENAME2 = "table2";

    private static final String COLUMNNAME1 = "column1";
    private static final String COLUMNNAME2 = "column2";
    private static final String COLUMNNAME3 = "column3";
    private static final String COLUMNNAME4 = "column4";
    private static final String COLUMNNAME5 = "column4";
    private static final String COLUMNNAME6 = "column4";

    private static final String TEST_STRING = "foobar";
    private static final String TEST_MDV = "7686876786sd9876786876";

    @Test
    public void testChangeListSerialization() throws SerializationException {

        final List<Change> changeList = newArrayList();
        MDEntry entry = new MDEntry(1, true, 1, TABLENAME1, TEST_MDV);
        Map<String, Object> rowData = newHashMap();
        rowData.put(COLUMNNAME1, 1);
        rowData.put(COLUMNNAME2, TEST_STRING);
        rowData.put(COLUMNNAME3, true);
        rowData.put(COLUMNNAME4, new Date(System.currentTimeMillis()));
        rowData.put(COLUMNNAME5, 4.5);
        rowData.put(COLUMNNAME6, null);
        changeList.add(new Change(entry, rowData));

        entry = new MDEntry(2, false, 2, TABLENAME2, TEST_MDV);
        rowData = new HashMap<String, Object>();
        rowData.put(COLUMNNAME1, 2);
        rowData.put(COLUMNNAME2, TEST_STRING);
        rowData.put(COLUMNNAME3, "false");
        rowData.put(COLUMNNAME4, new Date(System.currentTimeMillis()));
        rowData.put(COLUMNNAME5, 3.14);
        rowData.put(COLUMNNAME6, null);
        changeList.add(new Change(entry, rowData));

        final JSONSerializationAdapter adapter = new JSONSerializationAdapter();
        final String jsonChangeList = adapter.serializeChangeList(changeList);
        final List<Change> deserializedChangeList = adapter.deserializeChangeList(jsonChangeList);

        assertEquals("Original and deserialised change lists are different", changeList, deserializedChangeList);
    }

    @Test
    public void testTupleSerialization() throws SerializationException {

        final List<Change> changeList = newArrayList();
        MDEntry entry = new MDEntry(1, true, 1, TABLENAME1, TEST_MDV);
        Map<String, Object> rowData = newHashMap();
        rowData.put(COLUMNNAME1, 1);
        rowData.put(COLUMNNAME2, TEST_STRING);
        rowData.put(COLUMNNAME3, true);
        rowData.put(COLUMNNAME4, new Date(System.currentTimeMillis()));
        rowData.put(COLUMNNAME5, 4.5);
        rowData.put(COLUMNNAME6, null);
        changeList.add(new Change(entry, rowData));

        entry = new MDEntry(2, false, 2, TABLENAME2, TEST_MDV);
        rowData = new HashMap<String, Object>();
        rowData.put(COLUMNNAME1, 2);
        rowData.put(COLUMNNAME2, TEST_STRING);
        rowData.put(COLUMNNAME3, "false");
        rowData.put(COLUMNNAME4, new Date(System.currentTimeMillis()));
        rowData.put(COLUMNNAME5, 3.14);
        rowData.put(COLUMNNAME6, null);
        changeList.add(new Change(entry, rowData));

        Tuple<Integer, List<Change>> tuple = new Tuple<Integer, List<Change>>(0, changeList);

        final JSONSerializationAdapter adapter = new JSONSerializationAdapter();
//        final String jsonChangeList = adapter.serializeChangeList(changeList);
        final String jsonChangeList = adapter.serializeChangeList(tuple);
        final Tuple<Integer, List<Change>> deserializedTuple = adapter.deserializeMaxRevisionAndChangeList(
            jsonChangeList);

        assertEquals("max revision of serialized and deserialized tuple are different!", tuple.getValue1(),
            deserializedTuple.getValue1());
        assertEquals("Original and deserialised tuples are different!", tuple.getValue2(),
            deserializedTuple.getValue2());
    }

    @Test
    public void testTupleSerializationEmptyChangeList() throws SerializationException {

        final List<Change> changeList = newArrayList();

        Tuple<Integer, List<Change>> tuple = new Tuple<Integer, List<Change>>(0, changeList);

        final JSONSerializationAdapter adapter = new JSONSerializationAdapter();
//        final String jsonChangeList = adapter.serializeChangeList(changeList);
        final String jsonChangeList = adapter.serializeChangeList(tuple);
        final Tuple<Integer, List<Change>> deserializedTuple = adapter.deserializeMaxRevisionAndChangeList(
            jsonChangeList);

        assertEquals("max revision of serialized and deserialized tuple are different!", tuple.getValue1(),
            deserializedTuple.getValue1());
        assertEquals("Original and deserialised tuples are different!", tuple.getValue2(),
            deserializedTuple.getValue2());
    }


    @Test
    public void testSchemaSerialization() throws SerializationException {

        final Schema schema = TestUtil.getSchema();
        final JSONSerializationAdapter adapter = new JSONSerializationAdapter();
        final String jsonSchema = adapter.serializeSchema(schema);
        final Schema deserializedSchema = adapter.deserializeSchema(jsonSchema);
        assertEquals("Original and deserialised schemas are different", schema, deserializedSchema);

    }
}
