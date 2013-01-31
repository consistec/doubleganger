package de.consistec.syncframework.common.server;

/*
 * #%L
 * Project - doppelganger
 * File - IServerSyncProvider.java
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

import de.consistec.syncframework.common.SyncData;
import de.consistec.syncframework.common.SyncSettings;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.exception.SyncException;

/**
 * This interface defines behavior which must be implemented by classes which should act as a proxy
 * between client and server side synchronization.
 * If you want to communicate with the remote server, you have to implement this interface in a class,
 * that will be then specified in framework configuration as proxy provider class
 * {@link de.consistec.syncframework.common.Config#setServerProxy(java.lang.Class)}
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public interface IServerSyncProvider {

    /**
     * Apply changes.
     *
     * @param clientData Client changes data.
     * @return New revision
     * @throws SyncException the sync exception
     */
    int applyChanges(SyncData clientData) throws SyncException;

    /**
     * Gets the changes.
     *
     * @param rev the rev
     * @return the changes
     * @throws SyncException the sync exception
     */
    SyncData getChanges(int rev) throws SyncException;

    /**
     * Returns the server schema.
     * Schema should consists only from monitored data tables (no md tables);
     *
     * @return the schema
     * @throws SyncException the sync exception
     */
    Schema getSchema() throws SyncException;

    /**
     * Validates the passed client settings and throws a SyncException if necessary.
     *
     * @param clientSettings the settings of the client to validate
     * @throws SyncException thrown if validation failes
     */
    void validate(SyncSettings clientSettings) throws SyncException;
}
