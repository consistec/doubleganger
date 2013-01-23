package de.consistec.syncframework.common.i18n;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

/**
 * List of warning messages used in framework.
 * <p/>
 *
 * @author Piotr Wieczorek
 * @company Consistec Engineering and Consulting GmbH
 * @date 30.11.2012 13:45:36
 * @since 0.0.1-SNAPSHOT
 */
@BaseName("de/consistec/syncframework/common/i18n/warnings")
@LocaleData(value = {@Locale("en") })
public enum Warnings {

    //<editor-fold defaultstate="expanded" desc="***************** Common messages *****************" >
    /**
     * When update of clients revision fails.
     */
    COMMON_CANT_UPDATE_CLIENT_REV,
    /**
     * When client caught
     * {@link de.consistec.syncframework.common.exception.ServerStatusException ServerStatusException}
     * when trying to synchronize.
     * <p>
     * <b>Parameters</b>: server status code, exception message.
     * </p>
     */
    COMMON_CLIENT_CAUGHT_SERVER_STATUS_EXCEPTION,
    /**
     * When applying the client changes on the server fails.
     */
    COMMON_CANT_APLY_CLIENT_CHANGES_ON_SERVER,
    /**
     * When creation of {@link javax.xml.validation.SchemaFactory SchemaFactory} fails.
     */
    COMMON_CANT_CREATE_XML_SCHEMA_FACTORY,
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="*************** Messages related to configuration ***************" >
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="*************** Messages related to data layer. ***************" >
    /**
     * Message to inform that transaction isolation level SERIALIZABLE couldn't be set on the database connection.
     */
    DATA_TRANSACTION_ISOLATION_LEVEL_NOT_SERIALIZABLE,
    /**
     * Informs that an attempt to set Autocommit mode on {@link java.sql.Connection connection} failed.
     * <p>
     * <b>Parameters</b>: autocommit mode (true/false).
     * </p>
     */
    DATA_CANT_SET_AUTCOMIT_MODE,
    /**
     * Informs that an attempt to close the {@link java.sql.Connection connection} failed.
     */
    DATA_CANT_CLOSE_CONNECTION,
    /**
     * Warning, if metadata table (_md) could not be found during sync and is recreated.
     */
    COMMON_RECREATING_SERVER_META_TABLES,
    /**
     * Warning, if metadata table (_md) could not be recreated.
     */
    COMMON_RECREATING_SERVER_META_TABLES_FAILED;
    //</editor-fold>
}
