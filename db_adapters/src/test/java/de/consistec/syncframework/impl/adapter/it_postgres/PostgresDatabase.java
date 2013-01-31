package de.consistec.syncframework.impl.adapter.it_postgres;

import de.consistec.syncframework.impl.TestDatabase;
import de.consistec.syncframework.impl.adapter.DumpDataSource;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 21.01.13 11:07
 */
public class PostgresDatabase extends TestDatabase {

    private static final String CONFIG_FILE = "/config_postgre.properties";

    public PostgresDatabase() {
        super(CONFIG_FILE, DumpDataSource.SupportedDatabases.POSTGRESQL);
    }
}
