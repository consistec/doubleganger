package de.consistec.doubleganger.common.exception.database_adapter;

/*
 * #%L
 * Project - doubleganger
 * File - DatabaseAdapterClosingException.java
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
 * Exception that indicates problem with closing database adapter resources (mainly the connection).
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 24.10.12 13:36
 * @author marcel
 * @since 0.0.1-SNAPSHOT
 */
public class DatabaseAdapterClosingException extends DatabaseAdapterException {

    /**
     *
     */
    public DatabaseAdapterClosingException() {
    }

    /**
     *
     * @param message Error message
     */
    public DatabaseAdapterClosingException(String message) {
        super(message);
    }

    /**
     *
     * @param msg Error message
     * @param th Error cause
     */
    public DatabaseAdapterClosingException(String msg, Throwable th) {
        super(msg, th);
    }

    /**
     *
     * @param cause Error cause
     */
    public DatabaseAdapterClosingException(Throwable cause) {
        super(cause);
    }

}
