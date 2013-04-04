package de.consistec.doubleganger.common.exception.database_adapter;

/*
 * #%L
 * Project - doubleganger
 * File - TransactionAbortedException.java
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
 * Exception to indicate that a transaction aborted exception in database occurred.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 10.10.12 11:14
 * @author marcel
 * @since 0.0.1-SNAPSHOT
 */
public class TransactionAbortedException extends DatabaseAdapterException {

    /**
     *
     */
    public TransactionAbortedException() {
    }

    /**
     *
     * @param message Error message
     */
    public TransactionAbortedException(String message) {
        super(message);
    }

    /**
     *
     * @param message Error message
     * @param th Exception cause.
     */
    public TransactionAbortedException(String message, Throwable th) {
        super(message, th);
    }

    /**
     *
     * @param cause Exception cause.
     */
    public TransactionAbortedException(Throwable cause) {
        super(cause);
    }
}
