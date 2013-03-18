package de.consistec.doubleganger.common.exception.database_adapter;

/*
 * #%L
 * Project - doppelganger
 * File - DatabaseAdapterInstantiationException.java
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
 * Exception to indicate a problem during database adapter instantiation.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 10.07.12 10:33
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 * <p/>
 */
public class DatabaseAdapterInstantiationException extends DatabaseAdapterException {

    /**
     *
     */
    public DatabaseAdapterInstantiationException() {
        super();
    }

    /**
     *
     * @param message Error message
     */
    public DatabaseAdapterInstantiationException(String message) {
        super(message);
    }

    /**
     *
     * @param message Error message
     * @param cause Error cause
     */
    public DatabaseAdapterInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     * @param cause Error cause
     */
    public DatabaseAdapterInstantiationException(Throwable cause) {
        super(cause);
    }
}
