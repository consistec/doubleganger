package de.consistec.syncframework.common;

/*
 * #%L
 * Project - doppelganger
 * File - SyncSettings.java
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

import java.util.Set;

/**
 * This class represents client settings which are validated on server side.
 *
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 15.01.13 14:46
 */
public class SyncSettings {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private Set<String> syncTables;
    private TableSyncStrategies strategies;

//</editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class constructors " >

    /**
     * The constructor of the class SyncSettings.
     *
     * @param syncTables tables which the client wants to sync
     * @param strategies the table sync strategies of the client
     */
    public SyncSettings(final Set<String> syncTables, final TableSyncStrategies strategies) {
        this.syncTables = syncTables;
        this.strategies = strategies;
    }
//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class methods " >

    /**
     * returns tables which the client wants to sync.
     *
     * @return set of table names
     */
    public Set<String> getSyncTables() {
        return syncTables;
    }

    /**
     * returns the table sync strategy from client for the passed table.
     *
     * @param table table name
     * @return table sync strategy for the passed table name
     */
    public TableSyncStrategy getStrategy(String table) {
        return strategies.getSyncStrategyForTable(table);
    }

    //</editor-fold>

}
