package de.consistec.syncframework.common.util;

/*
 * #%L
 * Project - doppelganger
 * File - StringUtilTest.java
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.consistec.syncframework.common.TestBase;

import org.junit.Test;

/**
 * Test of string utility methods.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 11.10.2012 11:07
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
public class StringUtilTest extends TestBase {

    /**
     * Test of isNullOrEmpty method, of class StringUtil.
     */
    @Test
    public void testIsNullOrEmpty() {
        assertTrue("String isn't initialized, so result should be \"true\"", StringUtil.isNullOrEmpty(null));
        assertTrue("String is empty, so result should be \"true\"", StringUtil.isNullOrEmpty(""));
        assertFalse("String isn't emtpy, so result should be \"false\"", StringUtil.isNullOrEmpty("a"));
    }
}