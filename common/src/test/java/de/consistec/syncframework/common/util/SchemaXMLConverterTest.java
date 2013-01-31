package de.consistec.syncframework.common.util;

import static org.junit.Assert.assertEquals;

import de.consistec.syncframework.common.TestBase;
import de.consistec.syncframework.common.TestUtil;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.data.schema.SchemaXMLConverter;
import de.consistec.syncframework.common.exception.SerializationException;

import org.junit.Test;

/**
 * Tests schema xml converter.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 26.07.12 10:03
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class SchemaXMLConverterTest extends TestBase {

    @Test
    public void testSchemaXMLConverter() throws SerializationException {
        Schema fromXml = new SchemaXMLConverter().fromXML(TestUtil.getStringFromXMLFile("test_schema.xml"));
        Schema expectedSchema = TestUtil.getSchema();
        assertEquals(expectedSchema, fromXml);

        String xml = new SchemaXMLConverter().toXML(expectedSchema);
        expectedSchema = new SchemaXMLConverter().fromXML(xml);

        assertEquals(fromXml, expectedSchema);
    }
}
