package de.consistec.syncframework.impl.adapter;

import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.data.schema.ISQLConverter;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.syncframework.common.util.PropertiesUtil;
import de.consistec.syncframework.common.util.StringUtil;
import de.consistec.syncframework.impl.data.schema.CreateSchemaToMySQLConverter;
import de.consistec.syncframework.impl.i18n.DBAdapterErrors;

import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 30.01.13 17:02
 */
public class MySqlDatabaseAdapter extends GenericDatabaseAdapter {

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
     * Default jdbc driver class for mySQL.
     * <p/>
     * Value: {@value}.
     */
    public static final String DEFAULT_DRIVER = "com.mysql.jdbc.Driver";
    /**
     * Database property file.
     * Value: {@value}.
     */
    public static final String CONFIG_FILE = "/config_mysql.properties";
    /**
     * Default port on which database server is listening.
     * <p/>
     * Value: {@value}.
     */
    public static final int DEFAULT_PORT = 3306;

    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlDatabaseAdapter.class.getCanonicalName());


    private static final String HOST_REGEXP = "H_O_S_T";
    private static final String PORT_REGEXP = "P_O_R_T";
    private static final String DB_NAME_REGEXP = "D_B_N_A_M_E";
    private static final String URL_PATTERN = "jdbc:mysql://" + HOST_REGEXP + ":" + PORT_REGEXP + "/" + DB_NAME_REGEXP;

    private Integer port;
    private String host;
    private String databaseName;

    /**
     * Do not let direct object creation.
     */
    public MySqlDatabaseAdapter() {
        LOGGER.debug("created new {}", getClass().getCanonicalName());
    }

    @Override
    public void init(Properties adapterConfig) throws DatabaseAdapterInstantiationException {

        if (StringUtil.isNullOrEmpty(adapterConfig.getProperty(PROPS_DRIVER_NAME))) {
            driverName = DEFAULT_DRIVER;
        } else {
            driverName = adapterConfig.getProperty(PROPS_DRIVER_NAME);
        }

        username = PropertiesUtil.readString(adapterConfig, PROPS_SYNC_USERNAME, false);
        password = PropertiesUtil.readString(adapterConfig, PROPS_SYNC_PASSWORD, false);

        if (StringUtil.isNullOrEmpty(adapterConfig.getProperty(PROPS_URL))) {
            if (!StringUtil.isNullOrEmpty(adapterConfig.getProperty(PROPS_PORT))) {
                port = PropertiesUtil.readNumber(adapterConfig, PROPS_PORT, true, Integer.class);
            }

            host = PropertiesUtil.readString(adapterConfig, PROPS_HOST, true);
            databaseName = PropertiesUtil.readString(adapterConfig, PROPS_DB_NAME, true);
            connectionUrl = createUrl(host, port, databaseName);

            LOGGER.debug("MySQL connection URL is {}", connectionUrl);
        }

        createConnection();
    }

    /**
     * Creates jdbc url string for mySQL.
     * <p/>
     *
     * @param phost Server host address (preferably ip).
     * @param pport Port on which server is listing.
     * @param pdbName Database name to connect to.
     * @return Jdbc url string for mySQL driver.
     */
    private static String createUrl(String phost, Integer pport, String pdbName) {  //NOSONAR

        if (StringUtil.isNullOrEmpty(phost)) {
            throw new IllegalArgumentException(read(DBAdapterErrors.HOSTNAME_IS_EMPTY));
        }
        if (StringUtil.isNullOrEmpty(pdbName)) {
            throw new IllegalArgumentException(read(DBAdapterErrors.DATABASE_NAME_EMPTY));
        }

        String result = URL_PATTERN.replaceAll(HOST_REGEXP, phost);
        result = result.replaceAll(PORT_REGEXP, String.valueOf((pport == null) ? DEFAULT_PORT : pport));
        result = result.replaceAll(DB_NAME_REGEXP, pdbName);

        return result;
    }

    @Override
    public ISQLConverter getSchemaConverter() {
        return new CreateSchemaToMySQLConverter();
    }

    /**
     * Brief description of object's state.
     * <p/>
     * E.g.
     * <code> MySQLDatabaseAdapter{ port=3306, host=192.168.3.1, databasename=myname} </code>
     * <p/>
     * Result of this method could be changed in future so one should not rely on it.
     * <p/>
     *
     * @return String description of object's state.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("{ port=");
        builder.append(port == null ? "null" : port);
        builder.append(",\n host=");
        builder.append(StringUtil.isNullOrEmpty(host) ? "null or empty" : host);
        builder.append(",\n databaseName=");
        builder.append(StringUtil.isNullOrEmpty(databaseName) ? "null or empty" : databaseName);
        builder.append(" }");
        return builder.toString();
    }
}
