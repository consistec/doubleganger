package de.consistec.doubleganger.common.data.schema;

/*
 * #%L
 * Project - doubleganger
 * File - TableTest.java
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
import static org.junit.Assert.assertNotSame;

import de.consistec.doubleganger.common.TestBase;

import org.junit.Test;

/**
 * Tests of Table object.
 *
 * @company consistec Engineering and Consulting GmbH
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
