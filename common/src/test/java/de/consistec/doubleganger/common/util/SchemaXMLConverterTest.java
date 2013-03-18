package de.consistec.doubleganger.common.util;

/*
 * #%L
 * Project - doppelganger
 * File - SchemaXMLConverterTest.java
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

import static org.junit.Assert.assertEquals;

import de.consistec.doubleganger.common.TestBase;
import de.consistec.doubleganger.common.TestUtil;
import de.consistec.doubleganger.common.data.schema.Schema;
import de.consistec.doubleganger.common.data.schema.SchemaXMLConverter;
import de.consistec.doubleganger.common.exception.SerializationException;

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
