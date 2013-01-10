package de.consistec.syncframework.common.util;

import static org.junit.Assert.assertEquals;

import de.consistec.syncframework.common.TestBase;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * Tests of hash calculator.
 *
 * @company Consistec Engineering and Consulting GmbH
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
