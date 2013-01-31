package de.consistec.syncframework.common.adapter;

import de.consistec.syncframework.common.data.schema.Column;
import de.consistec.syncframework.common.data.schema.ISQLConverter;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterInstantiationException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This interface provides abstraction of the database layer.
 * To implement a new database adapter you have to implement this interface or extend existing adapter,
 * e.g. the GenericDatabaseAdapter from database adapters jar.
 * <p/>
 * Adapter configuration is hold by
 * {@link de.consistec.syncframework.common.Config#getServerDatabaseProperties() } method.
 * Configuration options for your adapter implementation can be added to frameworks configuration file.
 * Just use {@code "framework.server.db_adapter."} prefix for each key.
 * <b>Remember</b> to expose your options key names as {@code public static final String } fields without the
 * {@code "framework.server.db_adapter."} prefix in your implementation.<br/>
 * In {@link java.util.Properties} object returned by
 * {@link de.consistec.syncframework.common.Config#getServerDatabaseProperties() } method, options will have
 * keys without the {@code "framework.server.db_adapter."} prefix.
 *
 * @author Markus Backes
 * @company Consistec Engineering and Consulting GmbH
 * @date 03.07.12 11:36
 * @since 0.0.1-SNAPSHOT
 */
public interface IDatabaseAdapter {

    /**
     * Creates connection to destination database and sets Connection.TRANSACTION_SERIALIZABLE attribute on it.
     * <p/>
     *
     * @param adapterConfig Data for creating adapter instance. <b>Remember</b> that the keys does <b>not</b> have
     * {@code "framework.server.db_adapter."} prefix!
     * @throws DatabaseAdapterInstantiationException
     */
    void init(Properties adapterConfig) throws DatabaseAdapterInstantiationException;

    /**
     * Initialize the adapter with external connection.
     * <p/>
     * The connection has already transaction isolation level set to SERIALIZABLE.
     *
     * @param connection External database connection.
     * @throws DatabaseAdapterInstantiationException
     */
    void init(Connection connection) throws DatabaseAdapterInstantiationException;

    /**
     * Returns a schema sql converter for the current database.
     * <p/>
     *
     * @return The converter object.
     * @see de.consistec.syncframework.common.data.schema.ISQLConverter
     */
    ISQLConverter getSchemaConverter();

    /**
     * Returns a table sql converter for the current database.
     *
     * @return the converter object
     * @see de.consistec.syncframework.common.data.schema.ISQLConverter
     */
    ISQLConverter getTableConverter();

    /**
     * Return a list of changes since the given revision.
     * <p/>
     *
     * @param revision The revision to select the changes from then on
     * @param tableName The table from which to select the changes
     * @param callback The callback with the changes
     * @throws DatabaseAdapterException
     */
    void getChangesForRevision(int revision, String tableName, DatabaseAdapterCallback<ResultSet> callback)
        throws DatabaseAdapterException;

    /**
     * Return a list of changes with flag set to 1 (only client side).
     * <p/>
     *
     * @param tableName The table from which to select the changes
     * @param callback The callback with the changes
     * @throws DatabaseAdapterException
     */
    void getChangesByFlag(String tableName, DatabaseAdapterCallback<ResultSet> callback)
        throws DatabaseAdapterException;

    /**
     * Update the revision for a single row.
     * <p/>
     *
     * @param revision The revision to set for this row.
     * @param tableName The table name which contains this row.
     * @param primaryKey The primary key for the row to update.
     * @return The update count
     * @throws DatabaseAdapterException
     */
    int updateRevision(int revision, String tableName, Object primaryKey) throws DatabaseAdapterException;

    /**
     * Get row for the given primary key.
     * <p/>
     *
     * @param primaryKey The primary key for the row to select.
     * @param tableName The table name which contains this row.
     * @param callback The callback with the row.
     * @throws DatabaseAdapterException
     */
    void getRowForPrimaryKey(Object primaryKey, String tableName, DatabaseAdapterCallback<ResultSet> callback)
        throws DatabaseAdapterException;

    /**
     * Get all deleted rows for the given table name.
     * <p/>
     *
     * @param tableName Table to select deleted rows from
     * @param callback The callback with the deleted row information
     * @throws DatabaseAdapterException
     */
    void getDeletedRowsForTable(String tableName, DatabaseAdapterCallback<ResultSet> callback)
        throws DatabaseAdapterException;

    /**
     * Get all rows from the given table.
     * <p/>
     *
     * @param table The table to select rows from
     * @param callback The callback with the selected rows
     * @throws DatabaseAdapterException
     */
    void getAllRowsFromTable(String table, DatabaseAdapterCallback<ResultSet> callback) throws DatabaseAdapterException;

    /**
     * Returns a schema object for the current database.
     * Schema should consists only from monitored data tables (no md tables);
     * <p/>
     *
     * @return the schema
     * @throws DatabaseAdapterException
     */
    Schema getSchema() throws DatabaseAdapterException;

    /**
     * Create the md schema on the server.
     * <p/>
     *
     * @throws DatabaseAdapterException
     */
    void createMDSchemaOnServer() throws DatabaseAdapterException;

    /**
     * Create the md schema on the client.
     * <p/>
     *
     * @throws DatabaseAdapterException
     */
    void createMDSchemaOnClient() throws DatabaseAdapterException;

    /**
     * Update a single md row.
     * <p/>
     *
     * @param rev The new revision
     * @param f The new flag, Set the flag to -1 to get ignored
     * @param pk The primary key to update
     * @param mdv The new mdv
     * @param tableName The table name
     * @throws DatabaseAdapterException
     */
    void updateMdRow(int rev, int f, Object pk, String mdv, String tableName) throws DatabaseAdapterException;

    /**
     * Insert a single md row.
     * <p/>
     *
     * @param rev The new revision
     * @param f The new flag, Set the flag to -1 to get ignored
     * @param pk The primary key to insert
     * @param mdv The new mdv
     * @param tableName The table name
     * @throws DatabaseAdapterException
     */
    void insertMdRow(int rev, int f, Object pk, String mdv, String tableName) throws DatabaseAdapterException;

    /**
     * Update a single data row.
     * <p/>
     *
     * @param data The data map. Key==column name. Value==column value.
     * @param primaryKey The primary key to update
     * @param tableName The table name
     * @throws DatabaseAdapterException
     */
    void updateDataRow(Map<String, Object> data, Object primaryKey, String tableName) throws DatabaseAdapterException;

    /**
     * Insert a single data row.
     * <p/>
     *
     * @param data The data map. Key==column name. Value==column value.
     * @param tableName The table name
     * @throws DatabaseAdapterException
     */
    void insertDataRow(Map<String, Object> data, String tableName) throws DatabaseAdapterException;

    /**
     * Delete a single row from the database.
     * <p/>
     *
     * @param primaryKey The primary key to delete
     * @param tableName The table name
     * @throws DatabaseAdapterException
     */
    void deleteRow(Object primaryKey, String tableName) throws DatabaseAdapterException;

    /**
     * Get the newest revision value.
     * <p/>
     *
     * @return the biggest revision number
     * @throws DatabaseAdapterException
     */
    int getLastRevision() throws DatabaseAdapterException;

    /**
     * Calculate the next revision number to set.
     * <p/>
     *
     * @return The next revision
     * @throws DatabaseAdapterException
     */
    int getNextRevision() throws DatabaseAdapterException;

    /**
     * Returns the name of the primary key column.
     * <p/>
     *
     * @param table The table to receive primary key column name from
     * @return The primary key Column
     * @throws DatabaseAdapterException
     */
    Column getPrimaryKeyColumn(String table) throws DatabaseAdapterException;

    /**
     * Returns all column names for the single table.
     * <p/>
     *
     * @param tableName The table to receive column names from
     * @return The column names
     * @throws DatabaseAdapterException
     */
    List<String> getColumnNamesFromTable(String tableName) throws DatabaseAdapterException;

    /**
     * Apply the given schema to the database.
     * <p/>
     *
     * @param s The schema
     * @throws DatabaseAdapterException
     */
    void applySchema(Schema s) throws DatabaseAdapterException;

    /**
     * Returns true if schema for configured tables exist (client side only).
     * Check should include md tables.
     * <p/>
     *
     * @return True if schema exist, otherwise false
     * @throws DatabaseAdapterException
     */
    boolean hasSchema() throws DatabaseAdapterException;

    /**
     * Return database connection used by the adapter.
     *
     * @return Database connection.
     */
    Connection getConnection();

    /**
     * Commits the underling database connection.
     *
     * @throws DatabaseAdapterException
     */
    void commit() throws DatabaseAdapterException;

    /**
     * Checks if the meta data table for the passed db table name exists.
     *
     * @param tableName name of db table which meta data table is checked
     * @return true if meta data table exists otherwise false
     */
    boolean existsMDTable(String tableName) throws DatabaseAdapterException;

    /**
     * Creates the meta data table for the passed db table name on the server.
     *
     * @param tableName name of db table which meta data table will be created.
     */
    void createMDTableOnServer(String tableName) throws DatabaseAdapterException;

    /**
     * Creates the meta data table for the passed db table name on the client.
     *
     * @param tableName name of db table which meta data table will be created.
     */
    void createMDTableOnClient(String tableName) throws DatabaseAdapterException;
}
