package de.consistec.syncframework.impl.adapter;

import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.adapter.DatabaseAdapterCallback;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.data.schema.Table;
import de.consistec.syncframework.common.exception.SchemaConverterException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.syncframework.common.exception.database_adapter.TransactionAbortedException;
import de.consistec.syncframework.common.exception.database_adapter.UniqueConstraintException;
import de.consistec.syncframework.common.util.PropertiesUtil;
import de.consistec.syncframework.common.util.StringUtil;
import de.consistec.syncframework.impl.i18n.DBAdapterErrors;

import java.sql.BatchUpdateException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A PostgreSQL specific implementation of IDatabaseAdapter.
 *
 * @author Markus Backes
 * @company Consistec Engineering and Consulting GmbH
 * @date 05.07.12 14:29
 * @since 0.0.1-SNAPSHOT
 */
public class PostgresDatabaseAdapter extends GenericDatabaseAdapter {

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
     * Default jdbc driver class for PostgreSQL.
     * <p/>
     * Value: {@value}.
     */
    public static final String DEFAULT_DRIVER = "org.postgresql.Driver";
    /**
     * Database property file.
     * Value: {@value}.
     */
    public static final String CONFIG_FILE = "/config_postgre.properties";
    /**
     * Default port on which database server is listening.
     * <p/>
     * Value: {@value}.
     */
    public static final int DEFAULT_PORT = 5432;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresDatabaseAdapter.class.getCanonicalName());
    private static final String HOST_REGEXP = "H_O_S_T";
    private static final String PORT_REGEXP = "P_O_R_T";
    private static final String DB_NAME_REGEXP = "D_B_N_A_M_E";
    private static final String URL_PATTERN = "jdbc:postgresql://" + HOST_REGEXP + ":" + PORT_REGEXP + "/" + DB_NAME_REGEXP;
    private static final String SYNC_USER = "syncuser";
    private static final String CREATE_LANGUAGE_FILE_PATH = "/sql/postgres_create_language.sql";
    private static final String CREATE_TRIGGERS_FILE_PATH = "/sql/postgres_create_triggers.sql";
    private static final Config CONF = Config.getInstance();
    private Integer port;
    private String host;
    private String databaseName;

    /**
     * Do not let direct object creation.
     */
    public PostgresDatabaseAdapter() {
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

            LOGGER.debug("PostgreSQL connection URL is {}", connectionUrl);
        }

        createConnection();
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
        final DatabaseAdapterCallback<ResultSet> callback) throws DatabaseAdapterException {
        try {
            super.getRowForPrimaryKey(primaryKey, tableName, callback);
        } catch (DatabaseAdapterException ex) {
            handleTransactionAborted(ex);
        }

    }

    @Override
    public void applySchema(Schema schema) throws DatabaseAdapterException {
        Statement stmt = null;

        List<String> triggers = new LinkedList<String>();
        if (CONF.isSqlTriggerActivated()) {
            triggers = generateSqlTriggersForSchema(schema);
        }
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

            // see http://weblogs.java.net/blog/2004/10/24/stupid-scanner-tricks
            String createLanguageQuery = new Scanner(getClass().getResourceAsStream(CREATE_LANGUAGE_FILE_PATH))
                .useDelimiter("\\A").next();
            LOGGER.debug("creating plpgsql language {}", createLanguageQuery);
            stmt.execute(createLanguageQuery);

            if (CONF.isSqlTriggerActivated()) {
                LOGGER.debug("adding triggers");
                for (String trigger : triggers) {
                    stmt.execute(trigger);
                }
            }

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

    /**
     * Creates a list of triggers to update the F flag in the metadata table
     * for every change in the corresponding data table.
     * <p/>
     * @param schema
     * @return list of sql queries for the triggers
     */
    private List<String> generateSqlTriggersForSchema(Schema schema) {
        List<String> triggers = new LinkedList<String>();

        // see http://weblogs.java.net/blog/2004/10/24/stupid-scanner-tricks
        String triggerRawQuery = new Scanner(getClass().getResourceAsStream(CREATE_TRIGGERS_FILE_PATH))
            .useDelimiter("\\A").next();

        for (Table table : schema.getTables()) {
            String tableName = table.getName();

            // we don't want any trigger on the metadata tables
            if (!tableName.endsWith(CONF.getMdTableSuffix())) {
                String triggerQuery = triggerRawQuery.replaceAll("%syncuser%", SYNC_USER);
                triggerQuery = triggerQuery.replaceAll("%table%", tableName);
                triggerQuery = triggerQuery.replaceAll("%pk%", table.getPkColumnName());
                triggerQuery = triggerQuery.replaceAll("%_md%", CONF.getMdTableSuffix());

                LOGGER.debug("Creating trigger for table '{}':\n {}", tableName, triggerQuery);

                triggers.add(triggerQuery);
            }
        }
        return triggers;
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
     * Creates jdbc url string for PostgreSQL.
     * <p/>
     *
     * @param phost Server host address (preferably ip).
     * @param pport Port on which server is listing.
     * @param pdbName Database name to connect to.
     * @return Jdbc url string for postgreSQL driver.
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
