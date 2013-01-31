package de.consistec.syncframework.impl.i18n;

/*
 * #%L
 * Project - doppelganger
 * File - DBAdapterErrors.java
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
 * @date 04.12.2012 12:24:22
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
@BaseName("de/consistec/syncframework/impl/i18n/db_adapters_errors")
@LocaleData(value = {
    @Locale("en") })
public enum DBAdapterErrors {

    /**
     * Informs that jdbc driver can not be loaded.
     * <p>
     * <b>Parameter</b>: driver name.
     * </p>
     */
    CANT_LOAD_JDBC_DRIVER,
    /**
     * When creating adapter instance fails.
     * <p>
     * <b>Parameter</b>: adapter name.
     * </p>
     */
    CANT_CREATE_ADAPTER_INSTANCE,
    /**
     * When reading columns name from table fails.
     * <p>
     * <b>Parameter</b>: table name.
     * </p>
     */
    CANT_READ_TABLE_COLUMNS,
    /**
     * When applying schema on database fails.
     */
    CANT_APPLY_DB_SCHEMA,
    /**
     * When error occurs while converting xml representation of database
     * {@link de.consistec.syncframework.common.data.schema.Schema schema} to SQL script.
     */
    CANT_CONVERT_SCHEMA_TO_SQL,
    /**
     * When reading tables names from database fails.
     */
    CANT_READ_TABLES,
    /**
     * When committing the {@link java.sql.Connection connection} fails.
     */
    COMMITTING_THE_CONNECTION_FAILS,
    /**
     * When cant find pk column in table.
     * <p>
     * <b>Parameter</b>: table name.
     * </p>
     */
    NO_PK_COLUMN,
    /**
     * When error occurs while searching for pk column in table.
     * <p>
     * <b>Parameter</b>: table name.
     * </p>
     */
    ERROR_WHILE_LOOKING_FOR_PK,
    /**
     * When can not read the last revision from database.
     */
    CANT_READ_LAST_REVISION,
    /**
     * When can't update checksum row.
     * <p>
     * <b>Parameter</b>: table name.
     * </p>
     */
    CANT_UPDATE_MD_ROW,
    /**
     * When attempt to delete the database row fails.
     * <p>
     * <b>Parameter</b>: primary key, table name.
     * </p>
     */
    CANT_DELETE_ROW,
    /**
     * When can't insert checksum row.
     * <p>
     * <b>Parameter</b>: table name.
     * </p>
     */
    CANT_INSERT_MD_ROW,
    /**
     * When can't insert data row.
     * <p>
     * <b>Parameter</b>: table name.
     * </p>
     */
    CANT_INSERT_DATA_ROW,
    /**
     * When updating data row fails.
     * <p>
     * <b>Parameter</b>: table name.
     * </p>
     */
    CANT_UPDATE_DATA_ROW,
    /**
     * When searching for changes for provided revision fails.
     * <p>
     * <b>Parameter</b>: revision, table name.
     * </p>
     */
    CANT_READ_CHANGES_FOR_REVISION,
    /**
     * When searching for changes by flag fails.
     * <p>
     * <b>Parameter</b>: table name.
     * </p>
     */
    CANT_READ_CHANGES_FOR_FLAG,
    /**
     * When updating the revision fails.
     * <p>
     * <b>Parameter</b>: table name.
     * </p>
     */
    UPDATING_REVISON_FAILED,
    /**
     * When reading the row by primary key fails.
     * <p>
     * <b>Parameter</b>: table name, key.
     * </p>
     */
    CANT_READ_THE_ROW,
    /**
     * When searching for deleted rows fails.
     * <p>
     * <b>Parameter</b>: table name.
     * </p>
     */
    ERROR_SEARCHING_DELETED_ROWS,
    /**
     * When reading all rows in table fails.
     * <p>
     * <b>Parameter</b>: table name.
     * </p>
     */
    CANT_GET_ALL_ROWS,
    /**
     * When building the {@link de.consistec.syncframework.common.data.schema.Schema Schema} fails.
     */
    CANT_BUILD_SCHEMA,
    /**
     * When closing the {@link java.sql.ResultSet ResultSet} fails.
     */
    CANT_CLOSE_RESULTSET,
    /**
     * When closing the {@link java.sql.Statement Statement} fails.
     */
    CANT_CLOSE_STATEMENT,
    /**
     * When closing the {@link java.sql.Connection Connection} fails.
     */
    CANT_CLOSE_CONNECTION,
    /**
     * When provided database name is {@code null} or empty.
     */
    DATABASE_NAME_EMPTY,
    /**
     * When provided database hostname is {@code null} or empty.
     */
    HOSTNAME_IS_EMPTY,
    /**
     * When can't create md-table.
     * <p>
     * <b>Parameter</b>: table name.
     * </p>
     */
    CANT_CREATE_MD_TABLE,
    /**
     * When database transaction was aborted due to serialization failures.
     * <p/>
     * @see <a href="http://www.postgresql.org/docs/9.1/static/transaction-iso.html"> PostgreSQL transactions docs</a>
     */
    TRANSACTION_ABORTED_SERIALIZATION_FAILURES;
}
