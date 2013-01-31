package de.consistec.syncframework.common;

import java.util.Set;

/**
 * This class represents client settings which are validated on server side.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
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
