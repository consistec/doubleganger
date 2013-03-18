package de.consistec.doubleganger.common.data.schema;

/*
 * #%L
 * Project - doppelganger
 * File - Column.java
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

/**
 * Representation of a database column.
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date 25.07.12 15:50
 * @since 0.0.1-SNAPSHOT
 */
public class Column {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private String name;
    private int type;
    private int size;
    private int decimalDigits;
    private boolean nullable = true;
    //<editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class constructors " >

    /**
     * Initialize partially the new object, with name and type of the column.
     *
     * @param name Column name
     * @param type Column type
     */
    public Column(String name, int type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Initialize fully the new object.
     *
     * @param name Column name
     * @param type Column type
     * @param size Column size
     * @param decimalDigits the number of fractional digits
     * @param nullable If column can have null values
     */
    public Column(String name, int type, int size, int decimalDigits, boolean nullable) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.decimalDigits = decimalDigits;
        this.nullable = nullable;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Class accessors " >

    /**
     * @return Column name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Name of the column.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Column type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type Column type.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return Column size
     */
    public int getSize() {
        return this.size;
    }

    /**
     * @param size Column size.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Part of a description of table columns available in a catalog.
     *
     * @return the number of fractional digits
     */
    public int getDecimalDigits() {
        return this.decimalDigits;
    }

    /**
     * Part of a description of table columns available in a catalog.
     *
     * @param decimalDigits the number of fractional digits
     * @see org.postgresql.jdbc2.AbstractJdbc2DatabaseMetaData.getColumns((String catalog,
     *      String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException
     */
    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    /**
     * Part of a description of table columns available in a catalog.
     *
     * @return true if column can have null values
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * Part of a description of table columns available in a catalog.
     *
     * @param nullable If column can have null values.
     * @see #isNullable()
     */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    //</editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class methods " >

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Column column = (Column) object;

        if (column.size != size) {
            return false;
        }
        if (nullable != column.nullable) {
            return false;
        }
        if (!name.equals(column.name)) {
            return false;
        }
        if (type != column.type) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int hashcodePrime = 31;
        int result = name.hashCode();
        result += hashcodePrime * result + type;
        result += hashcodePrime * result + size;
        result += hashcodePrime * result + (nullable ? 1 : 0);
        return result;
    }

    /**
     * Result of this method could be changed in futures releases.
     *
     * @return String representation of instance.
     */
    @Override
    public String toString() {
        return String.format("%s{name=%s, type=%d, size=%d, decimalDigits=%d, nullable=%b}", getClass().getSimpleName(),
            name, type, size, decimalDigits,
            nullable);
    }
}
