package de.consistec.doubleganger.impl.adapter;

/*
 * #%L
 * Project - doppelganger
 * File - DummyDataSource.java
 * %%
 * Copyright (C) 2011 - 2013 consistec GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.DB_NAME_REGEXP;
import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.HOST_REGEXP;
import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.PORT_REGEXP;
import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.PROPS_DB_NAME;
import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.PROPS_DRIVER_NAME;
import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.PROPS_EXTERN_PASSWORD;
import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.PROPS_EXTERN_USERNAME;
import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.PROPS_HOST;
import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.PROPS_PORT;
import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.PROPS_SYNC_PASSWORD;
import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.PROPS_SYNC_USERNAME;
import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.PROPS_URL;
import static de.consistec.doubleganger.common.i18n.MessageReader.read;
import static de.consistec.doubleganger.common.util.CollectionsUtil.newArrayList;
import static de.consistec.doubleganger.common.util.PropertiesUtil.readString;
import static de.consistec.doubleganger.common.util.StringUtil.isNullOrEmpty;

import de.consistec.doubleganger.common.ConfigConstants;
import de.consistec.doubleganger.common.adapter.DatabaseAdapterFactory;
import de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector;
import de.consistec.doubleganger.common.i18n.DBAdapterErrors;
import de.consistec.doubleganger.common.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.powermock.reflect.Whitebox;

/**
 * Imitates {@link javax.sql.DataSource DataSource} object.
 * <p>This class provides only implementation for {@link javax.sql.DataSource#getConnection() getConnection()} method.
 * All others will throw {@link UnsupportedOperationException} </p>
 *
 * @author Piotr Wieczorek
 * @company consistec Engineering and Consulting GmbH
 * @date 10.12.2012 16:11:34
 * @since 0.0.1-SNAPSHOT
 */
public class DummyDataSource implements DataSource {

    /**
     * SQLite database property file.
     * Value: {@value}
     */
    public static final String SQLITE_CONFIG_FILE = "/config_sqlite.properties";

    private SupportedDatabases dbType;
    private DatabaseAdapterFactory.AdapterPurpose conType;
    private List<Connection> createdConnections;
    private String propertiesPrefix;
    private Properties properties;

    public DummyDataSource(SupportedDatabases dbType, DatabaseAdapterFactory.AdapterPurpose conType) {
        this.dbType = dbType;
        this.conType = conType;
        createdConnections = newArrayList();
        try {
            if (conType == DatabaseAdapterFactory.AdapterPurpose.CLIENT) {
                propertiesPrefix = String.valueOf(
                    Whitebox.getField(ConfigConstants.class, "OPTIONS_COMMON_CLIENT_DB_ADAP_GROUP").get(null));
            } else {
                propertiesPrefix = String.valueOf(
                    Whitebox.getField(ConfigConstants.class, "OPTIONS_COMMON_SERV_DB_ADAP_GROUP").get(null));
            }

            propertiesPrefix += ".";
            readConfig();
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public SupportedDatabases getDbType() {
        return dbType;
    }

    public DatabaseAdapterFactory.AdapterPurpose getConType() {
        return conType;
    }

    private Connection create(String username, String password) throws Exception {

        Connection con = null;
        switch (dbType) {
            case MYSQL:
                con = createMySql(username, password);
                break;
            case POSTGRESQL:
                con = createPostgres(username, password);
                break;
            case SQLITE:
                con = createSqlLite(username, password);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported database");
        }

        createdConnections.add(con);
        return con;

    }

    private Connection createPostgres(String username, String password) throws Exception {
        String driver = readString(properties, propertiesPrefix + PROPS_DRIVER_NAME, false);
        if (isNullOrEmpty(driver)) {
            driver = "org.postgresql.Driver";
        }
        String url = readString(properties, propertiesPrefix + PROPS_URL, false);

        if (isNullOrEmpty(url)) {
            String host = readString(properties, propertiesPrefix + PROPS_HOST, true);
            String dbName = readString(properties, propertiesPrefix + PROPS_DB_NAME, true);
            String tmpPort = readString(properties, propertiesPrefix + PROPS_PORT, false);

            DatabaseAdapterConnector connector = new DatabaseAdapterConnector(PostgresDatabaseAdapter.DEFAULT_DRIVER,
                PostgresDatabaseAdapter.DEFAULT_PORT);
            url = createUrl(PostgresDatabaseAdapter.DEFAULT_PORT, host, tmpPort, dbName,
                PostgresDatabaseAdapter.URL_PATTERN_PREFIX);
        }
        return buildConnection(driver, url, username, password);
    }


    private Connection createMySql(String username, String password) throws Exception {
        String driver = readString(properties, propertiesPrefix + PROPS_DRIVER_NAME, false);
        if (isNullOrEmpty(driver)) {
            driver = "com.mysql.jdbc.Driver";
        }
        String url = readString(properties, propertiesPrefix + PROPS_URL, false);

        if (isNullOrEmpty(url)) {
            String host = readString(properties, propertiesPrefix + PROPS_HOST, true);
            String dbName = readString(properties, propertiesPrefix + PROPS_DB_NAME, true);
            String tmpPort = readString(properties, propertiesPrefix + PROPS_PORT, false);

            DatabaseAdapterConnector connector = new DatabaseAdapterConnector();
            url = createUrl(MySqlDatabaseAdapter.DEFAULT_PORT, host, tmpPort, dbName,
                MySqlDatabaseAdapter.URL_PATTERN_PREFIX);
        }
        return buildConnection(driver, url, username, password);
    }

    private Connection createSqlLite(String username, String password) throws IOException, ClassNotFoundException,
        SQLException {
        return createGeneric(username, password);
    }

    /**
     * Creates jdbc url string for any database.
     * <p/>
     *
     * @param urlPatternPrefix - prefix for the url pattern (example: jdbc:mysql://)
     * @return Jdbc url string for postgreSQL driver.
     */
    private String createUrl(Integer defaultPort, String host, String port, String databaseName, String urlPatternPrefix
    ) {  //NOSONAR

        if (StringUtil.isNullOrEmpty(host)) {
            throw new IllegalArgumentException(read(DBAdapterErrors.HOSTNAME_IS_EMPTY));
        }
        if (StringUtil.isNullOrEmpty(databaseName)) {
            throw new IllegalArgumentException(read(DBAdapterErrors.DATABASE_NAME_EMPTY));
        }

        String urlPattern = urlPatternPrefix + HOST_REGEXP + ":" + PORT_REGEXP + "/" + DB_NAME_REGEXP;
        String result = urlPattern.replaceAll(HOST_REGEXP, host);
        result = result.replaceAll(PORT_REGEXP, String.valueOf((port == null) ? defaultPort : port));
        result = result.replaceAll(DB_NAME_REGEXP, databaseName);

        return result;
    }

    private Connection createGeneric(String username, String password) throws ClassNotFoundException,
        SQLException {
        String dbDriver = readString(properties, propertiesPrefix + PROPS_DRIVER_NAME, true);
        String dbUrl = readString(properties, propertiesPrefix + PROPS_URL, true);
        return buildConnection(dbDriver, dbUrl, username, password);
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

    public String getSyncUserName() {
        return readString(properties, propertiesPrefix + PROPS_SYNC_USERNAME, false);
    }

    public String getSyncUserPassword() {
        return readString(properties, propertiesPrefix + PROPS_SYNC_PASSWORD, false);
    }

    public String getExternUserName() {
        return readString(properties, propertiesPrefix + PROPS_EXTERN_USERNAME, false);
    }

    public String getExternUserPassword() {
        return readString(properties, propertiesPrefix + PROPS_EXTERN_PASSWORD, false);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(getSyncUserName(), getSyncUserPassword());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        try {
            return create(username, password);
        } catch (Exception ex) {
            throw new SQLException("Can't read connection data", ex);
        }
    }

    private void readConfig() throws IOException {
        String filePath;
        switch (dbType) {
            case MYSQL:
                filePath = MySqlDatabaseAdapter.MYSQL_CONFIG_FILE;
                break;
            case POSTGRESQL:
                filePath = PostgresDatabaseAdapter.POSTGRE_CONFIG_FILE;
                break;
            case SQLITE:
                filePath = SQLITE_CONFIG_FILE;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported database");
        }
        InputStream in = getClass().getResourceAsStream(filePath);
        properties = new Properties();
        properties.load(in);
    }

    /**
     * @return Unmodifiable list of created connections.
     */
    public List<Connection> getCreatedConnections() {
        return Collections.unmodifiableList(createdConnections);
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
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

    public enum SupportedDatabases {

        POSTGRESQL, MYSQL, SQLITE;
    }

    ;
}
