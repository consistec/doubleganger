package de.consistec.doubleganger.common.exception;

/*
 * #%L
 * Project - doubleganger
 * File - SerializationException.java
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
 * This exception indicates errors with serialization/deserialization of data to be send/receive
 * from remote server provider.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 12.04.12 10:21
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 *
 */
public class SerializationException extends Exception {

    /**
     *
     * @param message Error message
     */
    public SerializationException(String message) {
        super(message);
    }

    /**
     *
     */
    public SerializationException() {
        super();
    }

    /**
     *
     * @param message Error message
     * @param th Error cause
     */
    public SerializationException(String message, Throwable th) {
        super(message, th);
    }

    /**
     *
     * @param th Error cause
     */
    public SerializationException(Throwable th) {
        super(th);
    }
}
