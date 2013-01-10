package de.consistec.syncframework.common.util;

import de.consistec.syncframework.common.i18n.Errors;

import ch.qos.cal10n.IMessageConveyor;
import ch.qos.cal10n.MessageConveyor;
import ch.qos.cal10n.MessageConveyorException;
import java.util.Locale;

/**
 * This class provides method for searching default {@link IMessageConveyor} for use in i18n classes.
 * <p>
 * <b>Warning:</b><br/>
 * This class is solely for internal usage!
 * </p>
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 29.11.2012 13:25:20
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
public final class DefaultMessageConveyorSearcher {

//<editor-fold defaultstate="expanded" desc=" Class fields " >
    /**
     * Default locale, points to provided bundles.
     */
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
//</editor-fold>
//<editor-fold defaultstate="expanded" desc=" Class constructors " >

    private DefaultMessageConveyorSearcher() {
        throw new AssertionError("No instances allowed");
    }
//</editor-fold>
//<editor-fold defaultstate="expanded" desc=" Class methods " >

    /**
     * Searches for IMessageConveyor instance when no locale provided.
     * Cal10n framework ignores default bundles, so we have to find if there is bundle for system default locale
     * and if no, then load provided English bundles.
     *
     * @return MessageConveyor for system locale, or, if such a bundle does not exists, for English locale.
     */
    public static IMessageConveyor search() {
        IMessageConveyor convey = new MessageConveyor(Locale.getDefault());
        try {
            convey.getMessage(Errors.values()[0]);
        } catch (MessageConveyorException ex) {
            // bundle does not exists so load english bundle.
            convey = new MessageConveyor(DEFAULT_LOCALE);
        }
        return convey;
    }
//</editor-fold>
}
