package de.consistec.doubleganger.common.i18n;

/*
 * #%L
 * Project - doppelganger
 * File - DBAdapterWarnings.java
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
 * @date 04.12.2012 12:33:28
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
@BaseName("de/consistec/doubleganger/common/i18n/db_adapters_warnings")
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
