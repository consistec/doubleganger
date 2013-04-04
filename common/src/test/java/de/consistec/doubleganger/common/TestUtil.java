package de.consistec.doubleganger.common;

/*
 * #%L
 * Project - doubleganger
 * File - TestUtil.java
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

import de.consistec.doubleganger.common.data.schema.Column;
import de.consistec.doubleganger.common.data.schema.Constraint;
import de.consistec.doubleganger.common.data.schema.ConstraintType;
import de.consistec.doubleganger.common.data.schema.Schema;
import de.consistec.doubleganger.common.data.schema.Table;

import java.io.InputStream;
import java.sql.Types;
import java.util.Scanner;

/**
 * Utility methods to use in unit tests.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 30.07.12 14:28
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class TestUtil {


    private static final String TABLENAME1 = "table1";
    private static final String TABLENAME2 = "table2";
    private static final String COLUMNNAME1 = "column1";
    private static final String COLUMNNAME2 = "column2";
    private static final String COLUMNNAME3 = "column3";
    private static final String COLUMNNAME4 = "column4";
    private static final String CONSTRAINTNAME1 = "pk_table1";
    private static final String CONSTRAINTNAME2 = "pk_table2";

    private TestUtil() {
        throw new AssertionError("No instance allowed");
    }

    /**
     * Prepares fake schema object.
     *
     * @return Schema object.
     */
    public static Schema getSchema() {
        Schema s = new Schema();
        Table t1 = new Table(TABLENAME1);
        t1.add(new Column(COLUMNNAME1, Types.INTEGER, 0, 0, false));
        t1.add(new Column(COLUMNNAME2, Types.VARCHAR, 25, 0, true));
        t1.add(new Constraint(ConstraintType.PRIMARY_KEY, CONSTRAINTNAME1, COLUMNNAME1));

        Table t2 = new Table(TABLENAME2);
        t2.add(new Column(COLUMNNAME3, Types.INTEGER, 0, 0, false));
        t2.add(new Column(COLUMNNAME4, Types.DOUBLE, 2, 2, true));
        t2.add(new Constraint(ConstraintType.PRIMARY_KEY, CONSTRAINTNAME2, COLUMNNAME3));

        s.addTables(t1);
        s.addTables(t2);
        return s;
    }

    /**
     * Returns content of the xml file as simple string.
     *
     * @param filename XML file
     * @return Content of the file
     */
    public static String getStringFromXMLFile(String filename) {
        InputStream is = TestUtil.class.getClassLoader().getResourceAsStream(filename);
        return new Scanner(is, "UTF-8").useDelimiter("\\A").next();
    }
}
