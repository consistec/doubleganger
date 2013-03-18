package de.consistec.doubleganger.common.data.schema;

/*
 * #%L
 * Project - doppelganger
 * File - ConstraintTest.java
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
 * Tests of Constraint object.
 *
 * @company consistec Engineering and Consulting GmbH
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
