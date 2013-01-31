package de.consistec.syncframework.common.data.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import de.consistec.syncframework.common.TestBase;

import org.junit.Test;

/**
 * Tests of Constraint object.
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 26.07.12 09:56
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class ConstraintTest extends TestBase {

    private static final String COLUMNNAME1 = "column1";
    private static final String COLUMNNAME2 = "column2";
    private static final String CONSTRAINTNAME1 = "PK";
    private static final String CONSTRAINTNAME2 = "FK";

    @Test
    public void testConstraintEquality() {
        Constraint constraint1 = new Constraint(ConstraintType.PRIMARY_KEY, CONSTRAINTNAME1, COLUMNNAME1);
        Constraint constraint2 = new Constraint(ConstraintType.PRIMARY_KEY, CONSTRAINTNAME1, COLUMNNAME1);

        assertEquals(constraint1, constraint2);
        assertEquals(constraint1, constraint1);
    }

    @Test
    public void testConstraintUnequality() {
        Constraint constraint1 = new Constraint(ConstraintType.PRIMARY_KEY, CONSTRAINTNAME1, COLUMNNAME1);
        Constraint constraint2 = new Constraint(ConstraintType.PRIMARY_KEY, CONSTRAINTNAME2, COLUMNNAME1);
        assertNotSame(constraint1, constraint2);

        constraint2 = new Constraint(ConstraintType.PRIMARY_KEY, CONSTRAINTNAME1, COLUMNNAME2);
        assertNotSame(constraint1, constraint2);

        assertNotSame(constraint1, null);
    }
}
