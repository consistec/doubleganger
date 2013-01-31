package de.consistec.syncframework.impl.adapter;

import de.consistec.syncframework.common.Config;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Scanner;
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
                LOGGER.info("Framework configuration reseted");
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

    @BeforeClass
    public static void setUpClass() {
        // initialize logging framework
        DOMConfigurator.configure(ClassLoader.getSystemResource("log4j.xml"));
    }

    /**
     * Returns content of the xml file as simple string.
     *
     * @param filename XML file
     * @return Content of the file
     */
    protected static String getStringFromFile(String filename) {
        InputStream is = TestBase.class.getClassLoader().getResourceAsStream(filename);
        return new Scanner(is, "UTF-8").useDelimiter("\\A").next();
    }

    private void resetConfigSingleton() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field instance = Config.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }
}
