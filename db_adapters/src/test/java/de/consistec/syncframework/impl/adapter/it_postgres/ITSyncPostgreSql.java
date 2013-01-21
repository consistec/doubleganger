package de.consistec.syncframework.impl.adapter.it_postgres;

import de.consistec.syncframework.impl.SynchronizationIT;
import de.consistec.syncframework.impl.TestScenario;

/**
 * @author davidm
 * @company Consistec Engineering and Consulting GmbH
 * @date 15.01.2013 10:18:59
 */
public class ITSyncPostgreSql extends SynchronizationIT {

    public ITSyncPostgreSql(TestScenario scenario) {
        super(scenario);
        db = new PostgresDatabase();
    }
}
