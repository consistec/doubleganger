package de.consistec.doubleganger.impl.adapter;

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

import static de.consistec.doubleganger.common.MdTableDefaultValues.FLAG_COLUMN_NAME;
import static de.consistec.doubleganger.common.MdTableDefaultValues.FLAG_MODIFIED;
import static de.consistec.doubleganger.common.MdTableDefaultValues.MDV_MODIFIED_VALUE;
import static de.consistec.doubleganger.common.MdTableDefaultValues.PK_COLUMN_NAME;

import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.adapter.DatabaseAdapterCallback;
import de.consistec.doubleganger.common.adapter.impl.ConnectionDataHolder;
import de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector;
import de.consistec.doubleganger.common.adapter.impl.GenericDatabaseAdapter;
import de.consistec.doubleganger.common.data.schema.ISQLConverter;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.doubleganger.common.util.StringUtil;
import de.consistec.doubleganger.impl.data.schema.CreateSchemaToMySQLConverter;

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
     * Defines the prefix of the mysql url.
     */
    public static final String URL_PATTERN_PREFIX = "jdbc:mysql://";

    /**
     * Default port on which database server is listening.
     * <p/>
     * Value: {@value}.
     */
    public static final int DEFAULT_PORT = 3306;
    private static final String SYNC_USER = "mysql";
    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlDatabaseAdapter.class.getCanonicalName());
    private static final String TRIGGERS_FILE_PATH = "/sql/mysql_create_triggers.sql";
    private static final Config CONF = Config.getInstance();

    private DatabaseAdapterConnector initializer;

    /**
     * Do not let direct object creation.
     */
    public MySqlDatabaseAdapter() {
        LOGGER.debug("created new {}", getClass().getCanonicalName());
        initializer = new DatabaseAdapterConnector(DEFAULT_DRIVER, DEFAULT_PORT);
    }

    @Override
    public void init(Properties adapterConfig) throws DatabaseAdapterInstantiationException {
        ConnectionDataHolder connectionData = initializer.init(adapterConfig, URL_PATTERN_PREFIX);
        connection = initializer.createConnection(connectionData);
    }

    @Override
    public void createMDTableOnServer(final String tableName) throws DatabaseAdapterException {
        super.createMDTableOnServer(tableName);

        if (CONF.isSqlTriggerOnServerActivated()) {

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
     *
     * @param tableName the table's name
     * @return sql query for the triggers
     */
    protected String[] generateSqlTriggersForTable(String tableName) throws DatabaseAdapterException {
        String[] queries = new String[4];

        // we don't want any trigger on the metadata tables
        if (!tableName.endsWith(CONF.getMdTableSuffix())) {

            // Yes, we read these files *every time* a MD table is created... It's not optimized,
            // but we do it only once: the first sync is somewhat slower, that's all.
            String triggerRawQuery = new Scanner(getClass().getResourceAsStream(TRIGGERS_FILE_PATH))
                .useDelimiter("\\A").next();

            String triggerQuery = triggerRawQuery.replaceAll("%syncuser%", SYNC_USER);
            triggerQuery = triggerQuery.replaceAll("%table%", tableName);
            triggerQuery = triggerQuery.replaceAll("%md_suffix%", CONF.getMdTableSuffix());
            triggerQuery = triggerQuery.replaceAll("%pk_data%", getPrimaryKeyColumn(tableName).getName());
            triggerQuery = triggerQuery.replaceAll("%flag_md%", FLAG_COLUMN_NAME);
            triggerQuery = triggerQuery.replaceAll("%pk_md%", PK_COLUMN_NAME);

            queries = triggerQuery.split(";;");

        }
        LOGGER.debug("Creating trigger for table '{}':\n {}", tableName, queries);
        return queries;
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
        Integer port = initializer.getPort();
        String host = initializer.getHost();
        String databaseName = initializer.getDatabaseName();

        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("{ port=");
        builder.append(port == null ? "null" : port);
        builder.append(",\n host=");
        builder.append(StringUtil.isNullOrEmpty(host) ? "null or empty" : host);
        builder.append(",\n databaseName=");
        builder.append(
            StringUtil.isNullOrEmpty(databaseName) ? "null or empty" : databaseName);
        builder.append(" }");
        return builder.toString();
    }
}
