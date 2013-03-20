package de.consistec.doubleganger.common.i18n;

/*
 * #%L
 * Project - doppelganger
 * File - MessageReader.java
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
import static de.consistec.doubleganger.common.util.CollectionsUtil.newConcurrentHashMap;

import de.consistec.doubleganger.common.util.DefaultMessageConveyorSearcher;

import ch.qos.cal10n.IMessageConveyor;
import ch.qos.cal10n.util.AnnotationExtractor;
import java.util.Locale;
import java.util.Map;

/**
 * @company consistec Engineering and Consulting GmbH
 * @date 29.11.2012 10:09:04
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
public final class MessageReader {

    /**
     * Default conveyor is specified as field to avoid unnecessary searches through the map.
     */
    private static final IMessageConveyor DEFAULT_MESSAGE_CONVEYOR = DefaultMessageConveyorSearcher.search();
    private static final Map<Locale, IMessageConveyor> CACHE = newConcurrentHashMap(5);

    static {
        CACHE.put(Locale.getDefault(), DEFAULT_MESSAGE_CONVEYOR);
    }

    private MessageReader() {
        throw new AssertionError("No instances allowed");
    }

    /**
     * Reads translated string.
     * Method searches the ResourceBundle for default system locale, if provided, and if not, it searches the English
     * ResourceBundle, distributed with framework.
     *
     * @param keysEnum Key for searching the string in a ResourceBundle
     * @param args Optional arguments for message
     * @return Message from ResourceBundle for default locale.
     */
    public static String read(Enum<?> keysEnum, Object... args) {
        checkEnum(keysEnum);
        return DEFAULT_MESSAGE_CONVEYOR.getMessage(keysEnum, args);
    }

    private static void checkEnum(Enum<?> keysEnum) {
        if (AnnotationExtractor.getBaseName(keysEnum.getDeclaringClass()) == null) {
            throw new IllegalArgumentException("Provided enumeration is not an CAL10n enum!");
        }
    }
}
