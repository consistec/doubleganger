package de.consistec.doubleganger.common.util;

/*
 * #%L
 * Project - doubleganger
 * File - PropertiesUtilTest.java
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

import static de.consistec.doubleganger.common.ConfigConstants.DELIMITER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.consistec.doubleganger.common.TestBase;
import de.consistec.doubleganger.common.conflict.ConflictStrategy;
import de.consistec.doubleganger.common.exception.ConfigException;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Properties;
import org.junit.Test;

/**
 * Tests of properties utilities.
 *
 * @author Piotr Wieczorek
 * @company consistec Engineering and Consulting GmbH
 * @date 29.10.2012 09:31:49
 * @since 0.0.1-SNAPSHOT
 */
public class PropertiesUtilTest extends TestBase {

    /**
     * Checks if tested method returns instance of proper class and if it throws proper exception when such a class
     * does not exists on CLASS PATH.
     *
     * @throws NoSuchMethodException
     * @throws Exception
     */
    @Test
    public void testReadClass() throws NoSuchMethodException, Exception {

        Properties props = new Properties();
        String propsName = "class";
        props.put(propsName, getClass().getCanonicalName());
        assertEquals("Expected and returned Class are different", getClass(),
            PropertiesUtil.readClass(props, propsName, true));
        assertNull("Reading nonexisting and optional value should return NULL",
            PropertiesUtil.readClass(new Properties(), propsName, false));
        try {
            PropertiesUtil.readClass(new Properties(), propsName, true);
        } catch (Throwable e) {
            assertEquals(
                "Reading value from Properties object when corresponding value does not exists should throw " + ConfigException.class.getSimpleName(),
                ConfigException.class, e.getClass());
        }
    }

    /**
     * Checks if tested method returns instance of proper enumeration and if it throws proper exception when such a
     * enumeration does not exists on CLASS PATH.
     *
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    @Test
    public void testReadEnum() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
        IllegalArgumentException, InstantiationException, InvocationTargetException {

        Properties props = new Properties();
        String propsName = "enum";
        ConflictStrategy enumValue = ConflictStrategy.CLIENT_WINS;
        props.put(propsName, enumValue.name());
        assertEquals("Expected and returned enumeration values are different", enumValue,
            PropertiesUtil.readEnum(props, propsName, true, ConflictStrategy.class));
        assertNull("Reading nonexisting and optional value should return NULL",
            PropertiesUtil.readEnum(new Properties(), propsName, false, ConflictStrategy.class));
        try {
            PropertiesUtil.readEnum(props, propsName, true, ConflictStrategy.class);
        } catch (Throwable e) {
            assertEquals(
                "Reading value from Properties object when corresponding value does not exists should throw " + ConfigException.class.getSimpleName(),
                ConfigException.class, e.getClass());
        }
    }

    /**
     * Checks if tested method returns properly constructed collection and if it throws proper exception when a values
     * for the collection does not exists in properties object.
     *
     * @throws Exception
     */
    @Test
    public void testReadCollection() throws Exception {

        Properties props = new Properties();
        String propsName = "collection";
        String el1 = "element1";
        String el2 = "element2";
        String el3 = "element3";
        String collectionAsString = el1 + DELIMITER + el2 + DELIMITER + el3;
        ArrayList<String> expected = new ArrayList<String>(3);
        expected.add(el1);
        expected.add(el2);
        expected.add(el3);
        props.put(propsName, collectionAsString);
        assertEquals("Expected and returned collection are different", expected,
            PropertiesUtil.readCollection(props, propsName, true, ArrayList.class));
        assertNull("Reading nonexisting and optional value should return NULL",
            PropertiesUtil.readCollection(new Properties(), propsName, false, ArrayList.class));
        try {
            PropertiesUtil.readCollection(new Properties(), propsName, true, ArrayList.class);
        } catch (Throwable e) {
            assertEquals(
                "Reading value from Properties object when corresponding value does not exists should throw " + ConfigException.class.getSimpleName(),
                ConfigException.class, e.getClass());
        }
    }

    /**
     * Checks if tested method returns instance of numeric class and if it throws proper exception when there is no
     * value in properties object.
     *
     * @throws Exception
     */
    @Test
    public void testReadNumber() throws Exception {

        Properties props = new Properties();
        String propsName = "number";
        Byte byteVal = 1;
        Short shortVal = 1;
        Integer integerVal = 1000;
        Long longVal = 20999L;
        Float floatVal = 123.12321F;
        Double doubleVal = 1.2324235123123124E7;
        BigInteger bigIntegerVal = new BigInteger("124234325435252");
        BigDecimal bigDecimalVal = new BigDecimal("242354354356245135.13543543543151543");
        props.put(propsName, String.valueOf(byteVal));
        assertEquals("Expected and returned Byte value are different", byteVal,
            PropertiesUtil.readNumber(props, propsName, true, Byte.class));
        props.put(propsName, String.valueOf(shortVal));
        assertEquals("Expected and returned Short value are different", shortVal,
            PropertiesUtil.readNumber(props, propsName, true, Short.class));
        props.put(propsName, String.valueOf(integerVal));
        assertEquals("Expected and returned Integer value are different", integerVal,
            PropertiesUtil.readNumber(props, propsName, true, Integer.class));
        props.put(propsName, String.valueOf(longVal));
        assertEquals("Expected and returned Long value are different", longVal,
            PropertiesUtil.readNumber(props, propsName, true, Long.class));
        props.put(propsName, String.valueOf(floatVal));
        assertEquals("Expected and returned Float value are different", floatVal,
            PropertiesUtil.readNumber(props, propsName, true, Float.class));
        props.put(propsName, String.valueOf(doubleVal));
        assertEquals("Expected and returned Double value are different", doubleVal,
            PropertiesUtil.readNumber(props, propsName, true, Double.class));
        props.put(propsName, String.valueOf(bigIntegerVal));
        assertEquals("Expected and returned BigInteger value are different", bigIntegerVal,
            PropertiesUtil.readNumber(props, propsName, true, BigInteger.class));
        props.put(propsName, String.valueOf(bigDecimalVal));
        assertEquals("Expected and returned BigDecimal value are different", bigDecimalVal,
            PropertiesUtil.readNumber(props, propsName, true, BigDecimal.class));
        assertNull("Reading nonexisting and optional value should return NULL",
            PropertiesUtil.readNumber(new Properties(), propsName, false, Integer.class));
        try {
            PropertiesUtil.readNumber(new Properties(), propsName, true, Byte.class);
        } catch (Throwable e) {
            assertEquals(
                "Reading value from Properties object when corresponding value does not exists should throw " + ConfigException.class.getSimpleName(),
                ConfigException.class, e.getClass());
        }
    }

    @Test
    public void testDefaultIfNull() {
        Integer defaultInteger = Integer.valueOf(4);
        Integer valueToFill;

        valueToFill = PropertiesUtil.defaultIfNull(defaultInteger, null);

        assertTrue(defaultInteger.equals(valueToFill));
    }

    @Test
    public void testDefaultIfNotNull() {
        Integer defaultInteger = Integer.valueOf(4);
        Integer valueToFill;
        Integer expectedValue = Integer.valueOf(5);

        valueToFill = PropertiesUtil.defaultIfNull(defaultInteger, expectedValue);

        assertTrue(expectedValue.equals(valueToFill));
    }
}