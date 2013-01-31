package de.consistec.syncframework.impl.adapter;

import static de.consistec.syncframework.common.MdTableDefaultValues.FLAG_COLUMN_NAME;
import static de.consistec.syncframework.common.MdTableDefaultValues.FLAG_MODIFIED;
import static de.consistec.syncframework.common.MdTableDefaultValues.MDV_MODIFIED_VALUE;
import static de.consistec.syncframework.common.MdTableDefaultValues.PK_COLUMN_NAME;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.adapter.DatabaseAdapterCallback;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @company Consistec Engineering and Consulting GmbH
 * @date 30.01.2013 15:34:35
 * @author davidm
 * @since
 */
public class MysqlDatabaseAdapter extends GenericDatabaseAdapter {

    /**
     * MySQL database property file.
     * Value: {@value}
     */
    public static final String MYSQL_CONFIG_FILE = "/config_mysql.properties";
    private static final String TRIGGERS_FILE_PATH = "/sql/mysql_create_triggers_%d.sql";
    private static final Config CONF = Config.getInstance();
    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlDatabaseAdapter.class.getCanonicalName());

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

                String triggerQuery = triggerRawQuery.replaceAll("%syncuser%", SYNC_USER);
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
}
