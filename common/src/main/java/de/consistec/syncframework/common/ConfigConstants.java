package de.consistec.syncframework.common;

import de.consistec.syncframework.common.conflict.ConflictStrategy;

/**
 * This class contains all constants used in {@code Config} class.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 10.01.13 15:59
 */
public final class ConfigConstants {

//<editor-fold defaultstate="expanded" desc=" Class fields " >

    /**
     * Delimiter for multivalued options strings, like SYNC_TABLES.<br/>
     * Value: {@value}
     */
    public static final String DELIMITER = ",";
    /**
     * Default suffix for md tables.<br/>
     * Value: {@value}
     */
    public static final String DEFAULT_MD_TABLE_SUFFIX = "_md";
    /**
     * Default number of synchronization tries when transaction error occurs.<br/>
     * Value: {@value}
     */
    public static final int DEFAULT_NR_SYNC_ON_TRANS_ERR = 3;
    /**
     * Default number of tries to apply changes on server side.<br/>
     * Value: {@value}
     */
    public static final int DEFAULT_NR_APPLY_CHANGES_ON_TRANS_ERR = 3;
    /**
     * Default number of tries to get the server changes.<br/>
     * Value: {@value}
     */
    public static final int DEFAULT_NR_GET_CHANGES_ON_TRANS_ERR = 3;
    /**
     * Default conflict action.<br/>
     * Value: ConflictStrategy.SERVER_WINS
     */
    public static final ConflictStrategy DEFAULT_CONFLICT_STRATEGY = ConflictStrategy.SERVER_WINS;
    /**
     * Default synchronization direction.<br/>
     * Value: SyncDirection.SERVER_TO_CLIENT
     */
    public static final SyncDirection DEFAULT_SYNC_DIRECTION = SyncDirection.BIDIRECTIONAL;

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Config options names in config file" >
    /**
     * Key prefix for common options.
     * <p/>
     * Value: {@value}
     */
    public static final String OPTIONS_COMMON_PREFIX = "framework";
    /**
     * Key prefix for server options.
     * <p/>
     * Value: {@value}
     */
    public static final String OPTIONS_SERVER_PREFIX = OPTIONS_COMMON_PREFIX + ".server";
    /**
     * Key prefix for client options.
     * <p/>
     * Value: {@value}
     */
    public static final String OPTIONS_CLIENT_PREFIX = OPTIONS_COMMON_PREFIX + ".client";
    /**
     * Key part for database adapter groups.
     * <p/>
     * All options from this group are used to create {@link Config.getServerDatabaseProperties() }
     * (if this value is preceded with {@value #OPTIONS_SERVER_PREFIX} ) or
     * {@link Config.getClientDatabaseProperties() } (if this value is preceded with {@value #OPTIONS_CLIENT_PREFIX} ).
     * <br/>
     * Value: {@value}
     */
    public static final String OPTIONS_DB_ADAPTER = ".db_adapter";
    /**
     * Key part for proxy provider group.
     * <p/>
     * All options from this group are used to create {@link Config.getServerProxyProviderProperties() }.
     * <br/>
     * Value: {@value}
     */
    public static final String OPTIONS_PROXY_PROVIDER = ".proxy_provider";
    // -------------- common framework options
    /**
     * Key for conflict {@link Config.getGlobalConflictStrategy() }.
     * <p/>
     * Value: {@value}
     */
    public static final String OPTIONS_COMMON_CONFLICT_ACTION = OPTIONS_COMMON_PREFIX + ".conflict_action";
    /**
     * Key for synchronization {@link Config.getGlobalSyncDirection() }.
     * <p/>
     * Value: {@value}
     */
    public static final String OPTIONS_COMMON_SYNC_DIRECTION = OPTIONS_COMMON_PREFIX + ".sync_direction";
    /**
     * Key for list of synchronized tables.
     * <p/>
     * Value: {@value}
     */
    public static final String OPTIONS_COMMON_SYNC_TABLES = OPTIONS_COMMON_PREFIX + ".sync_tables";
    /**
     * Key for {@link Config.getRetryNumberOfApplyChangesOnTransactionError() } value.
     * <p/>
     * Value: {@value}
     */
    public static final String OPTIONS_COMMON_NR_OF_APPLY_CHANGES_TRIES_ON_TRANS_ERROR = OPTIONS_SERVER_PREFIX
        + ".number_of_apply_changes_tries_on_transaction_error";
    /**
     * Key for {@link Config.getRetryNumberOfApplyChangesOnTransactionError() } value.
     * <p/>
     * Value: {@value}
     */
    public static final String OPTIONS_COMMON_NR_OF_GET_CHANGES_TRIES_ON_TRANS_ERROR = OPTIONS_SERVER_PREFIX
        + ".number_of_get_changes_tries_on_transaction_error";
    /**
     * Key for {@link Config.getSyncRetryNumber() } value.
     * <p/>
     * Value: {@value}
     */
    public static final String OPTIONS_COMMON_NR_OF_SYNC_TRIES_ON_TRANS_ERROR = OPTIONS_CLIENT_PREFIX
        + ".number_of_sync_tries_on_transaction_error";
    /**
     * Key for {@link Config.getMdTableSuffix() } value.
     * <p/>
     * Value: {@value}
     */
    public static final String OPTIONS_COMMON_MD_TABLE_SUFFIX = OPTIONS_COMMON_PREFIX + ".md_table_suffix";
    /**
     * Key for options which form {@link Config.getServerProxyProviderProperties() } object.
     * <p/>
     * Value: {@value}
     */
    public static final String OPTIONS_COMMON_SERV_DB_ADAP_GROUP = OPTIONS_SERVER_PREFIX + OPTIONS_DB_ADAPTER;
    /**
     * Key for options which form {@link Config.getClientDatabaseProperties() } object.
     * <p/>
     * Value: {@value}
     */
    public static final String OPTIONS_COMMON_CLIENT_DB_ADAP_GROUP = OPTIONS_CLIENT_PREFIX + OPTIONS_DB_ADAPTER;
    /**
     * Key for options which form {@link Config.getServerProxyProviderProperties() } object.
     * <p/>
     * Value: {@value}
     */
    public static final String OPTIONS_COMMON_SER_PROXY_GROUP = OPTIONS_SERVER_PREFIX + OPTIONS_PROXY_PROVIDER;

    /**
     * CHECKSTYLE:OFF
     */
    /**
     * Key for {@link Config.getServerDatabaseAdapter() } class.
     * <p/>
     * Value: {@value}
     */
    public static final String OPTIONS_COMMON_SERV_DB_ADAPTER_CLASS = OPTIONS_COMMON_SERV_DB_ADAP_GROUP + ".class";
    /**
     * Key for {@link Config.getClientDatabaseAdapter() } class.
     * <p/>
     * Value: {@value}
     */
    public static final String OPTIONS_COMMON_CLIENT_DB_ADAPTER_CLASS = OPTIONS_COMMON_CLIENT_DB_ADAP_GROUP + ".class";
    /**
     * Key for {@link Config.getServerProxy() } class.
     * <p/>
     * Value: {@value}
     */
    public static final String OPTIONS_COMMON_SERV_PROXY = OPTIONS_COMMON_SER_PROXY_GROUP + ".class";
//</editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class constructors " >
    private ConfigConstants() {
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class methods " >

//</editor-fold>

}