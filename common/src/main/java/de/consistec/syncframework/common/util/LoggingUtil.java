package de.consistec.syncframework.common.util;

import static de.consistec.syncframework.common.util.CollectionsUtil.newConcurrentHashMap;

import ch.qos.cal10n.IMessageConveyor;
import java.util.Locale;
import java.util.Map;
import org.slf4j.cal10n.LocLogger;
import org.slf4j.cal10n.LocLoggerFactory;

/**
 * Utility class to facilitate loggers handling.
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 29.11.2012 09:23:10
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
public final class LoggingUtil {
//<editor-fold defaultstate="expanded" desc=" Class fields " >

    /**
     * Default conveyor is specified as field to avoid unnecessary searches through the map.
     */
    private static final IMessageConveyor DEFAULT_MESSAGE_CONVEYOR = DefaultMessageConveyorSearcher.search();
    private static final LocLoggerFactory DEFAULT_FACTORY = new LocLoggerFactory(DEFAULT_MESSAGE_CONVEYOR);
    private static Map<Locale, IMessageConveyor> cache = newConcurrentHashMap(5);
//</editor-fold>

    static {
        cache.put(Locale.getDefault(), DEFAULT_MESSAGE_CONVEYOR);
    }

//<editor-fold defaultstate="expanded" desc=" Class constructors " >
    private LoggingUtil() {
        throw new AssertionError("No instances allowed");
    }
//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class methods " >

    /**
     * Create an instance of "localization aware" logger for system defaults locale.
     *
     * @param name Name for the logger.
     * @return Localized logger instance.
     */
    public static LocLogger createLogger(String name) {
        return DEFAULT_FACTORY.getLocLogger(name);
    }

    /**
     * Create an instance of "localization aware" logger for system defaults locale.
     *
     * @param clazz Class for constructing logger's name.
     * @return Localized logger instance.
     */
    public static LocLogger createLogger(Class clazz) {
        return DEFAULT_FACTORY.getLocLogger(clazz);
    }

//    This method will be needed in future, for various bundles.
//    public static IMessageConveyor lookup(Locale locale) {
//        IMessageConveyor conveyor = cache.get(locale);
//        if (conveyor == null) {
//            conveyor = new MessageConveyor(locale);
//            cache.put(locale, conveyor);
//        }
//        return conveyor;
//    }

//</editor-fold>

}
