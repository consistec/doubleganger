package de.consistec.syncframework.impl.i18n;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

/**
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 04.12.2012 16:58:49
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
@BaseName("de/consistec/syncframework/impl/i18n/warnings")
@LocaleData(value = {
    @Locale("en") })
public enum Warnings {

    /**
     * When server can't apply received client's changes because they are deprecated.
     */
    CANT_APPLY_CHANGES_CLIENT_NOT_UP_TO_DATE;
}
