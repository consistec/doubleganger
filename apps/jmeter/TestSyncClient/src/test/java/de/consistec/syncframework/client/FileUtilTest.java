/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.consistec.syncframework.client;

import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 * @author Piotr Wieczorek
 * @since
 * Company: consistec Engineering and Consulting GmbH
 * Date: 10.10.2012
 * Time: 14:59:41 
 */
public class FileUtilTest {

    public FileUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getExtension method, of class FileUtil.
     */
    @Test
    public void testGetExtension() {
        System.out.println("getExtension");
        assertEquals("Extracted filename extension is wrong", "log", FileUtil.getExtension("sync232132nn.log"));
        assertEquals("Extracted filename extension is wrong", "log", FileUtil.getExtension("sync232..13.2nn.log"));
        assertNull("Extracted filename extension should be \"null\"", FileUtil.getExtension("sync232"));
        assertNull("Extracted filename extension should be \"null\"", FileUtil.getExtension("sync232."));
    }

}