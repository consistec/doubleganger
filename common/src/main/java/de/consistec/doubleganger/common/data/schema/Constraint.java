package de.consistec.doubleganger.common.data.schema;

/*
 * #%L
 * Project - doubleganger
 * File - Constraint.java
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
 * Representation of SQL constraints.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 25.07.12 15:51
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class Constraint {

    private ConstraintType type;
    private String name;
    private String column;

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
}
