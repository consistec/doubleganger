package de.consistec.syncframework.common;

import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.i18n.Errors;
import de.consistec.syncframework.common.i18n.Warnings;
import de.consistec.syncframework.common.util.LoggingUtil;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.cal10n.LocLogger;

/**
 * Skeleton class for server and client synchronization providers.
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 10.12.2012 11:19:42
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
public abstract class AbstractSyncProvider {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(AbstractSyncProvider.class.getCanonicalName());
    /**
     * External data source provides external jdbc connections for database adapter.
     * <p>When this field has {@code null} value, adapter should be using its own connection.</p>
     */
    private DataSource ds;
    private TableSyncStrategies strategies;

    /**
     * Constructs provider object which uses database adapter with adapter internal jdbc connection.
     *
     * @param strategies Special synchronization strategies for tables.
     */
    public AbstractSyncProvider(TableSyncStrategies strategies) {
        this.strategies = strategies;
    }

    /**
     * Constructs provider object which uses database adapter with external jdbc connections, provided by
     * {@link javax.sql.DataSource ds}.
     *
     * @param strategies Special synchronization strategies for tables.
     * @param ds External data source.
     */
    public AbstractSyncProvider(TableSyncStrategies strategies, DataSource ds) {
        this(strategies);
        this.ds = ds;
    }

    /**
     * Return collection of synchronization strategies for monitored tables.
     *
     * @return Synchronization strategies.
     */
    public TableSyncStrategies getStrategies() {
        return strategies;
    }

    /**
     * External datasource.
     *
     * @return External datasource.
     */
    public DataSource getDs() {
        return ds;
    }

    /**
     * Closes database connection in adapter.
     * <p>If {@link java.sql.SQLException SQLException} occurs while closing the connection, no action is taken apart
     * from logging the exception with <i>warning</i> level. </p>
     *
     * @param adapter Database adapter
     */
    protected final void closeConnection(IDatabaseAdapter adapter) {
        if (adapter != null) {
            try {
                adapter.getConnection().close();
            } catch (SQLException ex) {
                LOGGER.warn(Warnings.DATA_CANT_CLOSE_CONNECTION, ex);
            }
        }
    }

    /**
     * Rolls back database transaction in adapter connection.
     *
     * @see java.sql.Connection#close()
     * @param adapter Database adapter
     * @throws SyncException When {@link java.sql.SQLException SQLException} occurs
     */
    protected final void rollback(IDatabaseAdapter adapter) throws SyncException {
        if (adapter != null) {
            try {
                adapter.getConnection().rollback();
            } catch (SQLException ex) {
                throw new SyncException(read(Errors.DATA_TRANSACTION_ROLLBACK_FAILED), ex);
            }
        }
    }
}
