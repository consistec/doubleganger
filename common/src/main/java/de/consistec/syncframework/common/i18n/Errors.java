package de.consistec.syncframework.common.i18n;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

/**
 * List of error messages used in framework.
 *
 * @author Markus
 * @author Piotr Wieczorek
 * @company Consistec Engineering and Consulting GmbH
 * @date 26.06.2012 14:02
 * @since 0.0.1-SNAPSHOT
 */
@BaseName("de/consistec/syncframework/common/i18n/errors")
@LocaleData({@Locale("en") })
public enum Errors {

    //<editor-fold defaultstate="expanded" desc="***************** Common errors *****************" >
    /**
     * Message for problems during synchronization of client tables.
     */
    COMMON_SYNCHRONIZE_CLIENT_TABLE_FAILED,
    /**
     * Message for failures in applying changes on database.
     * <p/>
     */
    COMMON_APPLY_CHANGES_FAILED,
    /**
     * Message for failures in loading hash algorithm.
     */
    COMMON_LOADING_HASHALGORITHM_FAILED,
    /**
     * Message for failures in synchronization of data with hash tables.
     */
    COMMON_SYNC_OF_DATA_AND_HASH_FAILED,
    /**
     * Message if no {@link de.consistec.syncframework.common.IConflictListener conflict listener} was found.
     */
    COMMON_NO_CONFLICT_LISTENER_FOUND,
    /**
     * Message for errors when property key can't be found in {@link java.util.Properties Properties} object.
     * <p>
     * <b>Parameters:</b> property name.
     * </p>
     */
    COMMON_PROPERTY_NOT_FOUND,
    /**
     * Message for situation when client cant push it changes to server without to fetch changes from server earlier.
     */
    COMMON_UPDATE_NECESSARY,
    /**
     * Message for situations when creation of IClientSyncProvider or IServerSyncProvider instance fails.
     */
    COMMON_SYNC_PROVIDER_INSTANTIATION_FAILED,
    /**
     * When provided (e.g as method parameter)
     * {@link de.consistec.syncframework.common.ISyncProgressListener ISyncProgressListener} has {@code null} value.
     */
    COMMON_PROVIDED_PROGRESS_LISTENER_NOT_INITIALIZED,
    /**
     * When provided (e.g as method parameter)
     * {@link de.consistec.syncframework.common.IConflictListener IConflictListener} has {@code null} value.
     */
    COMMON_PROVIDED_CONFLICT_LISTENER_NOT_INITIALIZED,
    /**
     * When synchronization direction has {@code null} value.
     */
    COMMON_SYNC_DIRECTION_CANT_BE_NULL,
    /**
     * When conflict action has {@code null} value.
     */
    COMMON_CONFLICT_ACTION_CANT_BE_NULL,
    /**
     * When client sync provider fails to obtain the list of client changes.
     */
    COMMON_CANT_GET_CLIENT_CHANGES,
    /**
     * When update of client's revisions with server's revisions fails.
     */
    COMMON_CANT_UPDATE_CLIENT_REVISIONS,
    /**
     * When provided server synchronization object has {@code null} value.
     */
    COMMON_SERVER_PROVIDER_NOT_INITIALIZED,
    /**
     * When provided CLIENT synchronization object has {@code null} value.
     */
    COMMON_CLIENT_PROVIDER_NOT_INITIALIZED,
    /**
     * When problems with closing the sync providers occurs.
     */
    COMMON_ERROR_WHILE_CLOSING_PROVIDERS,
    /**
     * When ADD-ADD conflict occurs on the server, while it shouldn't.
     */
    COMMON_ADD_ADD_CONFLICT_SHOULDNT_OCCUR,
    /**
     * When OUT-OF-DATE conflict occurs on the server, while it shouldn't.
     */
    COMMON_OUT_OF_DATE_SHOULDNT_OCCUR,
    /**
     * When trying to create ServerStatus instance from wrong status code.
     */
    COMMON_UNKNOWN_SERVER_STATUS_CODE,
    /**
     * When an attempt to apply client changes on the server database fails.
     * <p>
     * <b>Parameters</b>: attempts counter.
     * </p>
     */
    COMMON_CANT_APPLY_CLIENT_CHANGES_FOR_N_TIME,
    /**
     * When operation invoked on a table name that does not exists in a set of
     * {@link de.consistec.syncframework.common.Config#getSyncTables() synchronized tables} of framework's configuration.
     * <p>
     * <b>Parameters</b>: table name.
     * </p>
     */
    COMMON_TABLE_NOT_INTEND_FOR_SYNCHRONIZING,
    /**
     * When columns on table X on client and on server are different.
     * <p>
     * <b>Parameters</b>: table name.
     * </p>
     */
    COMMON_CLIENT_COLUMNS_AND_SERVER_COLUMN_FOR_TABLE_DONT_MATCH,
    /**
     * When attempt to get server changes fails.
     * <p>
     * <b>Parameters</b>: attempts counter.
     * </p>
     */
    COMMON_CANT_GET_SERVER_CHANGES_FOR_N_TIME,
    /**
     * When provided table name (e.g method parameter) has a {@code null} value.
     */
    COMMON_TABLE_NAME_IS_NULL,
    /**
     * When provided {@link java.io.InputStream InputStream} (e.g method parameter) has a {@code null} value.
     */
    COMMON_INPUT_STREAM_IS_NULL,
    /**
     * When converting db schema to xml fails.
     */
    COMMON_CANT_CONVERT_DB_SCHEMA_TO_XML,
    /**
     * When creation of xml parser fails.
     * <p/>
     *
     * @see javax.xml.parsers.DocumentBuilderFactory#newInstance()
     */
    COMMON_CANT_CREATE_XML_PARSER,
    /**
     * When xml validation fails.
     */
    COMMON_XML_VALIDATION_FAILED,
    /**
     * When can't parse xml document.
     */
    COMMON_CANT_PARSE_XML_DOCUMENT,
    /**
     * When cant convert a value to a desired type.
     * <p>
     * <b>Parameters</b>: type name.
     * </p>
     */
    COMMON_CANT_CONVERT_TO_TYPE,
    /**
     * When provided {@link de.consistec.syncframework.common.data.schema.Column Column} or its {@link String name} has
     * a {@code null} value.
     * <p/>
     *
     * @see de.consistec.syncframework.common.data.schema.Table#add(de.consistec.syncframework.common.data.schema.Column[])
     */
    COMMON_PROVIDED_COLUMN_IS_NULL,
    /**
     * When provided {@link de.consistec.syncframework.common.data.schema.Constraint Constraint} has a {@code null} value.
     * <p/>
     *
     * @see de.consistec.syncframework.common.data.schema.Table#add(de.consistec.syncframework.common.data.schema.Constraint[])
     */
    COMMON_PROVIDED_CONSTRAINT_IS_NULL,
    /**
     * When frameworks initialization fails.
     */
    COMMON_CANT_INIT_FRAMEWORK,
    /**
     * When invoking an operation and the frameworks structures were not initialized.
     */
    COMMON_FRAMEWORK_NOT_INITIALIZED,
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="***************** Errors related to configuration *****************" >

    /**
     * Message for problems with loading server's database adapter class.
     */
    CONFIG_CANT_LOAD_SERVER_DB_ADAPTER,
    /**
     * Message for problems with loading client's database adapter class.
     */
    CONFIG_CANT_LOAD_CLIENT_DB_ADAPTER,
    /**
     * Message for problems with loading server proxy class.
     */
    CONFIG_CANT_LOAD_SERVER_PROXY,
    /**
     * Message for problems with loading configuration option.
     */
    CONFIG_CANT_LOAD_OPTION,
    /**
     * When cant find option key in configuration file.
     * <p>
     * <b>Parameter</b>: option key.
     * </p>
     */
    CONFIG_OPTION_IS_MISSING,
    /**
     * When can convert option value to boolean value.
     * <p>
     * <b>Parameter</b>: option key.
     * </p>
     */
    CONFIG_OPTION_IS_NOT_BOOLEAN,
    /**
     * When server proxy is required
     * (e.g. {@link de.consistec.syncframework.common.SyncContext.ClientContext ClientContext}) but not specified.
     */
    CONFIG_NO_SERVER_PROXY_SPECIFIED,
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="*************** Messages related to data layer. ***************" >
    /**
     * Message for failures in rolling back database transaction.
     */
    DATA_TRANSACTION_ROLLBACK_FAILED,
    /**
     * Message for failures in applying changes on database.
     */
    DATA_ENABLE_AUTO_COMMIT_FAILED,
    /**
     * Message for failures in accessing the database.
     */
    DATA_DATABASE_ACCESS_FAILED,
    /**
     * Message provided type is unsupported by underlying databases or driver.
     * <p>
     * Parameters: data type name.
     * </p>
     */
    DATA_DATATYPE_NOT_SUPPORTED,
    /**
     * Message for situations when creation of IDatabaseAdapter instance fails.
     */
    DATA_DB_ADAPTER_INSTANTIATION,
    /**
     * Message for situations when close() method on database connection fails.
     */
    DATA_CLOSE_CONNECTION_FAILED,
    /**
     * When database connection has {@code null} value.
     */
    DATA_CONNECTON_NOT_INITIALIZED,
    /**
     * When provided {@link javax.sql.DataSource DataSource} has {@code null} value.
     */
    DATA_EXTERNAL_DATA_SOURCE_NOT_INITIALIZED,
    /**
     * Message for all other database/database adapters errors, not listed here.
     */
    DATA_GENERIC_ERROR,
    /**
     * When can't determine if database adapter should be used with client or with server provider.
     * <p/>
     *
     * @see de.consistec.syncframework.common.adapter.DatabaseAdapterFactory.AdapterPurpose
     */
    DATA_UNKNOWN_ADAPTER_PURPOSE,
    /**
     * When problems with database transaction occurs.
     */
    DATA_PROBLEMS_WITH_TRANSACTION,
    /**
     * When database adapter cat read columns names for provided table.
     * <p>
     * <b>Parameters</b>: table name.
     * </p>
     */
    DATA_CANT_LOAD_COLUMNS_FOR_TABLE,
    /**
     * When provided SQL type is not supported.
     * <p/>
     *
     * @see de.consistec.syncframework.common.util.SQLTypesUtil#sizeType(int)
     */
    DATA_NOT_SUPPORTED_SQL_TYPE,
    /**
     * When exception occurs while getting the {@link java.sql.Connection connection} from
     * {@link javax.sql.DataSource DataSource}.
     */
    DATA_CANT_GET_CONNECTION_FROM_PROVIDED_DATASOURCE,
    /**
     * This conflict strategy is not supported for the specified sync direction.
     * Example: client->server and server.wins
     */
    NOT_SUPPORTED_CONFLICT_STRATEGY,

    /**
     * This is an implementation error.
     * The conflict should never occur due to the configured sync direction and conflict strategy.
     */
    CONFLICT_CAN_NOT_HAPPEN,
    /**
     * Synctables which are configured on client side contains table(s) which are not
     * configured on server side.
     */
    COMMON_SYNCTABLE_SETTINGS_ERROR,

    /**
     * The sync strategy from client differ from servers sync strategy.
     */
    COMMON_NOT_IDENTICAL_SYNCSTRATEGY,
    /**
     * synced changes from client to server with sync direction SERVER_TO_CLIENT are not allowed.
     */
    COMMON_NO_CLIENTCHANGES_ALLOWED_TO_SYNC_FOR_TABLE,
    /**
     * synced changes from client to server with sync direction SERVER_TO_CLIENT are not allowed.
     */
    COMMON_NO_CLIENTCHANGES_ALLOWED_TO_SYNC,
    /**
     * synced changes from server to client with sync direction CLIENT_TO_SERVER are not allowed.
     */
    COMMON_NO_SERVERCHANGES_ALLOWED_TO_SYNC_FOR_TABLE,
    /**
     * synced changes from server to client with sync direction CLIENT_TO_SERVER are not allowed.
     */
    COMMON_NO_SERVERCHANGES_ALLOWED_TO_SYNC;

    //</editor-fold>
}
