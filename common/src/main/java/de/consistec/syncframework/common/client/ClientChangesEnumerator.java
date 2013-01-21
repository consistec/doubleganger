package de.consistec.syncframework.common.client;

import static de.consistec.syncframework.common.util.CollectionsUtil.newArrayList;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.TableSyncStrategy;
import de.consistec.syncframework.common.adapter.DatabaseAdapterCallback;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.MDEntry;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.i18n.Infos;
import de.consistec.syncframework.common.util.DBMapperUtil;
import de.consistec.syncframework.common.util.LoggingUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.slf4j.cal10n.LocLogger;

/**
 * The {@code ClientChangesEnumerator} is responsible for creating a list
 * of {@code Change} objects which represents all changes on client tables to sync.
 * <br/>
 * For all inserted, modified or deleted rows, which are marked in the database
 * client md-table with flag = 1, the {@code ClientChangeEnumerator} creates a {@code Change}
 * object. This object consists of meta data values {@code MDEntry} and
 * row data values. The row data values are represented as Map<String, Object>, where the columnname
 * the key and Object is the values in the data row are.
 *
 * @author Markus Backes
 * @company Consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public class ClientChangesEnumerator {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private static final LocLogger LOGGER = LoggingUtil.createLogger(ClientChangesEnumerator.class.getCanonicalName());
    private static final transient Config CONF = Config.getInstance();
    private IDatabaseAdapter adapter;
    private TableSyncStrategies tableSyncStrategies;

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Class constructors " >
    /**
     * Instantiates a new client changes enumerator.
     *
     * @param adapter The database adapte
     * @param tableSyncStrategies The configured sync strategies for tables
     */
    public ClientChangesEnumerator(IDatabaseAdapter adapter, TableSyncStrategies tableSyncStrategies) {
        this.adapter = adapter;
        this.tableSyncStrategies = tableSyncStrategies;
        LOGGER.debug("ClientChangesEnumerator Constructor finished");
    }

    //</editor-fold>
    //<editor-fold defaultstate="expanded" desc=" Class methods " >
    /**
     * Creates the list of {@code Change} objects for all inserted, modified or deleted
     * data rows in the client tables to sync.
     *
     * @return The changes
     * @throws DatabaseAdapterException if database operations fail
     */
    public List<Change> getChanges() throws DatabaseAdapterException {

        LOGGER.debug("getClientChanges called");

        final List<Change> allChanges = newArrayList();

        for (final String tableName : CONF.getSyncTables()) {

            LOGGER.debug("processing table {}", tableName);

            adapter.getChangesByFlag(tableName, new DatabaseAdapterCallback<ResultSet>() {
                @Override
                public void onSuccess(ResultSet resultSet) throws DatabaseAdapterException {
                    try {
                        while (resultSet.next()) {

                            MDEntry mdEntry = DBMapperUtil.getMetadata(resultSet, tableName);

                            Map<String, Object> rowData = DBMapperUtil.getRowData(resultSet);

                            TableSyncStrategy syncStrategy = tableSyncStrategies.getSyncStrategyForTable(tableName);
                            SyncDirection syncDirection = syncStrategy.getDirection();

                            if (syncDirection != SyncDirection.SERVER_TO_CLIENT) {
                                Change change = new Change(mdEntry, rowData);
                                allChanges.add(change);
                                LOGGER.info(Infos.COMMON_ADDED_CLIENT_CHANGE_TO_CHANGE_SET, change);
                            }
                        }
                    } catch (SQLException e) {
                        throw new DatabaseAdapterException(e);
                    }
                }
            });
        }
        LOGGER.debug("getClientChanges finished");
        return allChanges;
    }
    //</editor-fold>
}
