package de.consistec.syncframework.common;

/*
 * #%L
 * Project - doppelganger
 * File - AbstractSyncProvider.java
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

import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.adapter.DatabaseAdapterFactory;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.syncframework.common.i18n.Errors;
import de.consistec.syncframework.common.i18n.Warnings;
import de.consistec.syncframework.common.util.LoggingUtil;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.cal10n.LocLogger;

/**
 * Skeleton class for server and client synchronization providers.
 *
 * @author Piotr Wieczorek
 * @company consistec Engineering and Consulting GmbH
 * @date 10.12.2012 11:19:42
 * @since 0.0.1-SNAPSHOT
 */
public abstract class AbstractSyncProvider {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(AbstractSyncProvider.class.getCanonicalName());

    /**
     * database adapter only for test classes.
     */
    protected IDatabaseAdapter dbAdapter;

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
     * Constructs provider object which uses database adapter with adapter internal jdbc connection.
     *
     * @param strategies Special synchronization strategies for tables.
     * @param dbAdapter used databaseadapter
     */
    public AbstractSyncProvider(TableSyncStrategies strategies, IDatabaseAdapter dbAdapter) {
        this.strategies = strategies;
        this.dbAdapter = dbAdapter;
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
     * Constructs provider object which uses database adapter with external jdbc connections, provided by
     * {@link javax.sql.DataSource ds}.
     *
     * @param strategies Special synchronization strategies for tables.
     * @param ds External data source.
     * @param dbAdapter the used databaseadatper
     */
    public AbstractSyncProvider(TableSyncStrategies strategies, DataSource ds, IDatabaseAdapter dbAdapter) {
        this(strategies);
        this.ds = ds;
        this.dbAdapter = dbAdapter;
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
     * @param adapter Database adapter
     * @throws SyncException When {@link java.sql.SQLException SQLException} occurs
     * @see java.sql.Connection#close()
     */
    protected final void rollback(IDatabaseAdapter adapter) throws SyncException {
        if (adapter != null) {
            try {
                adapter.getConnection().rollback();
            } catch (SQLException ex) {
                throw new SyncException(read(Errors.DATA_TRANSACTION_ROLLBACK_FAILED), ex);
            } finally {
                closeConnection(adapter);
            }
        }
    }

    /**
     * Creates database adapter object (no autocommit).
     *
     * @param purpose client or server
     * @param autocommit true o'Lr false
     * @return Adapter object.
     */
    protected IDatabaseAdapter prepareDbAdapter(DatabaseAdapterFactory.AdapterPurpose purpose, boolean autocommit
    ) throws
        DatabaseAdapterInstantiationException {

        if (dbAdapter != null) {
            return dbAdapter;
        }

        IDatabaseAdapter adapter;
        try {
            if (getDs() == null) {
                adapter = DatabaseAdapterFactory.newInstance(purpose);
            } else {
                adapter = DatabaseAdapterFactory.newInstance(purpose,
                    getDs().getConnection());
            }

            adapter.getConnection().setAutoCommit(autocommit);
            return adapter;

        } catch (SQLException ex) {
            throw new DatabaseAdapterInstantiationException(ex);
        }

    }
}
