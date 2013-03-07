package de.consistec.syncframework.impl.adapter;

import de.consistec.syncframework.impl.TestDatabase;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.io.IOException;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.postgresql.jdbc2.optional.PoolingDataSource;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 07.02.13 11:14
 */
public class PooledTestDatabase extends TestDatabase {

    private DataSource pooledClientDataSource;

    public PooledTestDatabase(TestDatabase db
    ) {
        super(db.getConfigFile(), db.getSupportedDb());
    }

    public void initPooledDB() throws SQLException, IOException {
        if (getSupportedDb() == DumpDataSource.SupportedDatabases.POSTGRESQL) {
            pooledClientDataSource = createPostgresDataSource();
        } else if (getSupportedDb() == DumpDataSource.SupportedDatabases.MYSQL) {
            pooledClientDataSource = createMySqlDataSource();
        }
    }

    public DataSource getPooledClientDataSource() {
        return this.pooledClientDataSource;
    }

    private DataSource createPostgresDataSource() {
        PoolingDataSource source = new PoolingDataSource();
        source.setDataSourceName("datasource for pooling db connections");
        source.setServerName("localhost");
        source.setPortNumber(5432);
        source.setDatabaseName("client");
        source.setUser("syncuser");
        source.setPassword("syncuser");
        source.setMaxConnections(10);

        return source;
    }

    private DataSource createMySqlDataSource() {
        MysqlConnectionPoolDataSource source = new MysqlConnectionPoolDataSource();
        source.setServerName("localhost");
        source.setPortNumber(3306);
        source.setDatabaseName("client");
        source.setUser("mysql");
        source.setPassword("mysql");

        return source;
    }
}
