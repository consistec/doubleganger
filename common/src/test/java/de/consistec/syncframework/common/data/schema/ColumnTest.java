package de.consistec.syncframework.common.data.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import de.consistec.syncframework.common.TestBase;

import java.sql.Types;
import org.junit.Test;

/**
 * Tests of Column object.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 26.07.12 09:57
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class ColumnTest extends TestBase {

    private static final String COLUMNNAME1 = "column1";
    private static final String COLUMNNAME2 = "column2";

    @Test
    public void testColumnEquality() {
        Column column1 = new Column(COLUMNNAME1, Types.INTEGER, 1, 2, true);
        Column column2 = new Column(COLUMNNAME1, Types.INTEGER, 1, 2, true);

        assertEquals(column1, column2);
        assertEquals(column1, column1);
    }

    @Test
    public void testColumnUnequality() {
        Column column1 = new Column(COLUMNNAME1, Types.INTEGER, 0, 0, true);
        Column column2 = new Column(COLUMNNAME2, Types.INTEGER, 0, 0, true);
        assertNotSame(column1, column2);

        column2 = new Column(COLUMNNAME1, Types.VARCHAR, 0, 0, true);
        assertNotSame(column1, column2);

        column2 = new Column(COLUMNNAME1, Types.INTEGER, 1, 0, true);
        assertNotSame(column1, column2);

        column2 = new Column(COLUMNNAME1, Types.INTEGER, 0, 0, false);
        assertNotSame(column1, column2);

        column2 = new Column(COLUMNNAME1, Types.INTEGER, 0, 2, true);
        assertNotSame(column1, column2);

        assertNotSame(column1, null);
    }
}
