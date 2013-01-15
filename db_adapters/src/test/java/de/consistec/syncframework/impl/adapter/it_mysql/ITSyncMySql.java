package de.consistec.syncframework.impl.adapter.it_mysql;


import de.consistec.syncframework.impl.SynchronizationIT;
import de.consistec.syncframework.impl.TestDatabase;
import de.consistec.syncframework.impl.TestScenario;
import de.consistec.syncframework.impl.adapter.DumpDataSource;

/**
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 15.01.2013 10:23:35
 * @author davidm
 * @since
 */
public class ITSyncMySql extends SynchronizationIT {

    public ITSyncMySql(TestScenario scenario) {
        super(scenario);
        db = new TestDatabase("/config_mysql.properties", DumpDataSource.SupportedDatabases.MYSQL);
    }
}
