package de.consistec.doubleganger.common;

/*
 * #%L
 * Project - doppelganger
 * File - IConflictListener.java
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

import de.consistec.doubleganger.common.data.ResolvedChange;

import java.util.Map;

/**
 * The listener interface for receiving IConflict events.
 * The class that is interested in processing a IConflict event
 * implements this interface, and the object created with that class is registered with a component using the
 * component's
 * <code>addIConflictListener</code> method. When
 * the IConflict event occurs, that object's appropriate
 * method is invoked.
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date 03.07.12 11:36
 * @since 0.0.1-SNAPSHOT
 */
public interface IConflictListener {

    /**
     * Resolves the merge conflict.
     * <p/>
     *
     * @param serverData the server data
     * @param clientData the client data
     * @return data after client has resolved the conflict
     */
    ResolvedChange resolve(Map<String, Object> serverData, Map<String, Object> clientData);
}
