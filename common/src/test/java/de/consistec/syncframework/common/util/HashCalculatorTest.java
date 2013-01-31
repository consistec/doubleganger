package de.consistec.syncframework.common.util;

/*
 * #%L
 * Project - doppelganger
 * File - HashCalculatorTest.java
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

import de.consistec.syncframework.common.TestBase;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * Tests of hash calculator.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 11.07.12 15:20
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class HashCalculatorTest extends TestBase {

    private static final String HASH_DATA = "data to build hash from 12345";
    private static final String HASH_FOR_HASH_DATA = "6E100FF1A9D0D186BD3E5B2287428025";
    private static final String HASH_FOR_EMPTY_BYTE_ARRAY = "D41D8CD98F00B204E9800998ECF8427E";

    @Test
    public void testHashCreationAgainstNullParameter() throws NoSuchAlgorithmException {
        HashCalculator hashCalculator = new HashCalculator();
        String hash = hashCalculator.getHash((byte[])null);
        assertEquals(null, hash);

        hash = hashCalculator.getHash((Map<String, Object>)null);
        assertEquals(null, hash);

        hash = hashCalculator.getHash(new byte[] { });
        assertEquals(HASH_FOR_EMPTY_BYTE_ARRAY, hash);
    }

    @Test
    public void testHashCreation() throws NoSuchAlgorithmException {
        HashCalculator hashCalculator = new HashCalculator();
        String hash = hashCalculator.getHash(HASH_DATA.getBytes());
        assertEquals(HASH_FOR_HASH_DATA, hash);
    }

    @Test
    public void testNullEntryCreation() throws NoSuchAlgorithmException {
        HashCalculator hashCalculator = new HashCalculator();
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("TestKey", null);
        assertEquals(HASH_FOR_EMPTY_BYTE_ARRAY, hashCalculator.getHash(dataMap));
    }
}
