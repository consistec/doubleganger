package de.consistec.syncframework.common.util;

/**
 * String utility class.
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 11.10.2012 09:04:56
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPHOST
 */
public final class StringUtil {

    //<editor-fold defaultstate="expanded" desc=" Class constructors" >
    /**
     * It's utility class, so no instances.
     */
    private StringUtil() {
        throw new AssertionError("No instances allowed");
    }
    //</editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class methods" >

    /**
     * Checks if provided string value is null or empty.
     * 
     * @param value String value to check.
     * @return true if string is empty or null.
     */
    public static boolean isNullOrEmpty(final String value) {
        return value == null || value.isEmpty();
    }

    //</editor-fold>
}
