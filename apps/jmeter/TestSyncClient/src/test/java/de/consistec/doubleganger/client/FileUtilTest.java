/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.consistec.doubleganger.client;

/*
 * #%L
 * doubleganger
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