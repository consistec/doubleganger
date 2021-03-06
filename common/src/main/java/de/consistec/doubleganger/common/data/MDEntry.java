package de.consistec.doubleganger.common.data;

/*
 * #%L
 * Project - doubleganger
 * File - MDEntry.java
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
import java.io.Serializable;

/**
 * The Class MDEntry represents meta information about the state of a change (db row) entry.
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date 16.07.2012 11:10
 * @serial
 * @since 0.0.1-SNAPSHOT
 */
public class MDEntry implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * The primary key of the change.
     * <p/>
     *
     * @serial
     */
    private Object primaryKey;
    /**
     * Does the row data still exist?
     * <p/>
     *
     * @serial
     */
    private boolean dataRowExists;
    /**
     * The revision of the change.
     * <p/>
     *
     * @serial
     */
    private int revision;
    /**
     * The table name of the change.
     * <p/>
     *
     * @serial
     */
    private String tableName;
    /**
     * The hash value of the change.
     */
    private String mdv;

    /**
     * Instantiates a new Md entry with default values.
     */
    public MDEntry() {
    }

    /**
     * Instantiates a new message digest entry with provided values.
     *
     * @param primaryKey Primary key
     * @param rowDataExists true if the row data still exists
     * @param revision Row revision
     * @param tableName Table name
     * @param mdv hash value
     */
    public MDEntry(Object primaryKey, boolean rowDataExists, int revision, String tableName, String mdv) {
        this.primaryKey = primaryKey;
        this.dataRowExists = rowDataExists;
        this.revision = revision;
        this.mdv = mdv;
        this.tableName = tableName;
    }

    /**
     * Gets the primary key.
     *
     * @return the primary key
     */
    public Object getPrimaryKey() {
        return primaryKey;
    }

    /**
     * Sets the primary key.
     *
     * @param primaryKey the new primary key
     */
    public void setPrimaryKey(Object primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * Sets if the data entry still exists.
     *
     * @param dataRowExists true if it still exists
     */
    public void setDataRowExists(boolean dataRowExists) {
        this.dataRowExists = dataRowExists;
    }

    /**
     * Returns true if the data row still exists.
     * <p/>
     * @return true if data row still exists
     */
    public boolean dataRowExists() {
        return dataRowExists;
    }

    /**
     * THe MDEntry now considers its data row has been deleted.
     */
    public void setDataRowDeleted() {
        this.dataRowExists = false;
    }

    /**
     * Gets the revision.
     *
     * @return the revision
     */
    public int getRevision() {
        return revision;
    }

    /**
     * Sets the revision.
     *
     * @param revision the new revision
     */
    public void setRevision(int revision) {
        this.revision = revision;
    }

    /**
     * Gets the table name.
     *
     * @return the table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets the table name.
     *
     * @param tableName the new table name
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Returns the hash value of the entry.
     *
     * @return String the hash value
     */
    public String getMdv() {
        return mdv;
    }

    /**
     * Sets the hash value.
     *
     * @param mdv the hash value.
     */
    public void setMdv(final String mdv) {
        this.mdv = mdv;
    }

    /**
     * Description of object state.
     * Something like {@code MDEntry{pk=PkObjectToString, dataRowExists=true, rev=2, tableName=name}}.
     * Do <b>not</b> parse! Result could change in feature releases.
     *
     * @return Object's representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s{pk=%s, rowDataExists=%b, rev=%d, tableName=%s}", getClass().getSimpleName(),
            primaryKey == null ? "null" : primaryKey, dataRowExists,
            revision, tableName == null ? "null" : tableName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MDEntry mdEntry = (MDEntry) o;

        if (dataRowExists != mdEntry.dataRowExists) {
            return false;
        }
        if (revision != mdEntry.revision) {
            return false;
        }
        if (primaryKey != null ? !primaryKey.toString().equals(mdEntry.primaryKey.toString())
            : mdEntry.primaryKey != null) {
            return false;
        }
        if (tableName != null ? !tableName.equals(mdEntry.tableName) : mdEntry.tableName != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int hashcodePrime = 31;
        int result = primaryKey != null ? primaryKey.hashCode() : 0;
        result = hashcodePrime * result + (dataRowExists ? 1 : 0);
        result = hashcodePrime * result + revision;
        result = hashcodePrime * result + (tableName != null ? tableName.hashCode() : 0);
        return result;
    }
}
