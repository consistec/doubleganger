package de.consistec.syncframework.common.util;

/*
 * #%L
 * Project - doppelganger
 * File - StringUtil.java
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

/**
 * String utility class.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 11.10.2012 09:04:56
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
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
