package de.consistec.doubleganger.common.adapter.impl;

import static de.consistec.doubleganger.common.i18n.MessageReader.read;

import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.doubleganger.common.i18n.DBAdapterErrors;
import de.consistec.doubleganger.common.i18n.DBAdapterWarnings;
import de.consistec.doubleganger.common.util.PropertiesUtil;
import de.consistec.doubleganger.common.util.StringUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Connector class to create connection to specific database.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 27.03.13 14:14
 */
public class DatabaseAdapterConnector {

    /**
     * Host property name.
     */
    public static final String PROPS_HOST = "host";
    /**
     * Port property name.
     */
    public static final String PROPS_PORT = "port";
    /**
     * Database property name.
     */
    public static final String PROPS_DB_NAME = "db_name";

    /**
     * defines the regular expression for the host.
     */
    public static final String HOST_REGEXP = "H_O_S_T";
    /**
     * defines the regular expression for the port.
     */
    public static final String PORT_REGEXP = "P_O_R_T";
    /**
     * defines the regular expression for the db name.
     */
    public static final String DB_NAME_REGEXP = "D_B_N_A_M_E";

    /**
     * This option specify jdbc driver class canonical name for a database.
     * <p/>
     * Value: {@value}.
     */
    public static final String PROPS_DRIVER_NAME = "driver";
    /**
     * This option specify jdbc url for database.
     * <p/>
     * Value: {@value}.
     */
    public static final String PROPS_URL = "url";
    /**
     * This option specify the username of the database to connect to.
     * <p/>
     * Value: {@value}.
     */
    public static final String PROPS_SYNC_USERNAME = "user";
    /**
     * This option specify database user password.
     * <p/>
     * Value: {@value}.
     */
    public static final String PROPS_SYNC_PASSWORD = "password";
    /**
     * This option specifies the database username of an external user (unknown to the doubleganger).
     * <p/>
     * Value: {@value}.
     */
    public static final String PROPS_EXTERN_USERNAME = "extern.user";
    /**
     * This option specifies the database password of an external user (unknown to the doubleganger).
     * <p/>
     * Value: {@value}.
     */
    public static final String PROPS_EXTERN_PASSWORD = "extern.password";
    /**
     * This option specify the database schema to connect to.
     * <p/>
     * Value: {@value}.
     */
    public static final String PROPS_SCHEMA = "schema";

    private static final Marker FATAL_MARKER = MarkerFactory.getMarker("FATAL");

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseAdapterConnector.class.getCanonicalName());

    private Integer port;
    private String host;
    private String databaseName;

    private String defaultDriver;
    private int defaultPort;

    /**
     * Constructor of the connector to pass defaultDriver and defaultPort values.
     *
     * @param defaultDriver - the default driver if none is configured in configuration file.
     * @param defaultPort - the default port if none is configured in configuration file.s
     */
    public DatabaseAdapterConnector(String defaultDriver, int defaultPort) {
        this.defaultDriver = defaultDriver;
        this.defaultPort = defaultPort;
    }

    /**
     * Default constructor for objects which initialize the connection by themself.
     * The GenericDatabaseAdapter for example uses this constructor.
     */
    public DatabaseAdapterConnector() {
    }


    /**
     * This method initializes the adapter object.
     * <p/>
     * This version of init method will create connection object based on connection options provided in
     * {@code adapterConfig} parameter.
     * <p/>
     * If you need a specific routine to initialize this object, you have to override this method.
     * <p/>
     *
     * @param adapterConfig - configuration for adapter.
     * @param urlPatternPrefix - the prefix for the url pattern
     * @return connectionData - ConnectionDataHolder object which holds the values to connection to database.
     * @throws DatabaseAdapterInstantiationException
     * @see de.consistec.doubleganger.common.adapter.IDatabaseAdapter#init(java.util.Properties)
     */
    public ConnectionDataHolder init(Properties adapterConfig, String urlPatternPrefix) throws
        DatabaseAdapterInstantiationException {
        final String driverName;

        if (StringUtil.isNullOrEmpty(adapterConfig.getProperty(PROPS_DRIVER_NAME))) {
            driverName = defaultDriver;
        } else {
            driverName = adapterConfig.getProperty(PROPS_DRIVER_NAME);
        }

        String username = PropertiesUtil.readString(adapterConfig, PROPS_SYNC_USERNAME, false);
        String password = PropertiesUtil.readString(adapterConfig, PROPS_SYNC_PASSWORD, false);

        String connectionUrl = "";
        if (StringUtil.isNullOrEmpty(adapterConfig.getProperty(PROPS_URL))) {
            if (!StringUtil.isNullOrEmpty(adapterConfig.getProperty(PROPS_PORT))) {
                port = PropertiesUtil.readNumber(adapterConfig, PROPS_PORT, true, Integer.class);
            }

            host = PropertiesUtil.readString(adapterConfig, PROPS_HOST, true);
            databaseName = PropertiesUtil.readString(adapterConfig, PROPS_DB_NAME, true);
            connectionUrl = createUrl(urlPatternPrefix);

            LOGGER.debug("PostgreSQL connection URL is {}", connectionUrl);
        }

        return new ConnectionDataHolder(username, password, connectionUrl, driverName);
    }

    /**
     * Creates database connection.
     *
     * @param connectionData - ConnectionDataHolder object which holds the values to connection to database.
     * @return connection - Connection object created with this connector.
     * @throws de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterInstantiationException
     */
    public Connection createConnection(ConnectionDataHolder connectionData) throws DatabaseAdapterInstantiationException {
        final Connection connection;

        String driverName = connectionData.getDriverName();
        String password = connectionData.getPassword();
        String username = connectionData.getUsername();
        String connectionUrl = connectionData.getConnectionUrl();

        try {
            Class.forName(driverName);

            LOGGER.debug("create connection to {} ", connectionUrl);

            if (StringUtil.isNullOrEmpty(password) || StringUtil.isNullOrEmpty(username)) {
                connection = DriverManager.getConnection(connectionUrl);
            } else {
                connection = DriverManager.getConnection(connectionUrl, username, password);
            }
        } catch (ClassNotFoundException e) {
            String msg = read(DBAdapterErrors.CANT_LOAD_JDBC_DRIVER, driverName);
            LOGGER.error(FATAL_MARKER, msg, e);
            throw new DatabaseAdapterInstantiationException(msg, e);
        } catch (Exception e) {
            String msg = read(DBAdapterErrors.CANT_CREATE_ADAPTER_INSTANCE, getClass().getCanonicalName());
            LOGGER.error(FATAL_MARKER, msg, e);
            throw new DatabaseAdapterInstantiationException(msg, e);
        }

        try {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) {
            LOGGER.warn(read(DBAdapterWarnings.CANT_SET_TRANS_ISOLATION_LEVEL, "TRANSACTION_SERIALIZABLE"), e);
        }

        return connection;
    }

    /**
     * Creates jdbc url string for any database.
     * <p/>
     *
     * @param urlPatternPrefix - prefix for the url pattern (example: jdbc:mysql://)
     * @return Jdbc url string for postgreSQL driver.
     */
    public String createUrl(String urlPatternPrefix) {  //NOSONAR

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

    /**
     * Gets the port value.
     *
     * @return port - Integer
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Gets the host value.
     *
     * @return host - String
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the database name value.
     *
     * @return datebaseName - String
     */
    public String getDatabaseName() {
        return databaseName;
    }
}
