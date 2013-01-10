package de.consistec.syncframework.common.data.schema;

/**
 * Representation of SQL constraints.
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 25.07.12 15:51
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class Constraint {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private ConstraintType type;
    private String name;
    private String column;
    //</editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class constructors" >
    /**
     * Initialize new Constraint object.
     *
     * @param type Constraint type
     * @param name Constraint name
     * @param column Column name to which the constraint belongs.
     */
    public Constraint(ConstraintType type, String name, String column) {
        this.type = type;
        this.name = name;
        this.column = column;
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Class accessors " >
    /**
     *
     * @return The type of the constraint
     */
    public ConstraintType getType() {
        return type;
    }

    /**
     *
     * @param type Constraint type.
     */
    public void setType(ConstraintType type) {
        this.type = type;
    }

    /**
     *
     * @return Name of the constraint
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name Constraint name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return Column to which constraint belongs.
     */
    public String getColumn() {
        return column;
    }

    /**
     *
     * @param column Column belonging to this constraint.
     */
    public void setColumn(String column) {
        this.column = column;
    }

    //</editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class methods" >

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Constraint that = (Constraint) o;

        if (!column.equals(that.column)) {
            return false;
        }
        if (name == null ? that.name != null : !name.equals(that.name)) {
            return false;
        }
        if (type != that.type) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int hashcodePrime = 31;
        int result = type.hashCode();
        result = hashcodePrime * result + (name == null ? 0 : name.hashCode());
        result = hashcodePrime * result + column.hashCode();
        return result;
    }

    /**
     * Returns String representation of object state.
     * It will looks something like that: "Constraint{name=value, type=value, column=value}"
     * and could be changed in future.
     *
     * @return String representation of instance state.
     */
    @Override
    public String toString() {
        return String.format("%s{name=%s, type=%s, column=%s}", getClass().getSimpleName(), name, type.name(), column);
    }

    //</editor-fold>
}
