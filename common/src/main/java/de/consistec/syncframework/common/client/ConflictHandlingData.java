package de.consistec.syncframework.common.client;

/*
 * #%L
 * Project - doppelganger
 * File - ConflictHandlingData.java
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
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.MDEntry;

import java.util.Map;

/**
 * The class {@code ConflictHandlingData} contains client data values of any row and the row
 * from the server changeset with the same primary key which are necessary for the conflict handling.
 *
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 12.12.12 13:39
 */
public class ConflictHandlingData {

    private Change remoteChange;
    private Change clientChange;

    /**
     * Constructor of the class {@code ConflictHandlingData}.
     *
     * @param remoteChange - server changeset from server with the same primary key as the client data row
     */
    public ConflictHandlingData(Change remoteChange) {
        this.remoteChange = remoteChange;
    }

    /**
     * Constructor of the class {@code ConflictHandlingData}.
     *
     * @param clientChange - client change from client with the same primary key as the server data row
     * @param serverChange - server change from server with the same primary key as the client data row
     */
    public ConflictHandlingData(Change clientChange, Change serverChange) {
        this.clientChange = clientChange;
        this.remoteChange = serverChange;
    }

    /**
     * returns the remote entry of the data row from the server
     * with the same primary key as the client data row has.
     *
     * @return remote entry from server
     */
    public MDEntry getRemoteEntry() {
        return remoteChange.getMdEntry();
    }

    /**
     * returns the change of type {@code Change} from the server changeset
     * with the same primary key as the client data row has.
     *
     * @return the change from server changeset
     */
    public Change getRemoteChange() {
        return remoteChange;
    }

    /**
     * returns the remote entry of the data row from the server
     * with the same primary key as the client data row has.
     *
     * @return remote entry from server
     */
    public MDEntry getLocalEntry() {
        return clientChange.getMdEntry();
    }

    /**
     * returns the client data values as map where the key is the column name and the value the data value is.
     *
     * @return data values of client
     */
    public Map<String, Object> getLocalData() {
        return clientChange.getRowData();
    }
}
