package de.consistec.doubleganger.android.adapter;

/*
 * #%L
 * doubleganger
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

import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.PROPS_DRIVER_NAME;
import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.PROPS_SYNC_PASSWORD;
import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.PROPS_SYNC_USERNAME;
import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.PROPS_URL;

import de.consistec.doubleganger.common.adapter.impl.GenericDatabaseAdapter;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.doubleganger.common.util.PropertiesUtil;
import de.consistec.doubleganger.common.util.StringUtil;

import dalvik.system.PathClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database adapter for SQLite on Gingerbread platform.
 * <p/>
 * Date: 06.07.12 13:17
 *
 * @author Markus Backes
 */
public class GingerbreadSQLiteDatabaseAdapter extends GenericDatabaseAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GingerbreadSQLiteDatabaseAdapter.class);

    @Override
    public void init(Properties adapterConfig) throws DatabaseAdapterInstantiationException {

        LOGGER.debug("creating path class loader");

        PathClassLoader pathLoad = AccessController.doPrivileged(new SyncTestClassLoader());

        driverName = PropertiesUtil.readString(adapterConfig, PROPS_DRIVER_NAME, true);
        connectionUrl = PropertiesUtil.readString(adapterConfig, PROPS_URL, true);
        username = PropertiesUtil.readString(adapterConfig, PROPS_SYNC_USERNAME, false);
        password = PropertiesUtil.readString(adapterConfig, PROPS_SYNC_PASSWORD, false);

        LOGGER.debug("driverName= '{0}', connectionUrl= '{1}', username= '{2}',password= '{3}'",
            driverName, connectionUrl, username, password);

        try {

            LOGGER.debug("calling Class.forName({})", driverName);
            Class.forName(driverName, true, pathLoad);

            if (StringUtil.isNullOrEmpty(username) || StringUtil.isNullOrEmpty(password)) {
                connection = DriverManager.getConnection(connectionUrl);
            } else {
                connection = DriverManager.getConnection(connectionUrl, username, password);
            }
        } catch (ClassNotFoundException e) {
            throw new DatabaseAdapterInstantiationException(
                "the driver class could not be found: " + driverName + ". ", e);
        } catch (Exception e) {
            throw new DatabaseAdapterInstantiationException("could not initialize database adapter.", e);
        }

        try {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) {
            LOGGER.warn(
                "could not enable TransactionIsolation level SERIALIZABLE. This could lead to strange sync behavior!",
                e);
        }
    }

    private static class SyncTestClassLoader implements PrivilegedAction<PathClassLoader> {

        @Override
        public PathClassLoader run() {
            return new PathClassLoader("/system/framework/sqlite-jdbc.jar", ClassLoader.getSystemClassLoader());
        }
    }
}
