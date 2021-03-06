package de.consistec.doubleganger.common;

/*
 * #%L
 * Project - doubleganger
 * File - Config.java
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
import static de.consistec.doubleganger.common.ConfigConstants.DEFAULT_CONFLICT_STRATEGY;
import static de.consistec.doubleganger.common.ConfigConstants.DEFAULT_HASH_ALGORITHM;
import static de.consistec.doubleganger.common.ConfigConstants.DEFAULT_IS_SQL_TRIGGER_ACTIVATED;
import static de.consistec.doubleganger.common.ConfigConstants.DEFAULT_MD_TABLE_SUFFIX;
import static de.consistec.doubleganger.common.ConfigConstants.DEFAULT_NR_APPLY_CHANGES_ON_TRANS_ERR;
import static de.consistec.doubleganger.common.ConfigConstants.DEFAULT_NR_GET_CHANGES_ON_TRANS_ERR;
import static de.consistec.doubleganger.common.ConfigConstants.DEFAULT_NR_SYNC_ON_TRANS_ERR;
import static de.consistec.doubleganger.common.ConfigConstants.DEFAULT_SYNC_DIRECTION;
import static de.consistec.doubleganger.common.ConfigConstants.OPTIONS_COMMON_CLIENT_DB_ADAPTER_CLASS;
import static de.consistec.doubleganger.common.ConfigConstants.OPTIONS_COMMON_CLIENT_DB_ADAP_GROUP;
import static de.consistec.doubleganger.common.ConfigConstants.OPTIONS_COMMON_CONFLICT_ACTION;
import static de.consistec.doubleganger.common.ConfigConstants.OPTIONS_COMMON_HASH_ALGORITHM;
import static de.consistec.doubleganger.common.ConfigConstants.OPTIONS_COMMON_IS_SQL_TRIGGER_ON_CLIENT_ACTIVATED;
import static de.consistec.doubleganger.common.ConfigConstants.OPTIONS_COMMON_IS_SQL_TRIGGER_ON_SERVER_ACTIVATED;
import static de.consistec.doubleganger.common.ConfigConstants.OPTIONS_COMMON_MD_TABLE_SUFFIX;
import static de.consistec.doubleganger.common.ConfigConstants.OPTIONS_COMMON_NR_OF_APPLY_CHANGES_TRIES_ON_TRANS_ERROR;
import static de.consistec.doubleganger.common.ConfigConstants.OPTIONS_COMMON_NR_OF_GET_CHANGES_TRIES_ON_TRANS_ERROR;
import static de.consistec.doubleganger.common.ConfigConstants.OPTIONS_COMMON_NR_OF_SYNC_TRIES_ON_TRANS_ERROR;
import static de.consistec.doubleganger.common.ConfigConstants.OPTIONS_COMMON_SERV_DB_ADAPTER_CLASS;
import static de.consistec.doubleganger.common.ConfigConstants.OPTIONS_COMMON_SERV_DB_ADAP_GROUP;
import static de.consistec.doubleganger.common.ConfigConstants.OPTIONS_COMMON_SERV_PROXY;
import static de.consistec.doubleganger.common.ConfigConstants.OPTIONS_COMMON_SER_PROXY_GROUP;
import static de.consistec.doubleganger.common.ConfigConstants.OPTIONS_COMMON_SYNC_DIRECTION;
import static de.consistec.doubleganger.common.ConfigConstants.OPTIONS_COMMON_SYNC_TABLES;
import static de.consistec.doubleganger.common.i18n.MessageReader.read;
import static de.consistec.doubleganger.common.util.CollectionsUtil.newSyncSet;
import static de.consistec.doubleganger.common.util.Preconditions.checkNotNull;

import de.consistec.doubleganger.common.adapter.IDatabaseAdapter;
import de.consistec.doubleganger.common.conflict.ConflictStrategy;
import de.consistec.doubleganger.common.exception.ConfigException;
import de.consistec.doubleganger.common.i18n.Errors;
import de.consistec.doubleganger.common.i18n.Infos;
import de.consistec.doubleganger.common.server.IServerSyncProvider;
import de.consistec.doubleganger.common.util.LoggingUtil;
import de.consistec.doubleganger.common.util.PropertiesUtil;
import de.consistec.doubleganger.common.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.slf4j.cal10n.LocLogger;

/**
 * Singleton which holds configuration data for framework.
 * <p>
 * Properties can be set by mutators or loaded from properties file (or object).
 * Default configuration's file path in classpath is {@value ConfigLoader.CONFIG_FILE}.
 * Template of this file is provided with this jar package in it's root directory.
 * </p>
 *
 * @author Piotr Wieczorek
 * @company consistec Engineering and Consulting GmbH
 * @date 15.10.2012 11:19:22
 * @since 0.0.1-SNAPSHOT
 */
public final class Config {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(Config.class.getCanonicalName());
    //    private static Config instance;
    private Class<? extends IDatabaseAdapter> serverDatabaseAdapter;
    private Class<? extends IDatabaseAdapter> clientDatabaseAdapter;
    private Class<? extends IServerSyncProvider> serverProxy;
    private Set<String> syncTables = newSyncSet();
    private boolean isSqlTriggerOnServerActivated;
    private boolean isSqlTriggerOnClientActivated;
    private int retryNumberOfApplyChangesOnTransactionError;
    private int retryNumberOfGetChangesOnTransactionError;
    private String mdTableSuffix;
    private Properties serverDatabaseProperties;
    private Properties clientDatabaseProperties;
    private Properties serverProxyProviderProperties;
    private ConflictStrategy globalConflictStrategy;
    private SyncDirection globalSyncDirection;
    private int syncRetryNumber;
    private String hashAlgorithm;

    /**
     * It's singleton so no direct instance creation allowed.
     */
    private Config() {
        /**
         * Here AssertionError should be thrown, but we are using this constructor in unit tests (through reflection)
         * to reset configuration before each test method.
         */
        // here we need an initial read for the cal10n localization framework
        // otherwise we get an IOException (stream closed) if we want to read resources in.
        LOGGER.info(read(Infos.COMMON_CREATING_CONFIG));
    }

    /**
     * Loads the properties file from passed input stream and initilizes the config class attributes.
     *
     * @param inputStream the properties file as input stream
     * @throws IOException
     */
    public void init(InputStream inputStream) throws IOException {

        ConfigLoader loader = new ConfigLoader();
        Properties props = loader.loadFromFile(inputStream);
        setValues(props);
    }

    /**
     * An proxy to remote server.
     * <p/>
     * Server synchronization provider proxy is used when client is synchronizing with remote server.
     * <p/>
     *
     * @return Proxy for server synchronization provider class.
     */
    public Class<? extends IServerSyncProvider> getServerProxy() {
        return serverProxy;
    }

    /**
     * Proxy class for remote server provider.
     * <p/>
     *
     * @param serverProxy server proxy implementation.
     * @see #getServerProxy() server proxy class.
     */
    public void setServerProxy(Class<? extends IServerSyncProvider> serverProxy) {
        this.serverProxy = serverProxy;
    }

    /**
     * Default conflict strategy.
     * When there is no strategy specified for a table, this one will be used.
     *
     * @return Conflict action.
     */
    public ConflictStrategy getGlobalConflictStrategy() {
        return globalConflictStrategy;
    }

    /**
     * What action to undertake when synchronization conflict occur.
     *
     * @param globalConflictStrategy conflict action
     */
    public void setGlobalConflictStrategy(ConflictStrategy globalConflictStrategy) {
        this.globalConflictStrategy = globalConflictStrategy;
    }

    /**
     * Default sync direction.
     * Synchronization direction specifies which site of synchronization has priority over the other site.<br/>
     * When there is no direction specified in table sync strategy, this one will be used.
     *
     * @return Direction of synchronization process.
     */
    public SyncDirection getGlobalSyncDirection() {
        return globalSyncDirection;
    }

    /**
     * Default sync direction.
     * Synchronization direction specifies which site of synchronization has priority over the other site.<br/>
     * When there is no direction specified in table sync strategy, this one will be used.
     *
     * @param globalSyncDirection Synchronization direction for all tables.
     * @see #getGlobalSyncDirection()
     */
    public void setGlobalSyncDirection(SyncDirection globalSyncDirection) {
        this.globalSyncDirection = globalSyncDirection;
    }

    /**
     * Configuration options for remote server provider proxy.
     *
     * @return Configuration for server synchronization proxy providers.
     */
    public Properties getServerProxyProviderProperties() {
        if (serverProxyProviderProperties == null) {
            serverProxyProviderProperties = new Properties();
        }
        return serverProxyProviderProperties;
    }

    /**
     * Configuration options for remote server provider proxy.
     *
     * @param serverProxyProviderProperties Options for server proxy initialization.
     * @see #getServerProxyProviderProperties()
     */
    public void setServerProxyProviderProperties(Properties serverProxyProviderProperties) {
        this.serverProxyProviderProperties = serverProxyProviderProperties;
    }

    /**
     * Configuration options for database adapter used in server provider.
     *
     * @return Properties for server database connections. Used by database adapters.
     */
    public Properties getServerDatabaseProperties() {
        if (serverDatabaseProperties == null) {
            serverDatabaseProperties = new Properties();
        }
        return serverDatabaseProperties;
    }

    /**
     * Configuration options for database adapter used in server provider.
     *
     * @param serverDatabaseProperties Options for server's database adapter initialization.
     * @see #getServerDatabaseProperties()
     */
    public void setServerDatabaseProperties(Properties serverDatabaseProperties) {
        this.serverDatabaseProperties = serverDatabaseProperties;
    }

    /**
     * Configuration options for database adapter used in client provider.
     *
     * @return Properties for client database connections. Used by database adapters.
     */
    public Properties getClientDatabaseProperties() {
        if (clientDatabaseProperties == null) {
            clientDatabaseProperties = new Properties();
        }
        return clientDatabaseProperties;
    }

    /**
     * Configuration options for database adapter used in client provider.
     *
     * @param clientDatabaseProperties Options for client database adapter initialization.
     * @see #getClientDatabaseProperties()
     */
    public void setClientDatabaseProperties(Properties clientDatabaseProperties) {
        this.clientDatabaseProperties = clientDatabaseProperties;
    }

    /**
     * Return database adapter class for server side synchronization.
     *
     * @return Database adapter.
     * @see de.consistec.doubleganger.common.adapter.IDatabaseAdapter
     */
    public Class<? extends IDatabaseAdapter> getServerDatabaseAdapter() {
        return serverDatabaseAdapter;
    }

    /**
     * Sets the database adapter class for synchronization server.
     * <p/>
     *
     * @param serverDatabaseAdapter Database adapter class for server.
     */
    public void setServerDatabaseAdapter(Class<? extends IDatabaseAdapter> serverDatabaseAdapter) {
        this.serverDatabaseAdapter = serverDatabaseAdapter;
    }

    /**
     * Returns database adapter class for client side synchronization.
     *
     * @return Database adapter.
     * @see de.consistec.doubleganger.common.adapter.IDatabaseAdapter
     */
    public Class<? extends IDatabaseAdapter> getClientDatabaseAdapter() {
        return clientDatabaseAdapter;
    }

    /**
     * Sets the database adapter class for client synchronization.
     * <p/>
     *
     * @param clientDatabaseAdapter Database adapter class for client.
     */
    public void setClientDatabaseAdapter(Class<? extends IDatabaseAdapter> clientDatabaseAdapter) {
        this.clientDatabaseAdapter = clientDatabaseAdapter;
    }

    /**
     * Unmodifiable Set of tables names for synchronizing.
     * Any attempt to modify this set will result in {@link UnsupportedOperationException}.
     *
     * @return Synchronized set of tables names
     * @see java.util.Collections#unmodifiableSet(java.util.Set)
     */
    public Set<String> getSyncTables() {
        return Collections.unmodifiableSet(syncTables);
    }

    /**
     * Adds names of tables to be synchronized.
     * <p/>
     *
     * @param tablesNames Names of tables.
     * @return true if syncTables set changed as a result of the call
     */
    public boolean addSyncTable(String... tablesNames) {
        for (String name : tablesNames) {
            checkNotNull(name, read(Errors.COMMON_TABLE_NAME_IS_NULL));
        }
        return syncTables.addAll(Arrays.asList(tablesNames));
    }

    /**
     * Removes tables names from set of synchronized tables.
     * <p/>
     *
     * @param tablesNames Names of tables.
     * @return true if syncTables set changed as a result of the call
     */
    public boolean removeSyncTable(String... tablesNames) {
        return syncTables.removeAll(Arrays.asList(tablesNames));
    }

    /**
     * Returns number of synchronization tries when transaction error happens.
     *
     * @return Number of synchronization tries.
     */
    public int getSyncRetryNumber() {
        return syncRetryNumber;
    }

    /**
     * Sets number of synchronization tries when transaction error happens.
     *
     * @param retrySyncNumber Number of retries.
     * @see #getSyncRetryNumber()
     */
    public void setSyncRetryNumber(int retrySyncNumber) {
        this.syncRetryNumber = retrySyncNumber;
    }

    /**
     * Returns number of attempts to get server changes when transaction error happens.
     * <p/>
     *
     * @return Number of attempts to get server changes.
     */
    public int getRetryNumberOfGetChangesOnTransactionError() {
        return retryNumberOfGetChangesOnTransactionError;
    }

    /**
     * Sets the number of attempts to get the server changes when transaction error happens.
     * <p/>
     *
     * @param retryNumberOfGetChangesOnTransactionError Number of retries.
     */
    public void setRetryNumberOfGetChangesOnTransactionError(int retryNumberOfGetChangesOnTransactionError) {
        this.retryNumberOfGetChangesOnTransactionError = retryNumberOfGetChangesOnTransactionError;
    }

    /**
     * Returns number of attempts to apply changes on server when transaction error happens.
     *
     * @return number of attempts.
     */
    public int getRetryNumberOfApplyChangesOnTransactionError() {
        return retryNumberOfApplyChangesOnTransactionError;
    }

    /**
     * Sets the number of attempts to apply changes on server when transaction error happens.
     *
     * @param retryNumberOfApplyChangesOnTransactionError Number of retries.
     * @see #getRetryNumberOfApplyChangesOnTransactionError()
     */
    public void setRetryNumberOfApplyChangesOnTransactionError(int retryNumberOfApplyChangesOnTransactionError) {
        this.retryNumberOfApplyChangesOnTransactionError = retryNumberOfApplyChangesOnTransactionError;
    }

    /**
     * Returns suffix for hash tables.
     *
     * @return tables suffix
     */
    public String getMdTableSuffix() {
        return mdTableSuffix;
    }

    /**
     * Sets the suffix for hash tables.
     * <p/>
     *
     * @param mdTableSuffix Suffix for checksum tables.
     */
    public void setMdTableSuffix(String mdTableSuffix) {
        this.mdTableSuffix = mdTableSuffix;
    }

    /**
     * Returns true if the SQL triggers are activated on the server.
     *
     * @return boolean isSqlTriggerOnServerActivated
     */
    public boolean isSqlTriggerOnServerActivated() {
        return isSqlTriggerOnServerActivated;
    }

    /**
     * Sets the SQL triggers activation on the server.
     * <p/>
     *
     * @param active de/activate the sql triggers
     */
    public void setSqlTriggerOnServerActivated(boolean active) {
        this.isSqlTriggerOnServerActivated = active;
    }

    /**
     * Returns true if the SQL triggers are activated on the client.
     *
     * @return boolean isSqlTriggerOnServerActivated
     */
    public boolean isSqlTriggerOnClientActivated() {
        return isSqlTriggerOnClientActivated;
    }

    /**
     * Sets the SQL triggers activation on the client.
     * <p/>
     *
     * @param active de/activate the sql triggers
     */
    public void setSqlTriggerOnClientActivated(boolean active) {
        this.isSqlTriggerOnClientActivated = active;
    }

    /**
     * Returns the value of the hash algorithm (Default: "MD5").
     *
     * @return String hashAlgorithm
     */
    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    /**
     * Sets the global hash algorithm (see {@link Secrity.getProviders()} for options).
     * <p/>
     *
     * @param hashAlgorithm the hash algorithm (default "MD5")
     */
    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    /**
     * Returns a single instance of Config class.
     * If there is no instance yet, it will be created.
     * <p/>
     *
     * @return instance of framework configuration class
     */
    public static Config getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private void setValues(final Properties props) {
        prepareAdaptersConfig(props);
        prepareAdapters(props);
        prepareCommonConfig(props);
    }

    /**
     * Loads the values from Properties object to appropriate fields.
     * <p/>
     *
     * @param props
     */
    private void prepareCommonConfig(final Properties props) {

        hashAlgorithm = PropertiesUtil.defaultIfNull(DEFAULT_HASH_ALGORITHM, PropertiesUtil.readString(props,
            OPTIONS_COMMON_HASH_ALGORITHM, false));
        LOGGER.info(Infos.CONFIG_OPTION_LOADED, OPTIONS_COMMON_HASH_ALGORITHM, hashAlgorithm);

        mdTableSuffix = PropertiesUtil.defaultIfNull(DEFAULT_MD_TABLE_SUFFIX, PropertiesUtil.readString(props,
            OPTIONS_COMMON_MD_TABLE_SUFFIX, false));
        LOGGER.info(Infos.CONFIG_OPTION_LOADED, OPTIONS_COMMON_MD_TABLE_SUFFIX, mdTableSuffix);

        retryNumberOfApplyChangesOnTransactionError = PropertiesUtil.defaultIfNull(
            DEFAULT_NR_APPLY_CHANGES_ON_TRANS_ERR,
            PropertiesUtil.readNumber(props, OPTIONS_COMMON_NR_OF_APPLY_CHANGES_TRIES_ON_TRANS_ERROR,
            false,
            Integer.class));
        LOGGER.info(Infos.CONFIG_OPTION_LOADED, OPTIONS_COMMON_NR_OF_APPLY_CHANGES_TRIES_ON_TRANS_ERROR,
            retryNumberOfApplyChangesOnTransactionError);

        retryNumberOfGetChangesOnTransactionError = PropertiesUtil.defaultIfNull(DEFAULT_NR_GET_CHANGES_ON_TRANS_ERR,
            PropertiesUtil.readNumber(props, OPTIONS_COMMON_NR_OF_GET_CHANGES_TRIES_ON_TRANS_ERROR,
            false,
            Integer.class));
        LOGGER.info(Infos.CONFIG_OPTION_LOADED, OPTIONS_COMMON_NR_OF_GET_CHANGES_TRIES_ON_TRANS_ERROR,
            retryNumberOfGetChangesOnTransactionError);

        try {
            globalConflictStrategy = PropertiesUtil.defaultIfNull(DEFAULT_CONFLICT_STRATEGY,
                PropertiesUtil.readEnum(props,
                OPTIONS_COMMON_CONFLICT_ACTION, false,
                ConflictStrategy.class));
            LOGGER.info(Infos.CONFIG_OPTION_LOADED, OPTIONS_COMMON_CONFLICT_ACTION, globalConflictStrategy.name());

        } catch (Exception ex) {
            throw new ConfigException(read(Errors.CONFIG_CANT_LOAD_OPTION, OPTIONS_COMMON_CONFLICT_ACTION), ex);
        }

        try {
            syncTables = (Set<String>) PropertiesUtil.readCollection(props, OPTIONS_COMMON_SYNC_TABLES, true,
                HashSet.class);  //NOSONAR
            LOGGER.info(Infos.CONFIG_OPTION_LOADED, OPTIONS_COMMON_SYNC_TABLES, syncTables);
        } catch (Exception ex) {
            throw new ConfigException(read(Errors.CONFIG_CANT_LOAD_OPTION, OPTIONS_COMMON_SYNC_TABLES), ex);
        }

        try {
            globalSyncDirection = PropertiesUtil.defaultIfNull(DEFAULT_SYNC_DIRECTION, PropertiesUtil.readEnum(props,
                OPTIONS_COMMON_SYNC_DIRECTION, false,
                SyncDirection.class));
            LOGGER.info(Infos.CONFIG_OPTION_LOADED, OPTIONS_COMMON_SYNC_DIRECTION, globalSyncDirection.name());
        } catch (Exception ex) {
            throw new ConfigException(read(Errors.CONFIG_CANT_LOAD_OPTION, OPTIONS_COMMON_SYNC_DIRECTION), ex);
        }

        syncRetryNumber = PropertiesUtil.defaultIfNull(DEFAULT_NR_SYNC_ON_TRANS_ERR, PropertiesUtil.readNumber(props,
            OPTIONS_COMMON_NR_OF_SYNC_TRIES_ON_TRANS_ERROR, false,
            Integer.class));
        LOGGER.info(Infos.CONFIG_OPTION_LOADED, OPTIONS_COMMON_NR_OF_SYNC_TRIES_ON_TRANS_ERROR, syncRetryNumber);
    }

    /**
     * This methods builds Properties object for adapters, from entries in given <i>props</i> object.
     * <p/>
     *
     * @param props
     */
    private void prepareAdaptersConfig(final Properties props) {

        isSqlTriggerOnServerActivated = PropertiesUtil.defaultIfNull(DEFAULT_IS_SQL_TRIGGER_ACTIVATED,
            PropertiesUtil.readBoolean(props, OPTIONS_COMMON_IS_SQL_TRIGGER_ON_SERVER_ACTIVATED, false));
        LOGGER.info(Infos.CONFIG_OPTION_LOADED, OPTIONS_COMMON_IS_SQL_TRIGGER_ON_SERVER_ACTIVATED,
            isSqlTriggerOnServerActivated);
        isSqlTriggerOnClientActivated = PropertiesUtil.defaultIfNull(DEFAULT_IS_SQL_TRIGGER_ACTIVATED,
            PropertiesUtil.readBoolean(props, OPTIONS_COMMON_IS_SQL_TRIGGER_ON_CLIENT_ACTIVATED, false));
        LOGGER.info(Infos.CONFIG_OPTION_LOADED, OPTIONS_COMMON_IS_SQL_TRIGGER_ON_CLIENT_ACTIVATED,
            isSqlTriggerOnClientActivated);


        for (String key : props.stringPropertyNames()) {

            if (key.startsWith(OPTIONS_COMMON_SERV_DB_ADAP_GROUP) && !key.equals(
                OPTIONS_COMMON_SERV_DB_ADAPTER_CLASS)) {
                getServerDatabaseProperties().put(key.substring(OPTIONS_COMMON_SERV_DB_ADAP_GROUP.length() + 1),
                    props.getProperty(key));
            }

            if (key.startsWith(OPTIONS_COMMON_CLIENT_DB_ADAP_GROUP) && !key.equals(
                OPTIONS_COMMON_CLIENT_DB_ADAPTER_CLASS)) {
                getClientDatabaseProperties().put(key.substring(OPTIONS_COMMON_CLIENT_DB_ADAP_GROUP.length() + 1),
                    props.getProperty(key));
            }

            if (key.startsWith(OPTIONS_COMMON_SER_PROXY_GROUP) && !key.equals(OPTIONS_COMMON_SERV_PROXY)) {
                getServerProxyProviderProperties().put(key.substring(OPTIONS_COMMON_SER_PROXY_GROUP.length() + 1),
                    props.getProperty(key));
            }
        }
    }

    /**
     * Loads adapters and proxy classes.
     * <p/>
     *
     * @param props
     */
    private void prepareAdapters(final Properties props) {

        String msg;
        try {

            if (!StringUtil.isNullOrEmpty(props.getProperty(OPTIONS_COMMON_SERV_DB_ADAPTER_CLASS))) {
                serverDatabaseAdapter = PropertiesUtil.<Class<IDatabaseAdapter>>readClass(props,
                    OPTIONS_COMMON_SERV_DB_ADAPTER_CLASS, false);
                LOGGER.info(Infos.CONFIG_OPTION_LOADED, OPTIONS_COMMON_SERV_DB_ADAPTER_CLASS,
                    serverDatabaseAdapter.getCanonicalName());
            }

        } catch (ClassNotFoundException ex) {
            msg = read(Errors.CONFIG_CANT_LOAD_SERVER_DB_ADAPTER);
            LOGGER.error(msg, ex);
            throw new ConfigException(msg, ex);
        }

        try {

            if (!StringUtil.isNullOrEmpty(props.getProperty(OPTIONS_COMMON_CLIENT_DB_ADAPTER_CLASS))) {
                clientDatabaseAdapter = PropertiesUtil.<Class<IDatabaseAdapter>>readClass(props,
                    OPTIONS_COMMON_CLIENT_DB_ADAPTER_CLASS, false);
                LOGGER.info(Infos.CONFIG_OPTION_LOADED, OPTIONS_COMMON_CLIENT_DB_ADAPTER_CLASS,
                    clientDatabaseAdapter.getCanonicalName());
            }

        } catch (ClassNotFoundException ex) {
            msg = read(Errors.CONFIG_CANT_LOAD_CLIENT_DB_ADAPTER);
            LOGGER.error(msg, ex);
            throw new ConfigException(msg, ex);
        }

        try {
            if (!StringUtil.isNullOrEmpty(props.getProperty(OPTIONS_COMMON_SERV_PROXY))) {
                serverProxy = PropertiesUtil.<Class<IServerSyncProvider>>readClass(props, OPTIONS_COMMON_SERV_PROXY,
                    false);
            }
        } catch (ClassNotFoundException ex) {
            msg = read(Errors.CONFIG_CANT_LOAD_SERVER_PROXY);
            LOGGER.error(msg, ex);
            throw new ConfigException(msg, ex);
        }
    }

    /**
     * Prints common options in form <i>"{ optionName=optionValue, optionName=null .... }"</i>.
     * Returned String representation is a subject of changes so it should not be parsed!
     * <p/>
     *
     * @return String Brief description of the configuration state (only common options are printed)
     */
    @Override
    public String toString() {

        final String nullString = "null";
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("{");
        builder.append("\n serverDatabaseAdapter=");
        builder.append(serverDatabaseAdapter == null ? nullString : serverDatabaseAdapter.getSimpleName());
        builder.append(",\n clientDatabaseAdapter=");
        builder.append(clientDatabaseAdapter == null ? nullString : clientDatabaseAdapter.getSimpleName());
        builder.append(",\n serverSyncProvider=");
        builder.append(serverProxy == null ? nullString : serverProxy.getSimpleName());
        builder.append(",\n nr_of_syncTables=");
        builder.append(syncTables.size());
        builder.append(",\n retryNumberOfApplyChangesOnTransactionError=");
        builder.append(retryNumberOfApplyChangesOnTransactionError);
        builder.append(",\n mdTableSuffix=");
        builder.append(mdTableSuffix);
        builder.append(",\n conflictStrategy=");
        builder.append(globalConflictStrategy.name());
        builder.append(",\n syncRetryNumber=");
        builder.append(syncRetryNumber);
        builder.append(" }");
        return builder.toString();
    }

    // inner private class which will be initialized during access through the surrounded class.
    private static final class InstanceHolder {
        // The initialization of fields is done only once und will be implizit
        // synchronized through the ClassLoader

        static final Config INSTANCE = new Config();
    }
}
