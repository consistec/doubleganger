package de.consistec.syncframework.impl.adapter;

import de.consistec.syncframework.common.data.schema.ISQLConverter;
import de.consistec.syncframework.common.exception.SchemaConverterException;
import de.consistec.syncframework.common.exception.SerializationException;

/**
 * Adapter for implementations of the interface {@code de.consistec.syncframework.common.data.schema.ISQLConverter}.
 * Subclasses of this Adapter only need to implement the method {@code fromChangelog(final String xml)}.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 04.12.12 11:07
 */
public abstract class ChangeLogToSQLAdapter<T> implements ISQLConverter<T> {

//<editor-fold defaultstate="expanded" desc=" Class fields " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class constructors " >

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class methods " >

//</editor-fold>

    /**
     * @param objectToConvert object to convert.
     * @return empty String because it is not necessary to implement
     * @throws SchemaConverterException if conversion fails.
     * @see de.consistec.syncframework.common.data.schema.ISQLConverter.toSQL(final de.consistec.syncframework.common.data.schema.Schema schema)
     */
    @Override
    public String toSQL(final T objectToConvert) throws SchemaConverterException {
        return "";
    }

    /**
     * @param xml Change log.
     * @return SQL query for parsed xml document
     * @throws SerializationException if parsing of xml document fails.
     * @see de.consistec.syncframework.common.data.schema.ISQLConverter.fromChangelog(final String xml)}
     */
    @Override
    public abstract String fromChangelog(final String xml) throws SerializationException;
}
