package de.consistec.doubleganger.common.data.schema;

/*
 * #%L
 * Project - doppelganger
 * File - Schema.java
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

import static de.consistec.doubleganger.common.util.CollectionsUtil.newHashSet;
import static de.consistec.doubleganger.common.util.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * Class Schema represents a database structure.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 12.04.12 10.01
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 *
 */
public class Schema {

    private Set<Table> tables = newHashSet();

    /**
     * @see java.util.HashSet#toString()
     * @return String representation of tables set
     */
    @Override
    public String toString() {
        return tables.toString();
    }

    /**
     * Adds tables to schema.
     *
     * @param tables Tables to add.
     */
    public void addTables(Table... tables) {
        for (Table table : tables) {
            checkNotNull(table, "Can't add \"null\" to tables collection");
        }
        this.tables.addAll(Arrays.asList(tables));
    }

    /**
     * Removes tables from schema.
     *
     * @param tables Tables to remove.
     */
    public void removeTables(Table... tables) {
        this.tables.removeAll(Arrays.asList(tables));
    }

    /**
     * Returns unmodifiable set of tables.
     * Any attempt to modify the set will result in throwing {@link UnsupportedOperationException}
     *
     * @see java.util.Collections#unmodifiableSet(java.util.Set)
     * @return Unmodifiable set of tables.
     */
    public Set<Table> getTables() {
        return Collections.unmodifiableSet(tables);
    }

    /**
     * Syntax sugar, returns the number of tables in schema.
     *
     * @return Number of tables.
     */
    public int countTables() {
        return tables.size();
    }

    @Override
    public boolean equals(Object o) {

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Schema schema = (Schema) o;

        if (tables == null) {
            return (schema.tables == null);
        } else {
            return tables.equals(schema.tables);
        }
    }

    @Override
    public int hashCode() {
        return tables == null ? 0 : tables.hashCode();
    }
}
