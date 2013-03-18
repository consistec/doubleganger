package de.consistec.doubleganger.common.exception;

/*
 * #%L
 * Project - doppelganger
 * File - ServerStatusException.java
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

import de.consistec.doubleganger.common.server.ServerStatus;

/**
 * This exception indicates invalid server status.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 10.10.12 11:22
 * @author marcel
 * @since 0.0.1-SNAPSHOT
 */
public class ServerStatusException extends SyncException {

    private ServerStatus status;

    /**
     *
     */
    public ServerStatusException() {
    }

    /**
     *
     * @param status Server status
     */
    public ServerStatusException(ServerStatus status) {
        this.status = status;
    }

    /**
     *
     * @param status Server status
     * @param msg Error message
     */
    public ServerStatusException(ServerStatus status, String msg) {
        super(msg);
        this.status = status;
    }

    /**
     *
     * @param message Error message
     */
    public ServerStatusException(String message) {
        super(message);
    }

    /**
     *
     * @param message Error message
     * @param cause The cause
     */
    public ServerStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     * @param status Server status
     * @param message Error message
     * @param cause The cause
     */
    public ServerStatusException(ServerStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    /**
     *
     * @param cause Error message
     */
    public ServerStatusException(Throwable cause) {
        super(cause);
    }

    /**
     *
     * @param status Server status
     * @param cause The cause
     */
    public ServerStatusException(ServerStatus status, Throwable cause) {
        super(cause);
        this.status = status;
    }

    /**
     *
     * @return The status of the server.
     */
    public ServerStatus getStatus() {
        return status;
    }

}
