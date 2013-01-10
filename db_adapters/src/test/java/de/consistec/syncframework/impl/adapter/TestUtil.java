package de.consistec.syncframework.impl.adapter;

import de.consistec.syncframework.common.data.schema.Column;
import de.consistec.syncframework.common.data.schema.Constraint;
import de.consistec.syncframework.common.data.schema.ConstraintType;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.data.schema.Table;

import java.io.InputStream;
import java.sql.Types;
import java.util.Scanner;

/**
 * Utility methods to use in unit tests.
 *
 * @company Consistec Engineering and Consulting GmbH
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
