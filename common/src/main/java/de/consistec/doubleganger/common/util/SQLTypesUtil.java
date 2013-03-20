package de.consistec.doubleganger.common.util;

/*
 * #%L
 * Project - doppelganger
 * File - SQLTypesUtil.java
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
import static de.consistec.doubleganger.common.i18n.MessageReader.read;

import de.consistec.doubleganger.common.i18n.Errors;

import java.lang.reflect.Field;
import java.sql.Types;

/**
 * Utility methods to make work with {@link java.sql.Types} easier.
 * <p/>
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date 26.07.12 13:57
 * @since 0.0.1-SNAPSHOT
 */
public final class SQLTypesUtil {

    private SQLTypesUtil() {
        throw new AssertionError("No instances allowed");
    }

    /**
     * Its just a utility method to difference sql types in three categories.
     * <br/>
     * <ul>
     * <li>0: float, char, varchar, blob, clob</li>,
     * <li>1: decimal</li>,
     * <li>-1: integer, bigint, real, double, date, time, timestamp, boolean, numeric</li>
     * <p/>
     * </ul>
     * <br/>
     *
     * @param type Type code
     * @return 0 for the first category, 1 for the second category and -1 for the third category.
     * If {@code type} could not be resolved to a size, then {@link IllegalArgumentException} exception will be thrown.
     */
    public static int sizeType(final int type) {
        switch (type) {
            case Types.INTEGER:
            case Types.BIGINT:
            case Types.REAL:
            case Types.DOUBLE:
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
            case Types.BOOLEAN:
            case Types.NUMERIC:
                return -1;
            case Types.DECIMAL:
                return 1;
            case Types.FLOAT:
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.BLOB:
            case Types.CLOB:
                return 0;
            default:
                throw new IllegalArgumentException(read(Errors.DATA_NOT_SUPPORTED_SQL_TYPE));
        }
    }

    /**
     * Converts the given string to a java.sql.Types type.
     * <p/>
     *
     * @param value Name of the type.
     * @return The java.sql.Types type of the given string
     * @throws IllegalAccessException
     */
    public static int typeOf(String value) throws IllegalAccessException {
        Field[] fields = java.sql.Types.class.getFields();
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(value)) {
                return field.getInt(null);
            }
        }
        return Types.OTHER;
    }

    /**
     * Returns the name of the given java.sql.Types type.
     * <p/>
     *
     * @param type SQL type code.
     * @return Name of the type type.
     * @throws IllegalAccessException
     */
    public static String nameOf(int type) throws IllegalAccessException {
        Field[] fields = java.sql.Types.class.getFields();
        for (Field field : fields) {
            if (field.getInt(null) == type) {
                return field.getName();
            }
        }

        return "OTHER";
    }
}
