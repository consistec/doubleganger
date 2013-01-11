package de.consistec.syncframework.common;

import static de.consistec.syncframework.common.i18n.MessageReader.read;
import static de.consistec.syncframework.common.util.CollectionsUtil.newSyncMap;
import static de.consistec.syncframework.common.util.Preconditions.checkNotNull;

import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.i18n.Errors;
import de.consistec.syncframework.common.util.LoggingUtil;

import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.cal10n.LocLogger;

/**
 * The class {@code TableSyncStrategies} represents a container for all per table
 * synchronization strategies. It contains a map to store the strategies for each
 * table.
 * <p/>
 * If a table is not bind with a custom strategy, then the <i>global</i> values
 * for options like sync direction or filtering configured in the class
 * {@code de.consistec.syncframework.common.Config} will be used.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 03.12.12 15:05
 */
public class TableSyncStrategies {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(TableSyncStrategies.class.getCanonicalName());

    private static final TableSyncStrategy DEFAULT_SYNC_STRATEGY = new TableSyncStrategy(SyncDirection.BIDIRECTIONAL,
        ConflictStrategy.SERVER_WINS);

    /**
     * Question: maybe this should be a ...data.schema.Table,TableSyncStrategy map?.
     */
    private Map<String, TableSyncStrategy> strategies;

    /**
     * Creates new, empty strategies collection.
     */
    public TableSyncStrategies() {
        strategies = newSyncMap();
    }

    /**
     * Creates new strategies collection, populated with values from provided <i>strategies</i> collection.
     * Provided values will be copied.
     *
     * @param strategies Sync strategies for tables.
     */
    public TableSyncStrategies(Map<String, TableSyncStrategy> strategies) {
        this();
        addAll(strategies);
    }

    /**
     * Binds a table with a custom sync strategy.
     *
     * @param table table to be synchronized
     * @param syncStrategy strategy of synchronization
     */
    public final void addSyncStrategyForTable(String table, TableSyncStrategy syncStrategy) {
        checkNotNull(table, read(Errors.COMMON_TABLE_NAME_IS_NULL));
        LOGGER.debug("put sync strategy to map with key {}", syncStrategy, table);
        this.strategies.put(table, syncStrategy);
    }

    /**
     * Removes binding of <i>table</i> with strategy.
     * <p/>
     *
     * @param tables Tables names.
     */
    public final void removeSyncStrategyForTable(String... tables) {
        for (String tab : tables) {
            LOGGER.debug("remove sync strategy from map with key {}", tab);
            this.strategies.remove(tab);
        }
    }

    /**
     * Return synchronization strategy for <i>table</i>.
     * if no sync strategy can be found for the passed table, then a new sync strategy with
     * the global sync direction and conflict action values will be created.
     * if the global sync direction or conflict action is also not set, then the returned
     * sync strategy will have the default values SyncDirection.SERVER_TO_CLIENT
     * and ConflictStrategy.SERVER_WINS.
     *
     * @param table Table name.
     * @return Synchronization strategy for table.
     */
    public final TableSyncStrategy getSyncStrategyForTable(String table) {
        if (this.strategies.containsKey(table)) {
            return this.strategies.get(table);
        } else {
            Config cfg = Config.getInstance();
            SyncDirection syncDirection = DEFAULT_SYNC_STRATEGY.getDirection();
            if (cfg.getGlobalSyncDirection() != null) {
                syncDirection = cfg.getGlobalSyncDirection();
            }

            ConflictStrategy conflictStrategy = DEFAULT_SYNC_STRATEGY.getConflictStrategy();
            if (cfg.getGlobalConflictStrategy() != null) {
                conflictStrategy = cfg.getGlobalConflictStrategy();
            }

            return new TableSyncStrategy(syncDirection, conflictStrategy);
        }
    }

    /**
     * Add new strategies.
     * Provided {@code strategies} will be copied to this instance.
     *
     * @param strat Sync strategies
     */
    public final void addAll(Map<String, TableSyncStrategy> strat) {
        for (Entry<String, TableSyncStrategy> entry : strat.entrySet()) {
            checkNotNull(entry.getKey(), read(Errors.COMMON_TABLE_NAME_IS_NULL));
        }
        strategies.putAll(strat);
    }

    /**
     * Add new strategies.
     * Provided {@code strategies} will be copied to this instance.
     *
     * @param strat Synchronization strategies.
     */
    public final void addAll(TableSyncStrategies strat) {
        this.addAll(strat.strategies);
    }
}
