package de.consistec.syncframework.common.data.schema;

import de.consistec.syncframework.common.exception.SchemaConverterException;
import de.consistec.syncframework.common.exception.SerializationException;

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
 * @company Consistec Engineering and Consulting GmbH
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

    /**
     * Converts any xml document to SQL query.
     *
     * @param xml Change log.
     * @return any Sql queries to execute in database.
     * @throws SerializationException if parsing of xml document fails.
     */
    String fromChangelog(String xml) throws SerializationException;
}
