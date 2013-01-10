package de.consistec.syncframework.common.data.schema;

/**
 * Representation of a database column.
 *
 * @author Markus Backes
 * @company Consistec Engineering and Consulting GmbH
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
