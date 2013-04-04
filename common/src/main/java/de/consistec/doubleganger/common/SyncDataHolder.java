package de.consistec.doubleganger.common;

/*
 * #%L
 * Project - doubleganger
 * File - SyncDataHolder.java
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
 * Container for sync data values. This container contains the client and server datas.
 *
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 23.01.13 08:38
 */
public class SyncDataHolder {

    private SyncData clientSyncData;
    private SyncData serverSyncData;


    /**
     * Constructor for this container.
     *
     * @param clientSyncData data values from client
     * @param serverSyncData data values from server
     */
    public SyncDataHolder(final SyncData clientSyncData, final SyncData serverSyncData) {
        this.clientSyncData = clientSyncData;
        this.serverSyncData = serverSyncData;
    }

    /**
     * Returns the data values from client.
     *
     * @return client data values
     */
    public SyncData getClientSyncData() {
        return clientSyncData;
    }

    /**
     * Returns the data values from server.
     *
     * @return server data values
     */
    public SyncData getServerSyncData() {
        return serverSyncData;
    }
}
