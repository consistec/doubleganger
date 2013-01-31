package de.consistec.syncframework.impl.adapter.it_mysql;


import de.consistec.syncframework.impl.SynchronizationIT;
import de.consistec.syncframework.impl.TestScenario;

/**
 * @author davidm
 * @company Consistec Engineering and Consulting GmbH
 * @date 15.01.2013 10:23:35
 */
public class ITSyncMySql extends SynchronizationIT {

    public ITSyncMySql(TestScenario scenario) {
        super(scenario);
        db = new MySqlDatabase();
    }
}
