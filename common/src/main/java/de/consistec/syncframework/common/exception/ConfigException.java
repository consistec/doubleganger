package de.consistec.syncframework.common.exception;

/*
 * #%L
 * Project - doppelganger
 * File - ConfigException.java
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
 * Exception to throw when framework configuration is broken.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 15.10.2012 14:11
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
public class ConfigException extends RuntimeException {

    /**
     *
     */
    public ConfigException() {
    }

    /**
     *
     * @param message Error message
     */
    public ConfigException(String message) {
        super(message);
    }

    /**
     *
     * @param message Error message
     * @param cause Error cause
     */
    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     * @param cause Error cause
     */
    public ConfigException(Throwable cause) {
        super(cause);
    }
}
