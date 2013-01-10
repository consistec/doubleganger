package de.consistec.syncframework.common.data.schema;

import de.consistec.syncframework.common.exception.SchemaConverterException;
import de.consistec.syncframework.common.exception.SerializationException;

/**
 * Adapter for implementations of the interface {@code de.consistec.syncframework.common.data.schema.ISQLConverter}.
 * Subclasses of this Adapter only need to implement the method {@code toSQL(final Schema schame)}.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 04.12.12 11:11
 */
public abstract class SchemaToSQLAdapter implements ISQLConverter {

//<editor-fold defaultstate="expanded" desc=" Class fields " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class constructors " >

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class methods " >

//</editor-fold>

    /**
     * @param schema Schema to convert.
     * @return SQL query for the type Schema
     * @throws SchemaConverterException if conversion fails
     * @see de.consistec.syncframework.common.data.schema.ISQLConverter.toSQL(final Schema schema)
     */
    @Override
    public abstract String toSQL(final Schema schema) throws SchemaConverterException;

    /**
     * @param xml Change log.
     * @return empty String because it is not necessary to implement
     * @throws SerializationException if parsing of document fails.
     * @see de.consistec.syncframework.common.data.schema.ISQLConverter.fromChangelog(final String xml)
     */
    @Override
    public String fromChangelog(final String xml) throws SerializationException {
        return "";
    }
}
