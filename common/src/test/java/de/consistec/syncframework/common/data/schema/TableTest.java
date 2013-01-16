package de.consistec.syncframework.common.data.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import de.consistec.syncframework.common.TestBase;

import org.junit.Test;

/**
 * Tests of Table object.
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 26.07.12 09:56
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class TableTest extends TestBase {

    private static final String TABLENAME1 = "table1";
    private static final String TABLENAME2 = "table2";

    @Test
    public void testTableEquality() {
        Table table1 = new Table(TABLENAME1);
        Table table2 = new Table(TABLENAME1);

        assertEquals(table1, table2);
        assertEquals(table1, table1);
    }

    @Test
    public void testTableUnequality() {
        Table table1 = new Table(TABLENAME1);
        Table table2 = new Table(TABLENAME2);

        assertNotSame(table1, table2);
        assertNotSame(table1, null);
    }
}
