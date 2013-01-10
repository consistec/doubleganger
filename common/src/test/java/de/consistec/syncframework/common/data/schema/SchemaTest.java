package de.consistec.syncframework.common.data.schema;

import static junit.framework.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;

import de.consistec.syncframework.common.TestBase;

import org.junit.Test;

/**
 *
 * Date: 17.07.12  11:27
 *
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class SchemaTest extends TestBase {

    private static final String TABLENAME1 = "table1";
    private static final String TABLENAME2 = "table2";

    @Test
    public void testSchemaEquality() {
        Schema schema = new Schema();
        schema.addTables(new Table(TABLENAME1));
        Schema schema2 = new Schema();
        schema2.addTables(new Table(TABLENAME1));

        assertEquals(schema, schema2);
        assertEquals(schema, schema);
    }

    @Test
    public void testSchemaUnequality() {
        Schema schema = new Schema();
        schema.addTables(new Table(TABLENAME1));
        Schema schema2 = new Schema();
        schema2.addTables(new Table(TABLENAME2));

        assertNotSame(schema, schema2);
        assertNotSame(schema, null);
    }
}
