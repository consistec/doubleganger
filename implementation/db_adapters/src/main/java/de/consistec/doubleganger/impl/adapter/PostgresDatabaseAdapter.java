package de.consistec.doubleganger.impl.adapter;

/*
 * #%L
 * Project - doubleganger
 * File - PostgresDatabaseAdapter.java
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
import static de.consistec.doubleganger.common.i18n.MessageReader.read;

import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.adapter.DatabaseAdapterCallback;
import de.consistec.doubleganger.common.adapter.impl.ConnectionDataHolder;
import de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector;
import de.consistec.doubleganger.common.adapter.impl.GenericDatabaseAdapter;
import de.consistec.doubleganger.common.data.schema.Schema;
import de.consistec.doubleganger.common.exception.SchemaConverterException;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.doubleganger.common.exception.database_adapter.TransactionAbortedException;
import de.consistec.doubleganger.common.exception.database_adapter.UniqueConstraintException;
import de.consistec.doubleganger.common.i18n.DBAdapterErrors;
import de.consistec.doubleganger.common.util.StringUtil;

import java.sql.BatchUpdateException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A PostgreSQL specific implementation of IDatabaseAdapter.
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date 05.07.12 14:29
 * @since 0.0.1-SNAPSHOT
 */
public class PostgresDatabaseAdapter extends GenericDatabaseAdapter {

    /**
     * Default jdbc driver class for PostgreSQL.
     * <p/>
     * Value: {@value}.
     */
    public static final String DEFAULT_DRIVER = "org.postgresql.Driver";
    /**
     * Database property file.
     * Value: {@value}.
     */
    public static final String POSTGRE_CONFIG_FILE = "/config_postgre.properties";
    /**
     * Default port on which database server is listening.
     * <p/>
     * Value: {@value}.
     */
    public static final int DEFAULT_PORT = 5432;

    /**
     * Defines the prefix of the postgres url.
     */
    public static final String URL_PATTERN_PREFIX = "jdbc:postgresql://";

    /**
     * 40001 means psql error code SERIALIZATION FAILURE.
     * all transaction failures begin with 40...
     * <p/>
     *
     * @see http://www.postgresql.org/docs/8.2/static/errcodes-appendix.html
     */
    private static final String TRANSACTION_FAILURE_PREFIX = "40";
    /**
     * PostgreSQL code for UNIQUE VIOLATION.
     */
    private static final String UNIQUE_CONSTRAINT_EXCEPTION = "23505";
    private static final String RELATION_ALREADY_EXIST = "42P07";
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresDatabaseAdapter.class.getCanonicalName());

    private static final String CREATE_LANGUAGE_FILE_PATH = "/sql/postgres_create_language.sql";
    private static final String CREATE_POSTGRES_TRIGGERS_FILE_PATH = "/sql/postgres_create_triggers.sql";
    private static final Config CONF = Config.getInstance();
    private static final String SYNC_USER = "syncuser";

    private DatabaseAdapterConnector initializer;


    /**
     * Do not let direct object creation.
     */
    public PostgresDatabaseAdapter() {
        LOGGER.debug("created new {}", getClass().getCanonicalName());

        initializer = new DatabaseAdapterConnector(DEFAULT_DRIVER, DEFAULT_PORT);
    }

    @Override
    public void init(Properties adapterConfig) throws DatabaseAdapterInstantiationException {
        ConnectionDataHolder connectionData = initializer.init(adapterConfig, URL_PATTERN_PREFIX);
        connection = initializer.createConnection(connectionData);
    }

    @Override
    public void commit() throws DatabaseAdapterException {
        try {
            super.commit();
        } catch (DatabaseAdapterException ex) {
            handleTransactionAborted(ex);
        }
    }

    @Override
    public void getRowForPrimaryKey(final Object primaryKey, final String tableName,
                                    final DatabaseAdapterCallback<ResultSet> callback
    ) throws DatabaseAdapterException {
        try {
            super.getRowForPrimaryKey(primaryKey, tableName, callback);
        } catch (DatabaseAdapterException ex) {
            handleTransactionAborted(ex);
        }

    }

    @Override
    public void createMDTableOnClient(final String tableName) throws DatabaseAdapterException {
        try {

            super.createMDTableOnClient(tableName);
        } catch (DatabaseAdapterException ex) {
            SQLException sqlEx = (SQLException) ex.getCause();
            String state = sqlEx.getSQLState();

            if (UNIQUE_CONSTRAINT_EXCEPTION.equals(state) || RELATION_ALREADY_EXIST.equals(state)) {
                throw new UniqueConstraintException(read(DBAdapterErrors.CANT_CREATE_MD_TABLE, tableName),
                    sqlEx); //NOSONAR
            } else {
                handleTransactionAborted(ex);
            }
        }

        if (CONF.isSqlTriggerOnClientActivated()) {
            createTriggers(tableName);
        }
    }

    @Override
    public void createMDTableOnServer(final String tableName) throws DatabaseAdapterException {
        try {
            super.createMDTableOnServer(tableName);
        } catch (DatabaseAdapterException ex) {
            SQLException sqlEx = (SQLException) ex.getCause();
            if (sqlEx != null) {
                String state = sqlEx.getSQLState();

                if (UNIQUE_CONSTRAINT_EXCEPTION.equals(state) || RELATION_ALREADY_EXIST.equals(state)) {
                    throw new UniqueConstraintException(read(DBAdapterErrors.CANT_CREATE_MD_TABLE, tableName),
                        sqlEx); //NOSONAR
                } else {
                    handleTransactionAborted(ex);
                }
            } else {
                throw ex;
            }
        }

        if (CONF.isSqlTriggerOnServerActivated()) {
            createTriggers(tableName);
        }
    }

    /**
     * Creates the necessary triggers for this table if triggers are activated.
     * <p/>
     *
     * @param tableName the table's name
     * @throws DatabaseAdapterException
     */
    private void createTriggers(String tableName) throws DatabaseAdapterException {
        executePlpgsqlLanguageQuery();
        insertMdRowsForDataInTable(tableName);
        executeSqlTriggersForTable(tableName, CREATE_POSTGRES_TRIGGERS_FILE_PATH);
    }

    /**
     * Loads the language Plpgsql in the database so it can interpret the triggers' queries.
     * <p/>
     *
     * @throws DatabaseAdapterException
     */
    private void executePlpgsqlLanguageQuery() throws DatabaseAdapterException {
        String createLanguageQuery = getFileContent(CREATE_LANGUAGE_FILE_PATH);
        executeSqlQuery(createLanguageQuery);
    }

    /**
     * Creates an entry in the metadata table for every entry in the data table.
     * <p/>
     *
     * @param tableName the table's name
     * @throws DatabaseAdapterException
     */
    private void insertMdRowsForDataInTable(final String tableName) throws DatabaseAdapterException {
        getAllRowsFromTable(tableName, new DatabaseAdapterCallback<ResultSet>() {
            @Override
            public void onSuccess(ResultSet result) throws DatabaseAdapterException, SQLException {
                while (result.next()) {
                    final Object primaryKey = result.getObject(getPrimaryKeyColumn(tableName).getName());
                    insertMdRow(0, FLAG_MODIFIED, primaryKey, MDV_MODIFIED_VALUE, tableName);
                }
            }
        });
    }

    /**
     * Creates a trigger to update the F flag in the metadata on every change in the data table ON THE SERVER.
     * <p/>
     *
     * @param tableName the table's name
     * @param filePath path to the trigger's definition file
     * @return sql query for the triggers
     */
    private void executeSqlTriggersForTable(String tableName, String filePath) throws DatabaseAdapterException {
        String triggerQuery = "";

        // we don't want any trigger on the metadata tables
        if (!tableName.endsWith(CONF.getMdTableSuffix())) {

            // Yes, we read this file *every time* a MD table is created... It's not optimized, but
            // we do it only once: the first sync is somewhat slower, but that's all.
            String triggerRawQuery = getFileContent(filePath);

            triggerQuery = triggerRawQuery.replaceAll("%syncuser%", SYNC_USER);
            triggerQuery = triggerQuery.replaceAll("%table%", tableName);
            triggerQuery = triggerQuery.replaceAll("%md_suffix%", CONF.getMdTableSuffix());
            triggerQuery = triggerQuery.replaceAll("%pk_data%", getPrimaryKeyColumn(tableName).getName());
            triggerQuery = triggerQuery.replaceAll("%flag_md%", FLAG_COLUMN_NAME);
            triggerQuery = triggerQuery.replaceAll("%pk_md%", PK_COLUMN_NAME);

            LOGGER.debug("Creating trigger for table '{}':\n {}", tableName, triggerQuery);
        }
        executeSqlQuery(triggerQuery);
    }

    @Override
    public void applySchema(Schema schema) throws DatabaseAdapterException {
        Statement stmt = null;

        removeExistentTablesFromSchema(schema);

        try {
            stmt = connection.createStatement();

            String sqlSchema = getSchemaConverter().toSQL(schema);
            String[] tableScripts = sqlSchema.split(";");
            for (String tableSql : tableScripts) {
                stmt.addBatch(tableSql);
            }

            LOGGER.debug("applying schema: {}", sqlSchema);
            stmt.executeBatch();

        } catch (BatchUpdateException e) {
            throw new DatabaseAdapterException(read(DBAdapterErrors.CANT_APPLY_DB_SCHEMA), e.getNextException());
        } catch (SQLException e) {
            throw new DatabaseAdapterException(read(DBAdapterErrors.CANT_APPLY_DB_SCHEMA), e);
        } catch (SchemaConverterException e) {
            throw new DatabaseAdapterException(read(DBAdapterErrors.CANT_CONVERT_SCHEMA_TO_SQL), e);
        } finally {
            closeStatements(stmt);
        }
    }

    private String getFileContent(String filePath) {
        // see http://weblogs.java.net/blog/2004/10/24/stupid-scanner-tricks
        return new Scanner(getClass().getResourceAsStream(filePath)).useDelimiter("\\A").next();
    }

    @Override
    public void updateMdRow(int rev, int flag, Object pk, String mdv, String tableName) throws
        DatabaseAdapterException {
        try {
            super.updateMdRow(rev, flag, pk, mdv, tableName);
        } catch (DatabaseAdapterException ex) {
            handleTransactionAborted(ex);
        }
    }

    @Override
    public void deleteRow(Object primaryKey, String tableName) throws DatabaseAdapterException {
        try {
            super.deleteRow(primaryKey, tableName);
        } catch (DatabaseAdapterException ex) {
            handleTransactionAborted(ex);
        }
    }

    @Override
    public void insertMdRow(int rev, int f, Object pk, String mdv, String tableName) throws DatabaseAdapterException {
        try {
            super.insertMdRow(rev, f, pk, mdv, tableName);
        } catch (DatabaseAdapterException ex) {

            SQLException sqlEx = (SQLException) ex.getCause();

            if (UNIQUE_CONSTRAINT_EXCEPTION.equals(sqlEx.getSQLState())) {
                throw new UniqueConstraintException(read(DBAdapterErrors.CANT_INSERT_DATA_ROW, tableName),
                    sqlEx); //NOSONAR
            } else {
                handleTransactionAborted(ex);
            }
        }
    }

    @Override
    public void insertDataRow(Map<String, Object> data, String tableName) throws DatabaseAdapterException {
        try {
            super.insertDataRow(data, tableName);
        } catch (DatabaseAdapterException ex) {
            handleTransactionAborted(ex);
        }
    }

    @Override
    public void updateDataRow(Map<String, Object> data, Object primaryKey, String tableName) throws
        DatabaseAdapterException {
        try {
            super.updateDataRow(data, primaryKey, tableName);
        } catch (DatabaseAdapterException ex) {
            handleTransactionAborted(ex);
        }
    }

    /**
     * Brief description of object's state.
     * <p/>
     * E.g.
     * <code> PostgresDatabaseAdapter{ port=5432, host=192.168.3.1, databasename=myname} </code>
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
        builder.append(StringUtil.isNullOrEmpty(databaseName) ? "null or empty" : databaseName);
        builder.append(" }");
        return builder.toString();
    }

    @Override
    protected String getSchemaOfConnection() {
        return super.getSchemaOfConnection().toLowerCase();
    }

    private void handleTransactionAborted(DatabaseAdapterException ex) throws DatabaseAdapterException {

        if (ex.getCause() instanceof SQLException) {

            LOGGER.error(String.format("Cause State: %s", ((SQLException) ex.getCause()).getSQLState()), ex.getCause());

            SQLException sqlEx = (SQLException) ex.getCause();
            if (sqlEx.getSQLState().startsWith(TRANSACTION_FAILURE_PREFIX)) {
                throw new TransactionAbortedException(read(DBAdapterErrors.TRANSACTION_ABORTED_SERIALIZATION_FAILURES),
                    sqlEx);
            } else {
                throw ex;
            }
        } else {
            throw ex;
        }
    }
}
