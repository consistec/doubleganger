package de.consistec.syncframework.common.exception;

/*
 * #%L
 * Project - doppelganger
 * File - SchemaConverterException.java
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
 * Exception thrown during conversion of {@link de.consistec.syncframework.common.data.schema.Schema} objects.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 27.07.12 10:02
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class SchemaConverterException extends Exception {

    /**
     *
     */
    public SchemaConverterException() {
        super();
    }

    /**
     *
     * @param message Error message
     */
    public SchemaConverterException(String message) {
        super(message);
    }

    /**
     *
     * @param message Error message
     * @param cause The cause of exception
     */
    public SchemaConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     * @param cause The cause of exception
     */
    public SchemaConverterException(Throwable cause) {
        super(cause);
    }
}
