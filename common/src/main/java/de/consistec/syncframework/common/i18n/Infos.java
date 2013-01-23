package de.consistec.syncframework.common.i18n;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

/**
 * List of info messages used in framework.
 * <p/>
 *
 * @author Piotr Wieczorek
 * @company Consistec Engineering and Consulting GmbH
 * @date 30.11.2012 12:16:31
 * @since 0.0.1-SNAPSHOT
 */
@BaseName("de/consistec/syncframework/common/i18n/infos")
@LocaleData(value = {
    @Locale("en") })
public enum Infos {

    //<editor-fold defaultstate="expanded" desc="***************** Common messages *****************" >
    /**
     * Informs that initializing procedure for client was invoked.
     */
    COMMON_FRAMEWORK_INITIALIZED_CLIENT,
    /**
     * Informs that initializing procedure for server was invoked.
     */
    COMMON_FRAMEWORK_INITIALIZED_SERVER,
    /**
     * When client change was added to client changeset.
     * <p>
     * <b>Parameter</b>: change description.
     * </p>
     */
    COMMON_ADDED_CLIENT_CHANGE_TO_CHANGE_SET,
    /**
     * When server change was added to server changeset.
     * <p>
     * <b>Parameter</b>: change description.
     * </p>
     */
    COMMON_ADDED_SERVER_CHANGE_TO_CHANGE_SET,
    /**
     * Informs about new server site revision.
     * <p>
     * <b>Parameter</b>: new revision.
     * </p>
     */
    COMMON_NEW_SERVER_REVISION,
    /**
     * When detected change is the data was deleted by client.
     */
    COMMON_CLIENT_DELETED_CASE_DETECTED,
    /**
     * When detected change is the data was modified by client.
     */
    COMMON_CLIENT_MODIFIED_CASE_DETECTED,
    /**
     * When detected change is the data was added by client.
     */
    COMMON_CLIENT_ADDED_CASE_DETECTED,
    /**
     * When updating server hash entry.
     */
    COMMON_UPDATING_SERVER_HASH_ENTRY,
    /**
     * When creating new hash entry in server database.
     */
    COMMON_CREATING_NEW_SERVER_HASH_ENTRY,
    /**
     * When updating client hash entry.
     */
    COMMON_UPDATING_CLIENT_HASH_ENTRY,
    /**
     * When creating new hash entry in client database.
     */
    COMMON_CREATING_NEW_CLIENT_HASH_ENTRY,
    /**
     * When deleted row is found on client.
     */
    COMMON_FOUND_DELETED_ROW_ON_CLIENT,
    /**
     * When deleted row is found on server.
     */
    COMMON_FOUND_DELETED_ROW_ON_SERVER,
    /**
     * When retrying to synchronize.
     */
    COMMON_SYNCHRONIZING_AGAIN,
    /**
     * Prints current number of synchronization attempts.
     * <p>
     * <b>Parameter</b>: number of attempts.
     * </p>
     */
    COMMON_NUMBER_OF_SYNC_RETRIES,
    /**
     * After successfully applied client changes, server returns new revision to client.
     * <p>
     * <b>Parameter</b>: new server revision.
     * </p>
     */
    COMMON_SENDING_NEW_REVISION_TO_CLIENT,
    /**
     * Informs about attempt to reapply client changes on the server database.
     */
    COMMON_TRYING_TO_REAPPLY_CLIENT_CHANGES,
    /**
     * Prints how many attempts to apply client changes left.
     * <p>
     * <b>Parameter</b>: remaining number of attempts.
     * </p>
     */
    COMMON_REMAINING_NUMBER_OF_APPLY_CLIENT_CHANGES_RETRIES,
    /**
     * Prints how many sync retries left.
     * <p>
     * <b>Parameter</b>: remaining number of sync attempts.
     * </p>
     */
    COMMON_REMAINING_NUMBER_OF_SYNC_RETRIES,
    /**
     * Informs about attempt to get changes from server database.
     * <p>
     * <b>Parameter</b>: attempts counter.
     * </p>
     */
    COMMON_TRYING_TO_GET_SERVER_CHANGES_FOR_N_TIME,
    /**
     * Prints how many attempts to get server changes remains.
     * <p>
     * <b>Parameter</b>: remaining number attempts.
     * </p>
     */
    COMMON_REMAINING_NUMBER_OF_GET_SERVER_CHANGES_RETRIES,
    /**
     * When synchronization retry recognized.
     */
    COMMON_SYNC_RETRY_RECOGNIZED,
    /**
     * Informs that request for server changes will be performed.
     */
    COMMON_REQUESTING_CHANGES_FROM_SERVER,
    /**
     * Informs that the server changes will be applied on the client database.
     */
    COMMON_APPLYING_CHANGES_FROM_SERVER_TO_CLIENT,
    /**
     * Informs that request for client changes will be performed.
     */
    COMMON_REQUESTING_CHANGES_FROM_CLIENT,
    /**
     * Informs that the client changes will be applied on the server database.
     */
    COMMON_APPLYING_CHANGES_FROM_CLIENT_TO_SERVER,
    /**
     * Informs that client revisions are going to be updated.
     */
    COMMON_UPDATING_CLIENT_REVISIONS,
    /**
     * Informs abut the new client revision.
     * <p>
     * <b>Parameter</b>: new revision.
     * </p>
     */
    COMMON_CLIENT_REVISION_UPDATED_TO,
    /**
     * Well, informs that sync finished.
     * <p/>
     */
    COMMON_SYNCHRONIZATION_FINISHED,
    /**
     * Informs that database schema is up to date and there is no need to update it.
     */
    COMMON_SCHEMA_IS_UP_TO_DATE,
    /**
     * When database was empty, and schema from server provider is going to be applied on client.
     */
    COMMON_DOWNLOADING_DB_SCHEMA_FROM_SERVER,
    /**
     * Informs that database schema from server is going to be applied on client database.
     */
    COMMON_APPLYING_DB_SCHEMA,
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="*************** Messages related to configuration ***************" >
    /**
     * Loading configuration from input stream is just started.
     */
    CONFIG_LOADING_FROM_STREAM,
    /**
     * Information that loading frameworks configuration is done.
     */
    CONFIG_CONFIG_LOADED,
    /**
     * Information about successfully loaded config option.
     * <p>
     * <b>Parameters:</b> options name, option value.
     * </p>
     */
    CONFIG_OPTION_LOADED,
    /**
     * Informs that there is no special synchronization direction specified for a table and default will be used.
     * <p>
     * <b>Parameters:</b> default direction.
     * </p>
     */
    CONFIG_USING_DEFAULT_SYNC_DIRECTION,
    /**
     * Informs what synchronization direction is used.
     * <p>
     * <b>Parameters:</b> the direction.
     * </p>
     */
    CONFIG_SYNC_DIRECTION_IS,
    /**
     * Inform that loaded configuration file is empty.
     */
    CONFIG_CONFIGURATION_FILE_IS_EMPTY,
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="*************** Messages related to data layer. ***************" >
    /**
     * Information about setting the transaction isolation level on connection object.
     * <p>
     * <b>Parameters:</b> isolation level.
     * </p>
     */
    DATA_SETTING_TRANS_ISOLATION_LEVEL,
    /**
     * Information about the connection's transaction isolation level.
     * <p>
     * <b>Parameters:</b> isolation level.
     * </p>
     */
    DATA_TRANS_ISOLATION_LEVEL,
    /**
     * Information about validation of settings.
     */
    COMMON_SETTINGS_VALIDATION,
    /**
     * Information that recreation of server meta is retried.
     */
    COMMON_RETRYING_RECREATE_SERVER_META_TABLES;
    //</editor-fold>
}
