package de.consistec.syncframework.impl.data.schema;

import de.consistec.syncframework.common.data.schema.Column;
import de.consistec.syncframework.common.data.schema.Constraint;
import de.consistec.syncframework.common.data.schema.ISQLConverter;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.data.schema.Table;
import de.consistec.syncframework.common.exception.SchemaConverterException;
import de.consistec.syncframework.common.util.SQLTypesUtil;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 30.01.13 17:10
 */
public class CreateSchemaToMySQLConverter implements ISQLConverter<Schema> {

    /**
     * space constant.
     */
    protected static final char QUOTE = ' ';
    /**
     * sparator constant.
     */
    protected static final String LITERALS_COMMA = ",";

    /**
     * This method converts the passed Schema object to SQL queries which can be
     * executed to create the schema in the database.
     *
     * @param schema Schema to convert.
     * @return SQL queries to apply the schema in the database
     * @throws SchemaConverterException if conversion fails
     */
    @Override
    public String toSQL(Schema schema) throws SchemaConverterException {
        StringBuilder result = new StringBuilder();
        try {
            for (Table table : schema.getTables()) {
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

                result.append(") engine=InnoDB;");
            }
        } catch (IllegalAccessException e) {
            throw new SchemaConverterException(e);
        }
        return result.toString();
    }
}
