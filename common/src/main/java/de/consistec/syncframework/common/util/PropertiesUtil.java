package de.consistec.syncframework.common.util;

import static de.consistec.syncframework.common.ConfigConstants.DELIMITER;
import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.exception.ConfigException;
import de.consistec.syncframework.common.i18n.Errors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

/**
 * Utilities to read and convert values from {@link java.util.Properties} objects.
 *
 * @author Piotr Wieczorek
 * @company consistec Engineering and Consulting GmbH
 * @date 29.10.2012 09:25:43
 * @since 0.0.1-SNAPSHOT
 */
public final class PropertiesUtil {

    //<editor-fold defaultstate="expanded" desc=" Class constructors" >
    // Allow no instannces.
    private PropertiesUtil() {
        throw new AssertionError("No instances allowed");
    }
    //</editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class methods" >

    /**
     * Reads the enumeration value from given Properties object, and if the property isn't found,
     * then depending on the <i>"required"</i> param value, method will ends program
     * throws ConfigException object or return <i>null</i>.
     *
     * @param <T> Enumeration type
     * @param props Properties configuration object to read from.
     * @param name Key name which corresponds to enum value in give properties object.
     * @param required true if methods should ends with ConfigException in cease of property absence.
     * @param clazz Enumeration class
     * @return Enum value specified in given Properties object under the key <i>"name"</i>
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static <T extends Enum> T readEnum(final Properties props, final String name, final boolean required,
                                              final Class<T> clazz
    ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final String stringValue = readString(props, name, required);
        if (StringUtil.isNullOrEmpty(stringValue) && !required) {
            return null;
        }
        final Method valueOfMethod = clazz.getMethod("valueOf", String.class);
        return (T) valueOfMethod.invoke(null, stringValue);
    }

    /**
     * Reads the numeric value from given Properties object, and if the property isn't found,
     * then depending on the <i>"required"</i> value, method will ends program
     * throws ConfigException object or return <i>null</i>.
     *
     * @param <T> Type of value
     * @param props Properties configuration object to read from.
     * @param name Key to find the number in <i>props</i> object.
     * @param required true if methods should ends with ConfigException in cease of property absence.
     * @param clazz Type of numeric value
     * @return The number specified in given Properties object under the key <i>"name"</i>
     */
    public static <T extends Number> T readNumber(final Properties props, final String name, final boolean required,
                                                  final Class<T> clazz
    ) { //NOSONAR
        String stringValue = readString(props, name, required);

        if (!required && StringUtil.isNullOrEmpty(stringValue)) {
            return null;
        }
        if ("byte".equalsIgnoreCase(clazz.getSimpleName())) {
            return (T) Byte.decode(stringValue);
        }
        if ("short".equalsIgnoreCase(clazz.getSimpleName())) {
            return (T) Short.decode(stringValue);
        }
        if ("integer".equalsIgnoreCase(clazz.getSimpleName())) {
            return (T) Integer.valueOf(stringValue);
        }
        if ("long".equalsIgnoreCase(clazz.getSimpleName())) {
            return (T) Long.valueOf(stringValue);
        }
        if ("float".equalsIgnoreCase(clazz.getSimpleName())) {
            return (T) Float.valueOf(stringValue);
        }
        if ("double".equalsIgnoreCase(clazz.getSimpleName())) {
            return (T) Double.valueOf(stringValue);
        }
        if ("biginteger".equalsIgnoreCase(clazz.getSimpleName())) {
            return (T) new BigInteger(stringValue);
        }
        if ("bigdecimal".equalsIgnoreCase(clazz.getSimpleName())) {
            return (T) new BigDecimal(stringValue);
        }
        return null;
    }

    /**
     * Reads the string value from given Properties object, and if the property isn't found,
     * then depending on the <i>"required"</i> value, method will ends program
     * throws ConfigException object or returns <i>null</i>.
     *
     * @param props Properties object to read from.
     * @param name Key to find the String in <i>props</i> object.
     * @param required true if methods should ends with ConfigException in cease of property absence.
     * @return String value specified in given Properties object under the key <i>"name"</i>
     *         or null if {@code required} parameter is {@code false}.
     */
    public static String readString(final Properties props, final String name, final boolean required) {

        final String result = props.getProperty(name);
        if (required && StringUtil.isNullOrEmpty(result)) {
            throw new ConfigException(read(Errors.CONFIG_OPTION_IS_MISSING, name));
        }

        return result;
    }

    /**
     * Reads the string's collection from given Properties object.
     * And if the property isn't found, then depending on the <i>"required"</i> value,
     * method will ends program throws ConfigException object or returns <i>null</i>..
     *
     * @param props Properties object to read from.
     * @param name Key to find the number in <i>props</i> object.
     * @param required true if methods should ends with ConfigException in cease of property absence.
     * @param clazz type of returned collection
     * @return Collection of Strings specified as one String separated with
     *         {@link de.consistec.syncframework.common.Config#DELIMITER } in given Properties object under the key
     *         <i>"name"</i>
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static Collection<String> readCollection(final Properties props, final String name, final boolean required,
                                                    final Class<? extends Collection> clazz
    ) throws InstantiationException, IllegalAccessException {

        final String value = readString(props, name, required);
        if (StringUtil.isNullOrEmpty(value) && !required) {
            return null;
        }

        final Collection<String> result = clazz.newInstance();
        if (!StringUtil.isNullOrEmpty(value)) {
            result.addAll(Arrays.asList(value.split(DELIMITER)));
        }

        return result;
    }

    /**
     * Reads the class value from given Properties object, and if the property isn't found,
     * then depending on the <i>"required"</i> value, method will ends program throws
     * ConfigException object or returns <i>null</i>..
     *
     * @param <T> type of class
     * @param props Properties object to read from.
     * @param name Key to find the class in <i>props</i> object.
     * @param required true if methods should ends with ConfigException in cease of property absence.
     * @return Class object specified in Properties object under the key <i>"name"</i>
     * @throws ClassNotFoundException
     */
    public static <T> T readClass(final Properties props, final String name, final boolean required) throws
        ClassNotFoundException {

        final String value = readString(props, name, required);

        if (StringUtil.isNullOrEmpty(value)) {
            return null;
        }
        return (T) Class.forName(value);
    }

    /**
     * Reads the boolean value from given Properties object, and if the property isn't found,
     * then depending on the <i>"required"</i> value, method will ends program
     * throws ConfigException object or returns <i>null</i>.
     *
     * @param props Properties object to read from.
     * @param name Key to find the class in <i>props</i> object.
     * @param required true if methods should ends with ConfigException in cease of property absence.
     * @return Boolean value specified in given Properties object under the key <i>"name"</i>
     */
    public static Boolean readBoolean(final Properties props, final String name, final boolean required) {

        final String result = props.getProperty(name);
        if (required && StringUtil.isNullOrEmpty(result)) {
            throw new ConfigException(read(Errors.CONFIG_OPTION_IS_MISSING, name));
        }

        Boolean parsedValue = null;
        try {
            parsedValue = Boolean.valueOf(result);
        } catch (IllegalArgumentException e) {
            throw new ConfigException(read(Errors.CONFIG_OPTION_IS_NOT_BOOLEAN, name), e);
        }
        return parsedValue;
    }

    /**
     * Returns default value if provided is <i>null</i>.
     *
     * @param <T> type of value.
     * @param defaultValue default value.
     * @param valueToCheck current value.
     * @return If <i>valueToCheck</i> is <i>null</i> then return <i>defaultValue</i>.
     *         Otherwise return <i>valueToCheck</i>.
     */
    public static <T> T defaultIfNull(final T defaultValue, final T valueToCheck) {

        return (valueToCheck == null ? defaultValue : valueToCheck);
    }
}
