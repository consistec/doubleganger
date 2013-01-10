package de.consistec.syncframework.impl.adapter;

import static de.consistec.syncframework.common.util.CollectionsUtil.newArrayList;
import static de.consistec.syncframework.common.util.PropertiesUtil.readString;
import static de.consistec.syncframework.common.util.StringUtil.isNullOrEmpty;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.util.StringUtil;
import de.consistec.syncframework.impl.adapter.it_mysql.ITAllOperationsMySQL;
import de.consistec.syncframework.impl.adapter.it_postgres.ITAllOperationsPostgreSQLExternalConnection;
import de.consistec.syncframework.impl.adapter.it_sqlite.ITAllOperationsSQLite;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.powermock.reflect.Whitebox;

/**
 * Imitates {@link javax.sql.DataSource DataSource} object.
 * <p>This class provides only implementation for {@link javax.sql.DataSource#getConnection() getConnection()} method.
 * All others will throw {@link UnsupportedOperationException} </p>
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 10.12.2012 16:11:34
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
public class DumpDataSource implements DataSource {

//<editor-fold defaultstate="expanded" desc=" Class fields " >
    private SupportedDatabases dbType;
    private ConnectionType conType;
    private List<Connection> createdConnections;
    private String propertiesPrefix;
//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class constructors " >
    public DumpDataSource(SupportedDatabases dbType, ConnectionType conType) {
        this.dbType = dbType;
        this.conType = conType;
        createdConnections = newArrayList();
        try {
            if (conType == ConnectionType.CLIENT) {

                propertiesPrefix = String.valueOf(Whitebox.getField(Config.class, "OPTIONS_COMMON_CLIENT_DB_ADAP_GROUP").get(
                    null));

            } else {
                propertiesPrefix = String.valueOf(Whitebox.getField(Config.class, "OPTIONS_COMMON_SERV_DB_ADAP_GROUP").get(
                    null));
            }

            propertiesPrefix += ".";

        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >
    public SupportedDatabases getDbType() {
        return dbType;
    }

    public ConnectionType getConType() {
        return conType;
    }
//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class methods " >
    private Connection create() throws Exception {

        Connection con = null;
        switch (dbType) {
            case MYSQL:
                con = createMySql();
                break;
            case POSTGRESQL:
                con = createPostgres();
                break;
            case SQLITE:
                con = createSqlLite();
                break;
            default:
                throw new UnsupportedOperationException("Unsupported database");
        }

        createdConnections.add(con);
        return con;

    }

    private Connection createPostgres() throws Exception {
        Properties props = readConfig(ITAllOperationsPostgreSQLExternalConnection.CONFIG_FILE);

        String driver = readString(props, propertiesPrefix + GenericDatabaseAdapter.PROPS_DRIVER_NAME, false);
        if (isNullOrEmpty(driver)) {
            driver = "org.postgresql.Driver";
        }
        String dbUser = readString(props, propertiesPrefix + PostgresDatabaseAdapter.PROPS_USERNAME, false);
        String dbPass = readString(props, propertiesPrefix + PostgresDatabaseAdapter.PROPS_PASSWORD, false);

        String url = readString(props, propertiesPrefix + GenericDatabaseAdapter.PROPS_URL, false);
        if (isNullOrEmpty(url)) {

            String host = readString(props, propertiesPrefix + PostgresDatabaseAdapter.PROPS_HOST, true);
            String dbName = readString(props, propertiesPrefix + PostgresDatabaseAdapter.PROPS_DB_NAME, true);
            String tmpPort = readString(props, propertiesPrefix + PostgresDatabaseAdapter.PROPS_PORT, false);

            url = Whitebox.<String>invokeMethod(PostgresDatabaseAdapter.class, "createUrl", host,
                StringUtil.isNullOrEmpty(tmpPort) ? null : Integer.valueOf(tmpPort), dbName);
        }

        Class.forName(driver);
        if (StringUtil.isNullOrEmpty(dbUser) || StringUtil.isNullOrEmpty(dbPass)) {
            return DriverManager.getConnection(url);
        } else {
            return DriverManager.getConnection(url, dbUser, dbPass);
        }
    }

    private Connection createMySql() throws IOException, ClassNotFoundException, SQLException {
        return createGeneric(readConfig(ITAllOperationsMySQL.CONFIG_FILE));
    }

    private Connection createSqlLite() throws IOException, ClassNotFoundException, SQLException {
        return createGeneric(readConfig(ITAllOperationsSQLite.CONFIG_FILE));
    }

    private Connection createGeneric(Properties props) throws ClassNotFoundException, SQLException {
        String dbDriver = readString(props, propertiesPrefix + GenericDatabaseAdapter.PROPS_DRIVER_NAME, true);
        String dbUrl = readString(props, propertiesPrefix + GenericDatabaseAdapter.PROPS_URL, true);
        String dbUser = readString(props, propertiesPrefix + GenericDatabaseAdapter.PROPS_USERNAME, false);
        String dbPassword = readString(props, propertiesPrefix + GenericDatabaseAdapter.PROPS_PASSWORD, false);
        return buildConnection(dbDriver, dbUrl, dbUser, dbPassword);
    }

    private Connection buildConnection(String driver, String url, String dbUser, String dbPass) throws
        ClassNotFoundException, SQLException {
        Class.forName(driver);
        if (StringUtil.isNullOrEmpty(dbUser) || StringUtil.isNullOrEmpty(dbPass)) {
            return DriverManager.getConnection(url);
        } else {
            return DriverManager.getConnection(url, dbUser, dbPass);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return create();
        } catch (Exception ex) {
            throw new SQLException("Can't read connection data", ex);
        }
    }

    private Properties readConfig(String filePath) throws IOException {

        Properties props = new Properties();
        InputStream in = getClass().getResourceAsStream(filePath);
        props.load(in);
        return props;
    }

    /**
     *
     * @return Unmodifiable list of created connections.
     */
    public List<Connection> getCreatedConnections() {
        return Collections.unmodifiableList(createdConnections);
    }

    //<editor-fold defaultstate="collapsed" desc=" -------- Not implemented methods -------- " >
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    //</editor-fold>

//</editor-fold>
    //<editor-fold defaultstate="expanded" desc=" Inner types " >
    public enum SupportedDatabases {

        POSTGRESQL, MYSQL, SQLITE;
    };
    //</editor-fold>
}
