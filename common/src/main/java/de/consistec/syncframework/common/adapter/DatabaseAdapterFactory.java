package de.consistec.syncframework.common.adapter;

import static de.consistec.syncframework.common.i18n.MessageReader.read;
import static de.consistec.syncframework.common.util.Preconditions.checkNotNull;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.syncframework.common.i18n.Errors;
import de.consistec.syncframework.common.i18n.Infos;
import de.consistec.syncframework.common.i18n.Warnings;
import de.consistec.syncframework.common.util.LoggingUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.cal10n.LocLogger;

/**
 * Factory class to fabricate instances of specific implementation of
 * {@link de.consistec.syncframework.common.adapter.IDatabaseAdapter} interface.
 * Factory uses {@link de.consistec.syncframework.common.Config configuration} class to read which adapter should
 * be created.
 * <p/>
 *
 * @company Consistec Engineering and Consulting GmbH
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
     * @return IDatabaseAdapter New instance of {@link de.consistec.syncframework.common.adapter.IDatabaseAdapter}
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
     * @return IDatabaseAdapter New instance of {@link de.consistec.syncframework.common.adapter.IDatabaseAdapter}
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

            return instance;
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
