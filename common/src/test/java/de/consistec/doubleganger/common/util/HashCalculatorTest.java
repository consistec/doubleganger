package de.consistec.doubleganger.common.util;

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

import de.consistec.doubleganger.common.TestBase;
import de.consistec.doubleganger.common.data.Change;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterException;
import java.security.NoSuchAlgorithmException;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
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

    private static final String MD5_HASH_FOR_EMPTY_BYTE_ARRAY = "D41D8CD98F00B204E9800998ECF8427E";
    private static final String SHA1_HASH_FOR_EMPTY_BYTE_ARRAY = "DA39A3EE5E6B4B0D3255BFEF95601890AFD80709";
    private HashCalculator hashCalculator;

    @Before
    public void setup() throws NoSuchAlgorithmException {
        hashCalculator = new HashCalculator("MD5");
    }

    @Test
    public void testHashCreationAgainstNullParameter() throws DatabaseAdapterException, NoSuchAlgorithmException {
        String hash = hashCalculator.calculateHash((Change) null);
        assertEquals(null, hash);

        hash = hashCalculator.calculateHash((Map<String, Object>) null);
        assertEquals(null, hash);
    }

    @Test
    public void testNullEntryCreationMd5() throws DatabaseAdapterException, NoSuchAlgorithmException {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        Change change = new Change(null, dataMap);
        dataMap.put("TestKey", null);
        assertEquals(MD5_HASH_FOR_EMPTY_BYTE_ARRAY, hashCalculator.calculateHash(change));
    }

    @Test
    public void testNullEntryCreationSha1() throws DatabaseAdapterException, NoSuchAlgorithmException {
        hashCalculator = new HashCalculator("SHA-1");
        Map<String, Object> dataMap = new HashMap<String, Object>();
        Change change = new Change(null, dataMap);
        dataMap.put("TestKey", null);
        assertEquals(SHA1_HASH_FOR_EMPTY_BYTE_ARRAY, hashCalculator.calculateHash(change));
    }
}
