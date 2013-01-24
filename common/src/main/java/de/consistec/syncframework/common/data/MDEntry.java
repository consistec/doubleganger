package de.consistec.syncframework.common.data;

import static de.consistec.syncframework.common.MdTableDefaultValues.MDV_DELETED_VALUE;

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
     * Does the change still exist?
     * <p/>
     *
     * @serial
     */
    private boolean exists;
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
     * @param exists Exists flag
     * @param revision Row revision
     * @param tableName Table name
     * @param mdv hash value
     */
    public MDEntry(Object primaryKey, boolean exists, int revision, String tableName, String mdv) {
        this.primaryKey = primaryKey;
        this.exists = exists;
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
     * Is the data row deleted or not.
     *
     * @return true if the entry still exists in the data table.
     */
    public boolean isExists() {
        return exists;
    }

    /**
     * Sets true if the mdv value in the meta data table is not null,
     * otherwise false. if a row in the meta data table of a specific
     * primarey key exists and the mdv value is null, then the data row
     * was deleted.
     *
     * @param exists true if entry exist ???
     */
    public void setExists(boolean exists) {
        this.exists = exists;
    }

    /**
     * Sets the isExists value to false and empties the mdv value.
     */
    public void setDeleted() {
        mdv = MDV_DELETED_VALUE;
        exists = false;
    }

    /**
     * Sets the isExists value to true.
     */
    public void setAdded() {
        exists = true;
    }

    /**
     * Sets the isExists value to true.
     */
    public void setModified() {
        exists = true;
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
     * Something like {@code MDEntry{pk=PkObjectToString, isExists=true, rev=2, tableName=name}}.
     * Do <b>not</b> parse! Result could change in feature releases.
     *
     * @return Object's representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s{pk=%s, exists=%b, rev=%d, tableName=%s}", getClass().getSimpleName(),
            primaryKey == null ? "null" : primaryKey, exists,
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

        if (exists != mdEntry.exists) {
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
        result = hashcodePrime * result + (exists ? 1 : 0);
        result = hashcodePrime * result + revision;
        result = hashcodePrime * result + (tableName != null ? tableName.hashCode() : 0);
        return result;
    }
    //</editr-fold>
}
