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
public abstract class SQLConverterAdapter<T> implements ISQLConverter<T> {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    /**
     * space constant.
     */
    protected static final char QUOTE = ' ';
    /**
     * sparator constant.
     */
    protected static final String LITERALS_COMMA = ",";
//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class constructors " >

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class methods " >

//</editor-fold>

    /**
     * @param objectToConvert object to convert.
     * @return SQL query for the type Schema
     * @throws SchemaConverterException if conversion fails
     * @see de.consistec.syncframework.common.data.schema.ISQLConverter.toSQL(final T objectToConvert)
     */
    @Override
    public abstract String toSQL(final T objectToConvert) throws SchemaConverterException;

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
