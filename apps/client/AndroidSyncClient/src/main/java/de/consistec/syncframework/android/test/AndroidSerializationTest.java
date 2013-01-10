package de.consistec.syncframework.android.test;

import static de.consistec.syncframework.common.util.CollectionsUtil.newArrayList;
import static de.consistec.syncframework.common.util.CollectionsUtil.newHashMap;

import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.MDEntry;
import de.consistec.syncframework.common.exception.SerializationException;
import de.consistec.syncframework.impl.adapter.JSONSerializationAdapter;

import android.test.InstrumentationTestCase;
import java.util.Date;
import java.util.List;
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
        List<Change> changeList = newArrayList();
        MDEntry entry = new MDEntry(1, true, 1, TEST_TABLE_NAME);
        Map<String, Object> rowData = newHashMap();
        rowData.put(TEST_COLUMN1, 1);
        rowData.put(TEST_COLUMN2, TEST_STRING);
        rowData.put(TEST_COLUMN3, true);
        rowData.put(TEST_COLUMN4, new Date(System.currentTimeMillis()));
        rowData.put(TEST_COLUMN5, 4.5);
        changeList.add(new Change(entry, rowData));

        entry = new MDEntry(2, false, 2, TEST_TABLE_NAME);
        rowData = newHashMap();
        rowData.put(TEST_COLUMN1, 2);
        rowData.put(TEST_COLUMN2, TEST_STRING);
        rowData.put(TEST_COLUMN3, "false");
        rowData.put(TEST_COLUMN4, new Date(System.currentTimeMillis()));
        rowData.put(TEST_COLUMN5, 3.14);
        changeList.add(new Change(entry, rowData));

        JSONSerializationAdapter adapter = new JSONSerializationAdapter();
        String jsonChangeList = adapter.serializeChangeList(changeList);
        List<Change> deserializedChangeList = adapter.deserializeChangeList(jsonChangeList);

        assertEquals(changeList, deserializedChangeList);
    }
}
