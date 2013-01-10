package de.consistec.syncframework.android.test;

import android.test.suitebuilder.TestSuiteBuilder;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * The Class de.consistec.syncframework.android.test.AllTests.
 */
public class AllTests extends TestSuite {

    /**
     * Suite.
     *
     * @return the de.consistec.syncframework.android.test
     */
    public static Test suite() {
        return new TestSuiteBuilder(AllTests.class).includeAllPackagesUnderHere().build();
    }
}
