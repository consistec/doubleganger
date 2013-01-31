package de.consistec.syncframework.impl.adapter;

/*
 * #%L
 * Project - doppelganger
 * File - ConnectionType.java
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
 * Connection types.
 * This enumeration helps to distinguish purpose of the connection.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 26.10.12 17:19
 * @author Marcel
 * @since 0.0.1-SNAPSHOT
 */
public enum ConnectionType {

    /**
     * For server database.
     */
    SERVER,
    /**
     * For client database.
     */
    CLIENT
}
