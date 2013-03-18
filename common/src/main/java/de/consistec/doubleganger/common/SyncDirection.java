package de.consistec.doubleganger.common;

/*
 * #%L
 * Project - doppelganger
 * File - SyncDirection.java
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
 * Enumeration with possibles synchronization directions.
 * <p/>
 * Synchronization direction specifies which site of synchronization has priority over the other site.
 *
 * @author Marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 30.10.12 15:46
 * @since 0.0.1-SNAPSHOT
 */
public enum SyncDirection {

    /**
     * Only from client to server.
     */
    CLIENT_TO_SERVER,
    /**
     * Only from server to client.
     */
    SERVER_TO_CLIENT,
    /**
     * both directions, client to server and server to client.
     */
    BIDIRECTIONAL;
}
