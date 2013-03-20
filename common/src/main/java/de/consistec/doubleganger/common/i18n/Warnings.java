package de.consistec.doubleganger.common.i18n;

/*
 * #%L
 * Project - doppelganger
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
 * List of warning messages used in framework.
 * <p/>
 *
 * @author Piotr Wieczorek
 * @company consistec Engineering and Consulting GmbH
 * @date 30.11.2012 13:45:36
 * @since 0.0.1-SNAPSHOT
 */
@BaseName("de/consistec/doubleganger/common/i18n/warnings")
@LocaleData(value = {
    @Locale("en") })
public enum Warnings {

    /**
     * When update of clients revision fails.
     */
    COMMON_CANT_UPDATE_CLIENT_REV,
    /**
     * When client caught
     * {@link de.consistec.doubleganger.common.exception.ServerStatusException ServerStatusException}
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
}
