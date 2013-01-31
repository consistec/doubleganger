package de.consistec.syncframework.common.data.schema;

/*
 * #%L
 * Project - doppelganger
 * File - ISQLConverter.java
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

import de.consistec.syncframework.common.exception.SchemaConverterException;

/**
 * This interfaces defines the behavior for
 * {@link de.consistec.syncframework.common.data.schema.Schema Schema} converters.
 * <p/>
 * Implementations of this interface have to implement the {@code toSQL(Schema schema)} method
 * which is responsible for the convertation of the type Schema to a SQL query of type String.
 * One example implementation can be find in {@link de.consistec.syncframework.common.data.schema.CreateSchemaToSQLConverter}.
 * <br/>
 * The other possibility to convert to a SQL query of type String is the method {@code fromChangelog(String xml)}.
 * Implementations of this method have to convert xml structure and targets to a SQL query.
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date 26.07.12 15:25
 * @since 0.0.1-SNAPSHOT
 */
public interface ISQLConverter<T> {

    /**
     * Converts {@link de.consistec.syncframework.common.data.schema.Schema Schema} object to SQL query.
     *
     * @param objectToConvert object to convert.
     * @return Sql queries to build the schema in database.
     * @throws SchemaConverterException if conversion fails.
     */
    String toSQL(T objectToConvert) throws SchemaConverterException;
}
