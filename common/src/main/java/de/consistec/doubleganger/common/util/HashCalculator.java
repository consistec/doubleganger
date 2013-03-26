package de.consistec.doubleganger.common.util;

/*
 * #%L
 * Project - doppelganger
 * File - HashCalculator.java
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
import static de.consistec.doubleganger.common.MdTableDefaultValues.MDV_MODIFIED_VALUE;
import static de.consistec.doubleganger.common.util.CollectionsUtil.newArrayList;

import de.consistec.doubleganger.common.ConfigConstants;
import de.consistec.doubleganger.common.data.Change;

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

    /**
     * The Constant HEXES.
     */
    private static final String HEXES = "0123456789ABCDEF";
    /**
     * The message digest.
     */
    private MessageDigest md;

    /**
     * Instantiates a new HashCalculator.
     *
     * @throws NoSuchAlgorithmException exception if the hashcalculator can't find the right algorithm
     */
    public HashCalculator() throws NoSuchAlgorithmException {
        this(null);
    }

    /**
     * Instantiates a new HashCalculator with the given algorithm.
     *
     * @param algorithm Hash algorithm
     * @throws NoSuchAlgorithmException exception if the hashcalculator can't find the right algorithm
     */
    public HashCalculator(String algorithm) throws NoSuchAlgorithmException {
        if (algorithm == null || "".equals(algorithm)) {
            this.md = MessageDigest.getInstance(ConfigConstants.DEFAULT_HASH_ALGORITHM);
        } else {
            this.md = MessageDigest.getInstance(algorithm);
        }
    }

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
     * Computes the doubleganger hash value for this change, considering triggers as deactivated.
     * <p/>
     * @param change the change
     * @return hash value as string
     * @throws NoSuchAlgorithmException
     */
    public String calculateHash(Change change) {
        return calculateHash(change, false);
    }

    /**
     * Computes the doubleganger hash value for this change, or returns the default value if triggers are activated.
     * <p/>
     * @param change the change
     * @param isTriggerActivated are triggers activated?
     * @return hash value as string or default value if triggers are activated
     * @throws NoSuchAlgorithmException
     */
    public String calculateHash(Change change, boolean isTriggerActivated) {
        if (change == null) {
            return null;
        }

        if (isTriggerActivated) {
            return MDV_MODIFIED_VALUE;
        } else {
            return calculateHash(change.getRowData());
        }
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
    public String calculateHash(Map<String, Object> rowData) {
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

    /**
     * Returns hash of given bytes.
     *
     * @param bytes Data for calculation.
     * @return Hash value of provided bytes
     */
    private String getHash(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return getHex(md.digest(bytes));
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
}
