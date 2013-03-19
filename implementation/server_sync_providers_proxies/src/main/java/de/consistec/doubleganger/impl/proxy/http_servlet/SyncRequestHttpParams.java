package de.consistec.doubleganger.impl.proxy.http_servlet;

/*
 * #%L
 * Project - doppelganger
 * File - SyncRequestHttpParams.java
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
 * Enumeration of Http request parameters to be send with synchronization request.
 *
 * @author Piotr wieczorek
 * @company consistec Engineering and Consulting GmbH
 * @date 19.11.2012 16:38:29
 * @since 0.0.1-SNAPSHOT
 */
public enum SyncRequestHttpParams {
    /**
     * Header which holds the thread id of the client.
     */
    THREAD_ID,
    /**
     * Header which holds the action name of the server provider.
     * The value for this header will be name value of one of {@link SyncAction} entries .
     */
    ACTION,
    /**
     * Header which hold clients revision.
     */
    REVISION,
    /**
     * Header which holds the JSON String with the data.
     */
    CHANGES,
    /**
     * SyncSettings from client or server.
     */
    SETTINGS;
}
