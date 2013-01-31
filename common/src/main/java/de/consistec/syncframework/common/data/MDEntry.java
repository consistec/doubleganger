package de.consistec.syncframework.common.data;

import java.io.Serializable;

/**
 * The Class MDEntry represents meta information about the state of a change (db row) entry.
 *
 * @author Markus Backes
 * @company Consistec Engineering and Consulting GmbH
 * @date 16.07.2012 11:10
 * @serial
 * @since 0.0.1-SNAPSHOT
 */
public class MDEntry implements Serializable {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Class constructors " >
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Accessors " >
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

    //</editor-fold>
    //<editor-fold defaultstate="expanded" desc=" Class methods " >
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
    //</editor-fold>

}
