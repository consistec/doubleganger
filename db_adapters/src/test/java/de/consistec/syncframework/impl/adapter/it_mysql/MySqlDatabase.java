package de.consistec.syncframework.impl.adapter.it_mysql;

import de.consistec.syncframework.impl.TestDatabase;
import de.consistec.syncframework.impl.adapter.DumpDataSource;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 30.01.13 15:13
 */
public class MySqlDatabase extends TestDatabase {

    private static final String CONFIG_FILE = "/config_mysql.properties";

    public MySqlDatabase() {
        super(CONFIG_FILE, DumpDataSource.SupportedDatabases.MYSQL);
    }
}
