package de.consistec.syncframework.common.util;

/*
 * #%L
 * Project - doppelganger
 * File - LoggingUtil.java
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

import static de.consistec.syncframework.common.util.CollectionsUtil.newConcurrentHashMap;

import ch.qos.cal10n.IMessageConveyor;
import java.util.Locale;
import java.util.Map;
import org.slf4j.cal10n.LocLogger;
import org.slf4j.cal10n.LocLoggerFactory;

/**
 * Utility class to facilitate loggers handling.
 *
 * @company consistec Engineering and Consulting GmbH
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

//</editor-fold>

}
