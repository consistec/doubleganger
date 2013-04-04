package de.consistec.doubleganger.common.data.schema;

/*
 * #%L
 * Project - doubleganger
 * File - SchemaTest.java
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import de.consistec.doubleganger.common.TestBase;

import java.sql.Types;
import java.util.Iterator;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * Date: 17.07.12 11:27
 *
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class SchemaTest extends TestBase {

    private static final String TABLENAME1 = "table1";
    private static final String TABLENAME2 = "table2";
    private static final String COLUMNNAME1 = "column1";
    private static final String COLUMNNAME2 = "column2";
    private static final String COLUMNNAME3 = "column3";
    private static final String COLUMNNAME4 = "column4";
    Table table1;
    Column column1, column2, column3, column4;

    @Before
    public void setUp() {
        column1 = new Column(COLUMNNAME1, Types.INTEGER, 1, 2, false);
        column2 = new Column(COLUMNNAME2, Types.INTEGER, 1, 2, true);
        column3 = new Column(COLUMNNAME4, Types.VARCHAR, 15, 2, false);
        column4 = new Column(COLUMNNAME3, Types.INTEGER, 1, 2, true);

        Constraint pkConstraint = new Constraint(ConstraintType.PRIMARY_KEY, "table1_pk_constraint", COLUMNNAME1);

        table1 = new Table(TABLENAME1);
        table1.add(new Column[]{column1, column2, column3, column4});
        table1.add(new Constraint[]{pkConstraint});
    }

    @Test
    public void testSchemaEquality() {
        Schema schema1 = new Schema();
        schema1.addTables(new Table(TABLENAME1));
        Schema schema2 = new Schema();
        schema2.addTables(new Table(TABLENAME1));

        assertEquals(schema1, schema2);
        assertEquals(schema1, schema1);
    }

    @Test
    public void testSchemaUnequality() {
        Schema schema1 = new Schema();
        schema1.addTables(new Table(TABLENAME1));
        Schema schema2 = new Schema();
        schema2.addTables(new Table(TABLENAME2));

        assertNotSame(schema1, schema2);
        assertNotNull(schema1);
    }

    @Test
    public void testCount() {
        Schema schema1 = new Schema();
        schema1.addTables(new Table(TABLENAME1));

        assertEquals(1, schema1.countTables());
    }

    @Test(expected=NullPointerException.class)
    public void addNullTableSHouldThrowException() {
        Schema schema1 = new Schema();

        schema1.addTables(new Table[]{null, null});
    }

    @Test
    public void testRemoveExistingTable() {
        Schema schema1 = new Schema();

        schema1.addTables(table1);
        schema1.removeTables(table1);

        assertEquals(0, schema1.countTables());
    }
    @Test
    public void testRemoveUnexistingTable() {
        Schema schema1 = new Schema();

        schema1.addTables(new Table(TABLENAME1));
        schema1.removeTables(table1);

        assertEquals(1, schema1.countTables());
    }

    @Test
    public void testPkColumnName() {
        Schema schema1 = new Schema();
        schema1.addTables(table1);
        Iterator<Table> tableIterator = schema1.getTables().iterator();
        String pkColumnName = tableIterator.next().getPkColumnName();

        assertEquals(COLUMNNAME1, pkColumnName);
    }

}
