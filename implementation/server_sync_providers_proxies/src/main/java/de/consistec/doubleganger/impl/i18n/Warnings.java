package de.consistec.doubleganger.impl.i18n;

/*
 * #%L
 * Project - doubleganger
 * File - Warnings.java
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

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

/**
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 04.12.2012 16:58:49
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
@BaseName("de/consistec/doubleganger/impl/i18n/warnings")
@LocaleData(value = {
    @Locale("en") })
public enum Warnings {

    /**
     * When server can't apply received client's changes because they are deprecated.
     */
    CANT_APPLY_CHANGES_CLIENT_NOT_UP_TO_DATE;
}
