package de.consistec.syncframework.common.exception;

/*
 * #%L
 * Project - doppelganger
 * File - SyncException.java
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
 * This exception indicates general problem with synchronization.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date unknown
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class SyncException extends Exception {

    /**
     * Instantiates a new sync exception.
     */
    public SyncException() {
        super();
    }

    /**
     * Instantiates a new sync exception.
     *
     * @param message the message
     */
    public SyncException(String message) {
        super(message);
    }

    /**
     * Instantiates a new sync exception.
     *
     * @param message the message
     * @param th the throwable
     */
    public SyncException(String message, Throwable th) {
        super(message, th);
    }

    /**
     * Instantiates a new sync exception.
     * <p/>
     * @param cause the cause
     */
    public SyncException(Throwable cause) {
        super(cause);
    }
}
