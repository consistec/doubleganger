package de.consistec.doubleganger.common.data.schema;

/*
 * #%L
 * Project - doppelganger
 * File - Table.java
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

import static de.consistec.doubleganger.common.i18n.MessageReader.read;
import static de.consistec.doubleganger.common.util.CollectionsUtil.newHashSet;
import static de.consistec.doubleganger.common.util.Preconditions.checkNotNull;

import de.consistec.doubleganger.common.i18n.Errors;

import java.util.Arrays;
import java.util.Set;

/**
 * Representation of database table.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 25.07.12 15.50
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class Table {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private String name;
    private Set<Column> columns = newHashSet();
    private Set<Constraint> constraints = newHashSet();
    //</editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class constructors" >
    /**
     * Creates a table object with a <i>name</i>.
     *
     * @param name Table name.
     */
    public Table(String name) {
        this.name = name;
    }
    //</editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class accessors " >
    /**
     *
     * @return Table name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name Table name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a copy of original column set, so changes to this copy does not affect original set.
     *
     * @return returns Defensive copy of columns set.
     */
    public Set<Column> getColumns() {
        return newHashSet(columns);
    }

    /**
     * Adds columns to the table.
     * <i>columns</i> can't be null.
     *
     * @param columns Table's columns.
     */
    public void add(Column... columns) {
        for (Column column : columns) {
            checkNotNull(column, read(Errors.COMMON_PROVIDED_COLUMN_IS_NULL));
        }
        this.columns.addAll(Arrays.asList(columns));
    }

    /**
     * Removes columns from the table.
     * <i>columns</i> can not be null.
     *
     * @param columns Table's columns.
     */
    public void remove(Column... columns) {
        this.columns.removeAll(Arrays.asList(columns));
    }

    /**
     * Gets the name of the table's primary key column.
     *
     * @return columnName the name of the table's PK column, or an empty string if there is none.
     */
    public String getPkColumnName() {
        for (Constraint constraint : this.constraints) {
            if (ConstraintType.PRIMARY_KEY.equals(constraint.getType())) {
                return constraint.getColumn();
            }
        }
        return "";
    }

    /**
     * Returns a copy of original constraint set, so changes to this copy won't affect original set.
     *
     * @return Defensive copy of constraints set.
     */
    public Set<Constraint> getConstraints() {
        return newHashSet(constraints);
    }

    /**
     * Adds constraints to the table.
     * <i>constraints</i> can not be null.
     *
     * @param constraints Table's constraints
     */
    public void add(Constraint... constraints) {
        for (Constraint constraint : constraints) {
            checkNotNull(constraint, read(Errors.COMMON_PROVIDED_CONSTRAINT_IS_NULL));
        }
        this.constraints.addAll(Arrays.asList(constraints));
    }

    /**
     * Removes constraints from table.
     * <i>constraint</i> can not be null.
     *
     * @param constraints Constraints to remove from table.
     */
    public void remove(Constraint... constraints) {
        this.constraints.removeAll(Arrays.asList(constraints));
    }

    //</editor-fold>
    //<editor-fold defaultstate="expanded" desc=" Class methods " >
    @Override
    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }

        Table other = (Table) o;

        if (columns != null ? !columns.equals(other.columns) : other.columns != null) {
            return false;
        }
        if (constraints != null ? !constraints.equals(other.constraints) : other.constraints != null) {
            return false;
        }
        if (!name.equals(other.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int hashcodePrime = 31;
        int result = name.hashCode();
        result = hashcodePrime * result + (columns != null ? columns.hashCode() : 0);
        result = hashcodePrime * result + (constraints != null ? constraints.hashCode() : 0);
        return result;
    }

    /**
     * String representation of object state.
     * It looks like that: "Table{name=value, columns=[...], constraints=[]}"
     * but this could be changed in the future releases
     * <p/>
     * @return String representation on object state.
     */
    @Override
    public String toString() {
        return String.format("%s{name=%s, columns=%s, constraints=%s}", getClass().getSimpleName(), name,
            columns.toString(), constraints.toString());
    }
    //</editor-fold>
}
