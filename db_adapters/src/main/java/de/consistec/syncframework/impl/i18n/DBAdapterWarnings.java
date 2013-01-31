package de.consistec.syncframework.impl.i18n;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

/**
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 04.12.2012 12:33:28
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
@BaseName("de/consistec/syncframework/impl/i18n/db_adapters_warnings")
@LocaleData(value = {
    @Locale("en") })
public enum DBAdapterWarnings {

    /**
     * When setting the appropriate {@link java.sql.Connection#setTransactionIsolation(int) Transaction Isolation Level}
     * fails.
     * <p>
     * <b>Parameter</b>: Level name.
     * </p>
     */
    CANT_SET_TRANS_ISOLATION_LEVEL,
    /**
     * When an attempt to delete the row didn't delete it (e.g.
     * {@link java.sql.Statement#executeUpdate(java.lang.String) executeUpdate} returns 0).
     * <p>
     * <b>Parameter</b>: primary key, table name.
     * </p>
     */
    NO_ROW_DELETED;
}
