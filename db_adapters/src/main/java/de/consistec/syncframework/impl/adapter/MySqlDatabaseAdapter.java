package de.consistec.syncframework.impl.adapter;

/*
 * #%L
 * Project - doppelganger
 * File - MySqlDatabaseAdapter.java
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

import static de.consistec.syncframework.common.MdTableDefaultValues.FLAG_COLUMN_NAME;
import static de.consistec.syncframework.common.MdTableDefaultValues.FLAG_MODIFIED;
import static de.consistec.syncframework.common.MdTableDefaultValues.MDV_MODIFIED_VALUE;
import static de.consistec.syncframework.common.MdTableDefaultValues.PK_COLUMN_NAME;
import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.adapter.DatabaseAdapterCallback;
import de.consistec.syncframework.common.data.schema.ISQLConverter;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.syncframework.common.util.PropertiesUtil;
import de.consistec.syncframework.common.util.StringUtil;
import de.consistec.syncframework.impl.data.schema.CreateSchemaToMySQLConverter;
import de.consistec.syncframework.impl.i18n.DBAdapterErrors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
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
    public static final String MYSQL_CONFIG_FILE = "/config_mysql.properties";
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
    private static final String TRIGGERS_FILE_PATH = "/sql/mysql_create_triggers_%d.sql";
    private static final Config CONF = Config.getInstance();
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

    @Override
    public void createMDTableOnServer(final String tableName) throws DatabaseAdapterException {
        super.createMDTableOnServer(tableName);

        if (CONF.isSqlTriggerActivated()) {

            getAllRowsFromTable(tableName, new DatabaseAdapterCallback<ResultSet>() {
                @Override
                public void onSuccess(ResultSet result) throws DatabaseAdapterException, SQLException {
                    while (result.next()) {
                        final Object primaryKey = result.getObject(getPrimaryKeyColumn(tableName).getName());
                        insertMdRow(0, FLAG_MODIFIED, primaryKey, MDV_MODIFIED_VALUE, tableName);
                    }
                }
            });
            String[] triggerQueries = generateSqlTriggersForTable(tableName);
            executeSqlQueries(triggerQueries);
        }
    }

    /**
     * Creates a trigger to update the F flag in the metadata on every change in the data table ON THE SERVER.
     * <p/>
     * @param tableName the table's name
     * @return sql query for the triggers
     */
    protected String[] generateSqlTriggersForTable(String tableName) throws DatabaseAdapterException {
        String[] queries = new String[10];
        queries[0] = "DROP TRIGGER IF EXISTS `" + tableName + "_after_insert`";
        queries[1] = "DROP TRIGGER IF EXISTS `" + tableName + "_after_update`";
        queries[2] = "DROP TRIGGER IF EXISTS `" + tableName + "_before_delete`";
        queries[3] = "DROP TRIGGER IF EXISTS `" + tableName + "_after_delete`";

        // we don't want any trigger on the metadata tables
        if (!tableName.endsWith(CONF.getMdTableSuffix())) {

            // see http://weblogs.java.net/blog/2004/10/24/stupid-scanner-tricks
            // Yes, we read these files *every time* a MD table is created... It's not optimized, but
            // we do it only once: the first sync is somewhat slower, but that's all.
            for (int i = 1; i < 5; i++) {
                String filePath = String.format(TRIGGERS_FILE_PATH, i);

                String triggerRawQuery = new Scanner(getClass().getResourceAsStream(filePath))
                    .useDelimiter("\\A").next();

                String triggerQuery = triggerRawQuery.replaceAll("%syncuser%", username);
                triggerQuery = triggerQuery.replaceAll("%table%", tableName);
                triggerQuery = triggerQuery.replaceAll("%md_suffix%", CONF.getMdTableSuffix());
                triggerQuery = triggerQuery.replaceAll("%pk_data%", getPrimaryKeyColumn(tableName).getName());
                triggerQuery = triggerQuery.replaceAll("%flag_md%", FLAG_COLUMN_NAME);
                triggerQuery = triggerQuery.replaceAll("%pk_md%", PK_COLUMN_NAME);

                queries[i + 4] = triggerQuery;
            }

        }
        LOGGER.debug("Creating trigger for table '{}':\n {}", tableName, queries);
        return queries;
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
