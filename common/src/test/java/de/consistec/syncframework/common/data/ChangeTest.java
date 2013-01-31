package de.consistec.syncframework.common.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import de.consistec.syncframework.common.TestBase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * Tests of Change object.
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date 13.07.12 10:51
 * @since 0.0.1-SNAPSHOT
 */
public class ChangeTest extends TestBase {

    private static final String TEST_TABLE_NAME = "TestTable";
    private static final String TEST_COLUMN1 = "TestColumn1";
    private static final String TEST_COLUMN2 = "TestColumn2";
    private static final String TEST_MDV = "6767e648767786786dsffdsa786dfsaf";

    @Test
    public void testObjectEquality() {
        long currentTimeMillis = System.currentTimeMillis();
        MDEntry entryOne = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);
        MDEntry entryTwo = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);

        Map<String, Object> rowDataOne = new HashMap<String, Object>();
        rowDataOne.put(TEST_COLUMN1, 1);
        rowDataOne.put(TEST_COLUMN2, new Date(currentTimeMillis));

        Map<String, Object> rowDataTwo = new HashMap<String, Object>();
        rowDataTwo.put(TEST_COLUMN1, 1);
        rowDataTwo.put(TEST_COLUMN2, new Date(currentTimeMillis));

        Change changeOne = new Change(entryOne, rowDataOne);
        Change changeTwo = new Change(entryTwo, rowDataTwo);

        assertTrue(changeOne.equals(changeTwo));
        assertTrue(changeOne.equals(changeOne));
    }

    @Test
    public void testObjectUnequality() {
        long currentTimeMillis = System.currentTimeMillis();
        MDEntry entryOne = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);
        MDEntry entryTwo = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);

        Map<String, Object> rowDataOne = new HashMap<String, Object>();
        rowDataOne.put(TEST_COLUMN1, 1);
        rowDataOne.put(TEST_COLUMN2, new Date(currentTimeMillis));

        Map<String, Object> rowDataTwo = new HashMap<String, Object>();
        rowDataTwo.put(TEST_COLUMN1, 2);
        rowDataTwo.put(TEST_COLUMN2, new Date(currentTimeMillis));

        Change changeOne = new Change(entryOne, rowDataOne);
        Change changeTwo = new Change(entryTwo, rowDataTwo);

        assertFalse(changeOne.equals(changeTwo));
        assertFalse(changeOne == null);
    }

    @Test
    public void testHashCodeEquality() {
        long currentTimeMillis = System.currentTimeMillis();
        MDEntry entryOne = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);
        MDEntry entryTwo = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);

        Map<String, Object> rowDataOne = new HashMap<String, Object>();
        rowDataOne.put(TEST_COLUMN1, 1);
        rowDataOne.put(TEST_COLUMN2, new Date(currentTimeMillis));

        Map<String, Object> rowDataTwo = new HashMap<String, Object>();
        rowDataTwo.put(TEST_COLUMN1, 1);
        rowDataTwo.put(TEST_COLUMN2, new Date(currentTimeMillis));

        Change changeOne = new Change(entryOne, rowDataOne);
        Change changeTwo = new Change(entryTwo, rowDataTwo);

        assertEquals(changeOne.hashCode(), changeTwo.hashCode());
    }

    @Test
    public void testHashCodeUnequality() {
        long currentTimeMillis = System.currentTimeMillis();
        MDEntry entryOne = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);
        MDEntry entryTwo = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);

        Map<String, Object> rowDataOne = new HashMap<String, Object>();
        rowDataOne.put(TEST_COLUMN1, 1);
        rowDataOne.put(TEST_COLUMN2, new Date(currentTimeMillis));

        Map<String, Object> rowDataTwo = new HashMap<String, Object>();
        rowDataTwo.put(TEST_COLUMN1, 2);
        rowDataTwo.put(TEST_COLUMN2, new Date(currentTimeMillis));

        Change changeOne = new Change(entryOne, rowDataOne);
        Change changeTwo = new Change(entryTwo, rowDataTwo);

        assertNotSame(changeOne.hashCode(), changeTwo.hashCode());
    }
}
