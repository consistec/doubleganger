package de.consistec.doubleganger.common.data.schema;

/*
 * #%L
 * Project - doubleganger
 * File - ColumnTest.java
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
