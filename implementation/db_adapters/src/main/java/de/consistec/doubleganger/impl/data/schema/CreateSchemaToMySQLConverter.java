package de.consistec.doubleganger.impl.data.schema;

/*
 * #%L
 * Project - doubleganger
 * File - CreateSchemaToMySQLConverter.java
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

import de.consistec.doubleganger.common.data.schema.Column;
import de.consistec.doubleganger.common.data.schema.Constraint;
import de.consistec.doubleganger.common.data.schema.ISQLConverter;
import de.consistec.doubleganger.common.data.schema.Schema;
import de.consistec.doubleganger.common.data.schema.Table;
import de.consistec.doubleganger.common.exception.SchemaConverterException;
import de.consistec.doubleganger.common.util.SQLTypesUtil;

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
