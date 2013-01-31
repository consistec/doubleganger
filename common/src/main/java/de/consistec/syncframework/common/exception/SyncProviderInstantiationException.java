package de.consistec.syncframework.common.exception;

/*
 * #%L
 * Project - doppelganger
 * File - SyncProviderInstantiationException.java
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
 * Indicates problem with instantiation of server provider object.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 30.10.2012 12:40
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
public class SyncProviderInstantiationException extends Exception {

    /**
     *
     */
    public SyncProviderInstantiationException() {
    }

    /**
     *
     * @param message Error message
     */
    public SyncProviderInstantiationException(String message) {
        super(message);
    }

    /**
     *
     * @param message Error message
     * @param cause Error cause
     */
    public SyncProviderInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     * @param cause Error cause
     */
    public SyncProviderInstantiationException(Throwable cause) {
        super(cause);
    }
}
