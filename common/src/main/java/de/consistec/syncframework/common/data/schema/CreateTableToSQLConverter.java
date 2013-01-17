package de.consistec.syncframework.common.data.schema;

import de.consistec.syncframework.common.exception.SchemaConverterException;
import de.consistec.syncframework.common.util.SQLTypesUtil;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 16.01.13 15:25
 */
public class CreateTableToSQLConverter extends SQLConverterAdapter<Table> {

//<editor-fold defaultstate="expanded" desc=" Class fields " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class constructors " >

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >

//</editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class methods " >
    @Override
    public String toSQL(final Table table) throws SchemaConverterException {
        StringBuilder result = new StringBuilder();
        try {
            result.append(String.format("CREATE TABLE %c%s%c(", QUOTE, table.getName(), QUOTE));
            boolean firstLine = true;
            for (Column column : table.getColumns()) {
                if (firstLine) {
                    firstLine = false;
                } else {
                    result.append(LITERALS_COMMA);
                }
                String nullString = "";
                if (!column.isNullable()) {
                    nullString = " NOT NULL";
                }
                result.append(String.format("%c%s%c %s", QUOTE, column.getName(), QUOTE, SQLTypesUtil.nameOf(
                    column.getType())));
                switch (SQLTypesUtil.sizeType(column.getType())) {
                    case 0:
                        result.append(String.format(" (%d)", column.getSize()));
                        break;
                    case 1:
                        result.append(String.format(" (%d,%d)", column.getSize(), column.getDecimalDigits()));
                        break;
                    case -1:
                        //nothing to do with this size type
                        break;
                    default:
                        throw new SchemaConverterException(String.format(
                            "could not apply sizeType for type %d of column %s",
                            column.getType(), column.getName()));
                }
                result.append(nullString);
                for (Constraint constraint : table.getConstraints()) {
                    if (constraint.getColumn().equalsIgnoreCase(column.getName())) {
                        result.append(" PRIMARY KEY ");
                    }
                }
            }
            result.append(");");
        } catch (IllegalAccessException e) {
            throw new SchemaConverterException(e);
        }
        return result.toString();
    }
//</editor-fold>

}
