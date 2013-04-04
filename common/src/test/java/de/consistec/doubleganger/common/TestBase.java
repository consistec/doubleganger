package de.consistec.doubleganger.common;

/*
 * #%L
 * Project - doubleganger
 * File - TestBase.java
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

import java.lang.reflect.Field;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all unit test.
 * This class hold instances of logger, junit test watcher, and loads logging facility's configuration.
 *
 * @author Piotr Wieczorek
 * @company consistec Engineering and Consulting GmbH
 * @date 16.10.2012 13:41:03
 * @since 0.0.1-SNAPSHOT
 */
public class TestBase {

    /**
     * Logger instance.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(TestBase.class.getCanonicalName());

    /**
     * Test watcher, with methods invoked before and after of each tests.
     * This "watcher" prints test name before and after each test, and resets configuration singleton before each test.
     */
    @Rule
    public TestRule watchman = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            LOGGER.info("start {} for test class {} ...", description.getMethodName(),
                description.getTestClass().getCanonicalName());
            try {
//                resetConfigSingleton();
                LOGGER.info("Framework configuration resetted");
            } catch (Exception ex) {
                LOGGER.warn("Can't reset framework configuration ", ex);
            }
        }

        @Override
        protected void finished(Description description) {
            LOGGER.info("end {} for test class {}", description.getMethodName(),
                description.getTestClass().getCanonicalName());
        }
    };


    /**
     * Allow only instance of subclasses.
     */
    protected TestBase() {
        // do nothing
    }

    private void resetConfigSingleton() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException,
        NoSuchMethodException {
        Field instance = Config.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    /**
     * Load log4j configuration file.
     */
    @BeforeClass
    public static void setUpClass() {
        // initialize logging framework
        DOMConfigurator.configure(ClassLoader.getSystemResource("log4j.xml"));
    }
}
