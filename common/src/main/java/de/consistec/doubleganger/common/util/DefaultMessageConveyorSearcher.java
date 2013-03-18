package de.consistec.doubleganger.common.util;

/*
 * #%L
 * Project - doppelganger
 * File - DefaultMessageConveyorSearcher.java
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

import de.consistec.doubleganger.common.i18n.Errors;

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
 * @company consistec Engineering and Consulting GmbH
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
