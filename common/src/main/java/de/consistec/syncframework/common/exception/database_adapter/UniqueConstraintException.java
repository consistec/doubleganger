package de.consistec.syncframework.common.exception.database_adapter;

/*
 * #%L
 * Project - doppelganger
 * File - UniqueConstraintException.java
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
 * Exception to indicate that unique constraint voliation in database occurred.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 27.11.12 13:37
 * @author marcel
 */
public class UniqueConstraintException extends DatabaseAdapterException {

    /**
     * Creates new object without message.
     */
    public UniqueConstraintException() {
    }

    /**
     * Creates new object with a message.
     * @param message Message describing what happend.
     */
    public UniqueConstraintException(String message) {
        super(message);
    }

    /**
     * Creates new object with a message and cause exception.
     * @param message Message describing what happend.
     * @param cause Cause exception.
     */
    public UniqueConstraintException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates new object with cause exception.
     * @param cause Cause exception.
     */
    public UniqueConstraintException(Throwable cause) {
        super(cause);
    }



}
