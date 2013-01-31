package de.consistec.syncframework.logging;

import java.util.Arrays;

/**
 * <ul style="list-style-type: none;">
 * <li><b>Company:</b> consistec Engineering and Consulting GmbH</li>
 * <li><b>Date:</b> 26.11.12 10:03</li>
 * </ul>
 *
 * @author marcel
 */
public class TracingAspectUtil {


    public static String argsToString(Object[] paramValues, String[] paramNames) {
        StringBuilder argsString = new StringBuilder();
        for (int i = 0; i < paramValues.length; i++) {
            argsString.append(paramNames[i]).append("=")
                .append(toString(paramValues[i]));
            if (i < paramValues.length - 1) {
                argsString.append(", ");
            }
        }

        return argsString.toString();
    }

    @SuppressWarnings("rawtypes")
    public static String toString(Object object) {
        if (object == null) {
            return "<null>";
        } else if (object instanceof String) {
            if (((String) object).length() > 100) {
                return ((String) object).substring(0, 100) + "...[more]";
            } else {
                return (String) object;
            }
        } else if (object instanceof Long) {
            return ((Long) object).toString();
        } else if (object instanceof Boolean) {
            return ((Boolean) object).toString();
        } else if (object instanceof Double) {
            return ((Double) object).toString();
        } else if (object instanceof Integer) {
            return ((Integer) object).toString();
        } else if (object.getClass().isArray()) {
            return toArrayItems(object);
        } else {
            return object.toString();
        }
    }

    public static String toArrayItems(Object object) {
        if (object instanceof Object[]) {
            return Arrays.toString((Object[]) object);
        } else if (object instanceof long[]) {
            return Arrays.toString((long[]) object);
        } else if (object instanceof double[]) {
            return Arrays.toString((double[]) object);
        } else if (object instanceof int[]) {
            return Arrays.toString((int[]) object);
        } else if (object instanceof short[]) {
            return Arrays.toString((short[]) object);
        } else if (object instanceof char[]) {
            return Arrays.toString((char[]) object);
        } else if (object instanceof float[]) {
            return Arrays.toString((float[]) object);
        } else if (object instanceof boolean[]) {
            return Arrays.toString((boolean[]) object);
        } else if (object instanceof byte[]) {
            return Arrays.toString((byte[]) object);
        } else {
            return object.toString();
        }
    }

}
