package de.consistec.syncframework.common.util;

import static de.consistec.syncframework.common.util.CollectionsUtil.newArrayList;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The Class HashCalculator.
 * <p/>
 * @company consistec Engineering and Consulting GmbH
 * @date unknown
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class HashCalculator {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    /**
     * The Constant HEXES.
     */
    private static final String HEXES = "0123456789ABCDEF";
    /**
     * The message digest.
     */
    private MessageDigest md;

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Class constructors" >

    /**
     * Instantiates a new HashCalculator.
     *
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public HashCalculator() throws NoSuchAlgorithmException {
        this("MD5");
    }

    /**
     * Instantiates a new HashCalculator with the given algorithm.
     *
     * @param algorithm Hash algorithm
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public HashCalculator(String algorithm) throws NoSuchAlgorithmException {
        this.md = MessageDigest.getInstance(algorithm);
    }

    //</editor-fold>

    //<editor-fold defaultstate="expanded" desc=" Class methods" >

    /**
     * Returns hexadecimal representation of given data.
     *
     * @param raw Data
     * @return Hex representation of given bytes.
     */
    private String getHex(byte[] raw) {
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            /**
             * CHECKSTYLE:OFF
             */
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
            /**
             * CHECKSTYLE:ON
             */
        }
        return hex.toString();
    }

    /**
     * Returns hash of given bytes.
     *
     * @param bytes Data for calculation.
     * @return Hash value of provided bytes
     */
    public String getHash(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return getHex(md.digest(bytes));
    }

    /**
     * Returns hash value of given map.
     * <p/>
     * Before hash is calculated, entries are sorted through keys, so changes in map
     * ordering don't change the resulting hash.
     *
     * @param rowData Data rows for calculation.
     * @return Hash value of the row.
     */
    public String getHash(Map<String, Object> rowData) {
        if (rowData == null) {
            return null;
        }

        StringBuilder hashBuilder = new StringBuilder();
        List<String> sortedKeyList = newArrayList(rowData.keySet());
        Collections.sort(sortedKeyList);

        for (String key : sortedKeyList) {
            Object entry = rowData.get(key);
            if (entry != null) {
                hashBuilder.append(entry.toString());
            }
        }
        return getHash(hashBuilder.toString().getBytes());

    }

    @Override
    public int hashCode() {
        return md.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HashCalculator other = (HashCalculator) obj;
        if (this.md != other.md && (this.md == null || !this.md.equals(other.md))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "md=" + md + '}';
    }

    //</editor-fold>


}
