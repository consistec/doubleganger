package de.consistec.doubleganger.common;

/*
 * #%L
 * Project - doppelganger
 * File - ISyncProgressListener.java
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
 * The listener interface for receiving ISyncProgress events.
 * The class that is interested in processing a ISyncProgress event implements this interface, and the object created
 * with that class is registered with a component using the component's
 * <code>addISyncProgressListener</code> method. <br/>
 * When the ISyncProgress event occurs, then object's appropriate method is invoked.
 *
 * @company consistec Engineering and Consulting GmbH
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public interface ISyncProgressListener {

    /**
     * Progress update.
     *
     * @param message the message
     */
    void progressUpdate(String message);

    /**
     * Sync finished.
     */
    void syncFinished();
}
