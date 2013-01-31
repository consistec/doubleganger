package de.consistec.syncframework.common.util;

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