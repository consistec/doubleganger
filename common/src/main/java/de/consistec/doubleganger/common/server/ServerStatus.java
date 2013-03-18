package de.consistec.doubleganger.common.server;

/*
 * #%L
 * Project - doppelganger
 * File - ServerStatus.java
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

import static de.consistec.doubleganger.common.i18n.MessageReader.read;

import de.consistec.doubleganger.common.i18n.Errors;

/**
 * Enumerations of server statuses.
 * This status is set from the server if one of the following exceptions are thrown:
 * <br/>
 * <p/>
 * <ul>
 * <li>CLIENT_NOT_UPTODATE =>
 * {@link de.consistec.doubleganger.common.exception.ServerStatusException ServerStatusException},</li>
 * <li>TRANSACTION_ABORTED =>
 * {@link de.consistec.doubleganger.common.exception.database_adapter.TransactionAbortedException
 * TransactionAbortedException},</li>
 * <li>ENTRY_NOT_UNIQUE =>
 * {@link de.consistec.doubleganger.common.exception.database_adapter.UniqueConstraintException
 * UniqueConstraintException}</li>
 * </ul>
 * <br/>
 * <p/>
 * <p>This status should be transferred to the client so that the client can difference between
 * the several Internal Server Errors.</p>
 *
 * @author Marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 08.10.12 16:52
 * @since 0.0.1-SNAPSHOT
 */
public enum ServerStatus {

    /**
     * The enumeration {@code CLIENT_NOT_UPTODATE} represents the thrown
     * <code>ServerStatusException</code>.
     */
    CLIENT_NOT_UPTODATE(1),
    /**
     * The enumeration {@code TRANSACTION_ABORTED} represents the thrown
     * <code>TransactionAborted</code>.
     */
    TRANSACTION_ABORTED(2),
    /**
     * The enumeration {@code ENTRY_NOT_UNIQUE} represents the thrown
     * <code>UniqueConstraints</code>.
     */
    ENTRY_NOT_UNIQUE(3);
    private int code;

    private ServerStatus(int code) {
        this.code = code;
    }

    /**
     * Returns numeric status code.
     *
     * @return Numeric code.
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code Status code
     * @return ServerStatus enumeration value.
     */
    public static ServerStatus fromCode(int code) {
        for (ServerStatus value : ServerStatus.values()) {
            if (code == value.getCode()) {
                return value;
            }
        }
        throw new IllegalArgumentException(read(Errors.COMMON_UNKNOWN_SERVER_STATUS_CODE));
    }
}
