package de.consistec.syncframework.impl.adapter.it_sqlite;

import de.consistec.syncframework.impl.SynchronizationIT;
import de.consistec.syncframework.impl.TestScenario;

/**
 * @author davidm
 * @company Consistec Engineering and Consulting GmbH
 * @date 15.01.2013 10:24:40
 */
public class ITSyncSqlite extends SynchronizationIT {

    public ITSyncSqlite(TestScenario scenario) {
        super(scenario);
        db = new SqlLiteDatabase();
    }
}
