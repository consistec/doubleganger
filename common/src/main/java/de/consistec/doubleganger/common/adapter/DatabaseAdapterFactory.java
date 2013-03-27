package de.consistec.doubleganger.common.adapter;

/*
 * #%L
 * Project - doppelganger
 * File - DatabaseAdapterFactory.java
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
import static de.consistec.doubleganger.common.i18n.MessageReader.read;
import static de.consistec.doubleganger.common.util.Preconditions.checkNotNull;

import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.ConfigConstants;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.doubleganger.common.i18n.Errors;
import de.consistec.doubleganger.common.i18n.Infos;
import de.consistec.doubleganger.common.i18n.Warnings;
import de.consistec.doubleganger.common.util.HashCalculator;
import de.consistec.doubleganger.common.util.LoggingUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.cal10n.LocLogger;

/**
 * Factory class to fabricate instances of specific implementation of
 * {@link de.consistec.doubleganger.common.adapter.IDatabaseAdapter} interface.
 * Factory uses {@link de.consistec.doubleganger.common.Config configuration} class to read which adapter should
 * be created.
 * <p/>
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 26.06.12 14:10
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public final class DatabaseAdapterFactory {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(DatabaseAdapterFactory.class.getCanonicalName());

    /**
     * For what purpose should factory produce the adapter.
     */
    public enum AdapterPurpose {

        /**
         * For server provider.
         */
        SERVER,
        /**
         * For client provider.
         */
        CLIENT
    };

    private DatabaseAdapterFactory() {
        throw new AssertionError("Instances not allowed");
    }

    /**
     * Try to initialize new database adapter with provided connection created outside the framework.
     * <p/>
     * @param <T> Adapter type
     * @param purpose {@link AdapterPurpose}
     * @param connection Database connection not managed by sync framework
     * @return IDatabaseAdapter New instance of {@link de.consistec.doubleganger.common.adapter.IDatabaseAdapter}
     * implementation.
     */
    public static <T extends IDatabaseAdapter> T newInstance(AdapterPurpose purpose, Connection connection) throws
        DatabaseAdapterInstantiationException {

        checkNotNull(connection, read(Errors.DATA_CONNECTON_NOT_INITIALIZED));
        setTransactionIsolationLevel(connection);
        return privateNewInstance(purpose, connection);
    }

    /**
     * Try to initialize new database adapter with connection created from framework settings.
     * <p/>
     * @param purpose {@link AdapterPurpose}
     * @param <T> Adapter type
     * @return IDatabaseAdapter New instance of {@link de.consistec.doubleganger.common.adapter.IDatabaseAdapter}
     * implementation.
     */
    public static <T extends IDatabaseAdapter> T newInstance(AdapterPurpose purpose) throws
        DatabaseAdapterInstantiationException {
        return privateNewInstance(purpose, null);
    }

    /**
     * Prepares adapter class and configuration and then invokes
     * {@link DatabaseAdapterFactory#createInstance(java.lang.Class, java.util.Properties, java.sql.Connection) }.
     * <p/>
     * @param <T> Type of implementation
     * @param purpose
     * @param connection External database connection for adapter.
     * @return New T instance
     * @throws DatabaseAdapterInstantiationException
     */
    private static <T extends IDatabaseAdapter> T privateNewInstance(AdapterPurpose purpose, Connection connection)
        throws DatabaseAdapterInstantiationException {

        Properties props;
        try {

            final Config conf = Config.getInstance();
            Class clazz;
            switch (purpose) {
                case SERVER:
                    clazz = Class.forName(conf.getServerDatabaseAdapter().getCanonicalName());
                    props = conf.getServerDatabaseProperties();
                    break;
                case CLIENT:
                    clazz = Class.forName(conf.getClientDatabaseAdapter().getCanonicalName());
                    props = conf.getClientDatabaseProperties();
                    break;
                default:
                    throw new DatabaseAdapterInstantiationException(read(Errors.DATA_UNKNOWN_ADAPTER_PURPOSE));
            }

            return (T) createInstance(clazz, props, connection);

        } catch (ClassNotFoundException ex) {
            throw new DatabaseAdapterInstantiationException(ex);
        }
    }

    /**
     * Creates and initialize the instance.
     * <p/>
     * @param <T> Type of implementation
     * @param clazz Class of implementation
     * @param adapterConfig Configuration for adapter.
     * @param connection External database connection for adapter.
     * @return Newly created and initialized instance of type T.
     * @throws DatabaseAdapterInstantiationException
     */
    private static <T extends IDatabaseAdapter> T createInstance(Class<? extends IDatabaseAdapter> clazz,
        Properties adapterConfig, Connection connection) throws DatabaseAdapterInstantiationException {
        try {
            Constructor<? extends IDatabaseAdapter> constructor = clazz.getDeclaredConstructor(new Class[]{});
            constructor.setAccessible(true);
            T instance = (T) constructor.newInstance(new Object[]{});

            if (connection == null) {
                instance.init(adapterConfig);
            } else {
                instance.init(connection);
            }
            String hashFunction = adapterConfig.getProperty(ConfigConstants.OPTIONS_COMMON_HASH_ALGORITHM);
            if ("".equals(hashFunction)) {
                hashFunction = ConfigConstants.DEFAULT_HASH_ALGORITHM;
            }
            instance.setHashCalculator(new HashCalculator(hashFunction));

            return instance;
        } catch (NoSuchAlgorithmException ex) {
            throw new DatabaseAdapterInstantiationException(ex);
        } catch (IllegalArgumentException ex) {
            throw new DatabaseAdapterInstantiationException(ex);
        } catch (InvocationTargetException ex) {
            throw new DatabaseAdapterInstantiationException(ex);
        } catch (NoSuchMethodException ex) {
            throw new DatabaseAdapterInstantiationException(ex);
        } catch (InstantiationException ex) {
            throw new DatabaseAdapterInstantiationException(ex);
        } catch (IllegalAccessException ex) {
            throw new DatabaseAdapterInstantiationException(ex);
        }
    }

    private static void setTransactionIsolationLevel(final Connection connection) throws
        DatabaseAdapterInstantiationException {
        try {
            LOGGER.info(Infos.DATA_SETTING_TRANS_ISOLATION_LEVEL,
                "TRANSACTION_SERIALIZABLE(" + Connection.TRANSACTION_SERIALIZABLE + ")");
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            LOGGER.info(Infos.DATA_TRANS_ISOLATION_LEVEL, connection.getTransactionIsolation());
        } catch (SQLException e) {
            String msg = read(Warnings.DATA_TRANSACTION_ISOLATION_LEVEL_NOT_SERIALIZABLE);
            LOGGER.warn(msg, e);
            throw new DatabaseAdapterInstantiationException(msg, e);
        }
    }
}
