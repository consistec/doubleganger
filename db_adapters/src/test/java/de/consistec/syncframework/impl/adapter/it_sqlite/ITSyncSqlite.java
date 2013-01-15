package de.consistec.syncframework.impl.adapter.it_sqlite;

import de.consistec.syncframework.impl.SynchronizationIT;
import de.consistec.syncframework.impl.TestDatabase;
import de.consistec.syncframework.impl.TestScenario;
import de.consistec.syncframework.impl.adapter.DumpDataSource;

/**
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 15.01.2013 10:24:40
 * @author davidm
 * @since
 */
public class ITSyncSqlite extends SynchronizationIT {

    public ITSyncSqlite(TestScenario scenario) {
        super(scenario);
        db = new TestDatabase("/config_sqlite.properties", DumpDataSource.SupportedDatabases.SQLITE);
    }
}
