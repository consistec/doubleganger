package de.consistec.syncframework.impl.adapter.it_sqlite;

import de.consistec.syncframework.impl.TestDatabase;
import de.consistec.syncframework.impl.adapter.DumpDataSource;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 30.01.13 15:14
 */
public class SqlLiteDatabase extends TestDatabase {

    private static final String CONFIG_FILE = "/config_sqlite.properties";

    public SqlLiteDatabase() {
        super(CONFIG_FILE, DumpDataSource.SupportedDatabases.SQLITE);
    }
}
