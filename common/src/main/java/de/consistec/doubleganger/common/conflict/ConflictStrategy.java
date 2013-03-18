package de.consistec.doubleganger.common.conflict;

/*
 * #%L
 * Project - doppelganger
 * File - ConflictStrategy.java
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
 * This enumeration represents action to undertake in case of merge conflict.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date unknown
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public enum ConflictStrategy {

    /**
     * The Server wins.
     */
    SERVER_WINS,
    /**
     * The Client wins.
     */
    CLIENT_WINS,
    /**
     * Application throws an Event.
     */
    FIRE_EVENT;
}
