package de.consistec.syncframework.common.data;

import static de.consistec.syncframework.common.util.CollectionsUtil.newHashMap;

import de.consistec.syncframework.common.util.HashCalculator;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Map;

/**
 * Represents a single database base row.
 * Used for internal communication between framework components
 *
 * @author Markus Backes
 * @company Consistec Engineering and Consulting GmbH
 * @date unknown
 * @serial
 * @since 0.0.1-SNAPSHOT
 */
public class Change implements Serializable {

    /**
     * A Comparator which compares to Change objects through their primarey keys.
     */
    private static PrimaryKeyComparator pkComparator = new PrimaryKeyComparator();

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    /**
     * The message digest entry for this change.
     * Not null.
     * <p/>
     *
     * @serial
     */
    private MDEntry mdEntry;
    /**
     * The row data.
     * Not null.
     * <p/>
     *
     * @serial
     */
    private Map<String, Object> rowData = newHashMap();

    //</editor-fold>
    //<editor-fold defaultstate="expanded" desc=" Class constructors " >

    /**
     * Instantiates a new change.
     */
    public Change() {
    }

    /**
     * Instantiates a new change populated with provided data.
     *
     * @param mdEntry the md entry
     * @param rowData the row data
     */
    public Change(MDEntry mdEntry, Map<String, Object> rowData) {
        this.mdEntry = mdEntry;
        this.rowData = rowData;
    }

    //</editor-fold>
    //<editor-fold defaultstate="expanded" desc=" Class accessors " >

    /**
     * Gets the md entry.
     *
     * @return the md entry
     */
    public MDEntry getMdEntry() {
        return mdEntry;
    }

    /**
     * Sets the md entry.
     *
     * @param mdEntry the new md entry
     */
    public void setMdEntry(MDEntry mdEntry) {

        this.mdEntry = mdEntry;
    }

    /**
     * Gets the row data.
     *
     * @return the row data
     */
    public Map<String, Object> getRowData() {
        return rowData;
    }

    /**
     * Sets the row data.
     *
     * @param rowData the row data
     */
    public void setRowData(Map<String, Object> rowData) {
        this.rowData = rowData;
    }

    //</editor-fold>
    //<editor-fold defaultstate="expanded" desc=" Class methods " >

    /**
     * Brief description of instance state.
     * Result looks like {@code Change{mdEntry=MDEntry{...}, rowData=[]}} but could be changed in future releases.
     *
     * @return Object's state short description.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{ mdEntry="
            + mdEntry.toString()
            + ", rowData= "
            + rowData.toString()
            + " }";
    }

    /**
     * Computes the syncframework hash value for this change.
     * <p/>
     *
     * @return hash value as string
     * @throws NoSuchAlgorithmException
     */
    public String calculateHash() throws NoSuchAlgorithmException {
        return new HashCalculator().getHash(rowData);
    }

    @Override
    public boolean equals(Object object) { //NOSONAR

        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Change other = (Change) object;

        if (mdEntry != null ? !mdEntry.equals(other.mdEntry) : other.mdEntry != null) {
            return false;
        }

        if (rowData != null && other.rowData != null) {
            if (!rowData.keySet().equals(other.rowData.keySet())) {
                return false;
            }
            for (Map.Entry<String, Object> entry : rowData.entrySet()) {

                String key = entry.getKey();

                if (other.getRowData().get(key) == null && entry.getValue() == null) {
                    //needed to prevent nullpointerexception
                    continue;
                } else if (other.getRowData().get(key) == null ^ entry.getValue() == null) {
                    return false;
                } else if (!other.getRowData().get(key).toString().equals(entry.getValue().toString())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = mdEntry == null ? 0 : mdEntry.hashCode();
        result = 31 * result + (rowData == null ? 0 : rowData.hashCode());
        return result;
    }

    /**
     * Returns the primary key comparator of the Change class.
     *
     * @return PrimaryKeyComparator
     */
    public static final PrimaryKeyComparator getPrimaryKeyComparator() {
        return pkComparator;
    }
    //</editor-fold>

    private static class PrimaryKeyComparator implements Comparator<Change> {

        @Override
        public int compare(final Change o1, final Change o2
        ) {
            if (o1.getMdEntry() == null || o2.getMdEntry() == null) {
                throw new IllegalStateException("md entry must not be null!");
            }
            int pk1 = Integer.valueOf(o1.getMdEntry().getPrimaryKey().toString()).intValue();
            int pk2 = Integer.valueOf(o2.getMdEntry().getPrimaryKey().toString()).intValue();

            if (pk1 == pk2) {
                return 0;
            } else if (pk1 > pk2) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
