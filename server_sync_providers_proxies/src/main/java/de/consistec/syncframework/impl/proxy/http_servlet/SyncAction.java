package de.consistec.syncframework.impl.proxy.http_servlet;

/*
 * #%L
 * Project - doppelganger
 * File - SyncAction.java
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
 * Available actions to invoke on synchronization Servlet.
 * <p/>
 *
 * @author Piotr Wieczorek
 * @company consistec Engineering and Consulting GmbH
 * @date 02.11.2012 11:10:39
 * @since 0.0.1-SNAPSHOT
 */
public enum SyncAction {

    /**
     * Corresponds with {@link de.consistec.syncframework.common.server.IServerSyncProvider#getSchema() } method of
     * server provider.
     */
    GET_SCHEMA("getschema"),
    /**
     * Corresponds with {@link de.consistec.syncframework.common.server.IServerSyncProvider#getChanges(int) } method of
     * server provider.
     */
    GET_CHANGES("getchanges"),
    /**
     * Corresponds with
     * {@link de.consistec.syncframework.common.server.IServerSyncProvider#applyChanges(java.util.List, int) }
     * method of
     * server provider.
     */
    APPLY_CHANGES("applychanges"),
    /**
     * Corresponds with
     * {@link de.consistec.syncframework.common.server.IServerSyncProvider#validate(
     *de.consistec.syncframework.common.SyncSettings)}
     * method of
     * server provider.
     */
    VALIDATE_SETTINGS("validate");

    private String name;

    private SyncAction(String stringName) {
        name = stringName;
    }

    /**
     * Return the name of action as it should be written into the request.
     *
     * @return Action name is it is written in request header.
     */
    public String getStringName() {
        return name;
    }

    /**
     * Produces SyncAction instances.
     * <p/>
     *
     * @param stringName Action name as it is used in http request.
     * @return Instance of SyncAction corresponding to given <i>stringName</i>
     */
    public static SyncAction fromStringName(String stringName) {
        for (SyncAction val : SyncAction.values()) {
            if (val.getStringName().equalsIgnoreCase(stringName)) {
                return val;
            }
        }
        return null;
    }
}
