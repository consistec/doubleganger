package de.consistec.syncframework.impl.i18n;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

/**
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 04.12.2012 16:28:21
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
@BaseName("de/consistec/syncframework/impl/i18n/infos")
@LocaleData(value = {
    @Locale("en") })
public enum Infos {

    /**
     * Prints content of server's exception http header.
     */
    HEADER_WITH_SERVER_EXCEPTION,
    /**
     * Prints received server status code when server exception is caught.
     * <p>
     * <b>Parameters</b>: server status code.
     * </p>
     */
    SERVER_STATUS_CODE,
    /**
     * Prints information about new server's revision.
     * <p>
     * <b>Parameters</b>: revision.
     * </p>
     */
    NEW_SERVER_REVISION;
}
