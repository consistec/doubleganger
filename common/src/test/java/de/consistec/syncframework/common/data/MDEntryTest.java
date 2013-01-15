package de.consistec.syncframework.common.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import de.consistec.syncframework.common.TestBase;

import org.junit.Test;

/**
 * Tests of MdEntry object.
 *
 * @author Markus Backes
 * @company Consistec Engineering and Consulting GmbH
 * @date 13.07.12 08:48
 * @since 0.0.1-SNAPSHOT
 */
public class MDEntryTest extends TestBase {

    private static final String TEST_TABLE_NAME = "TestTable";
    private static final String TEST_TABLE_NAME2 = "TestTable2";
    private static final String TEST_MDV = "6767e648767786786dsffdsa786dfsaf";

    @Test
    public void testConstruction() {
        MDEntry entry = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);

        assertEquals(TEST_TABLE_NAME, entry.getTableName());
        assertEquals(1, entry.getPrimaryKey());
        assertEquals(true, entry.isExists());
        assertEquals(0, entry.getRevision());
    }

    @Test
    public void testSetterAndGetter() {
        MDEntry entry = new MDEntry();
        entry.setAdded();
        assertEquals(true, entry.isExists());
        entry.setDeleted();
        assertEquals(false, entry.isExists());
        entry.setExists(true);
        assertEquals(true, entry.isExists());
        entry.setModified();
        assertEquals(true, entry.isExists());
        entry.setPrimaryKey(1);
        assertEquals(1, entry.getPrimaryKey());
        entry.setRevision(1);
        assertEquals(1, entry.getRevision());
        entry.setTableName(TEST_TABLE_NAME);
        assertEquals(TEST_TABLE_NAME, entry.getTableName());
    }

    @Test
    public void testObjectEquality() {
        MDEntry entryOne = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);
        MDEntry entryTwo = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);

        assertTrue(entryOne.equals(entryTwo));
        assertTrue(entryOne.equals(entryOne));
    }

    @Test
    public void testObjectUnequality() {
        MDEntry entryOne = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);
        MDEntry entryTwo = new MDEntry(2, true, 0, TEST_TABLE_NAME, TEST_MDV);
        MDEntry entryThree = new MDEntry(1, false, 0, TEST_TABLE_NAME, TEST_MDV);
        MDEntry entryFour = new MDEntry(1, true, 1, TEST_TABLE_NAME, TEST_MDV);
        MDEntry entryFive = new MDEntry(1, true, 0, TEST_TABLE_NAME2, TEST_MDV);

        assertFalse(entryOne.equals(entryTwo));
        assertFalse(entryOne.equals(entryThree));
        assertFalse(entryOne.equals(entryFour));
        assertFalse(entryOne.equals(entryFive));
        assertFalse(entryOne == null);
    }

    @Test
    public void testHashCodeEquality() {
        MDEntry entryOne = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);
        MDEntry entryTwo = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);

        assertEquals(entryOne.hashCode(), entryTwo.hashCode());
    }

    @Test
    public void testHashCodeUnequality() {
        MDEntry entryOne = new MDEntry(1, true, 0, TEST_TABLE_NAME, TEST_MDV);
        MDEntry entryTwo = new MDEntry(2, true, 0, TEST_TABLE_NAME, TEST_MDV);
        MDEntry entryThree = new MDEntry(1, false, 0, TEST_TABLE_NAME, TEST_MDV);
        MDEntry entryFour = new MDEntry(1, true, 1, TEST_TABLE_NAME, TEST_MDV);
        MDEntry entryFive = new MDEntry(1, true, 0, TEST_TABLE_NAME2, TEST_MDV);

        assertNotSame(entryOne.hashCode(), entryTwo.hashCode());
        assertNotSame(entryOne.hashCode(), entryThree.hashCode());
        assertNotSame(entryOne.hashCode(), entryFour.hashCode());
        assertNotSame(entryOne.hashCode(), entryFive.hashCode());
    }
}
