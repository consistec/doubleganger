package de.consistec.doubleganger.common;

/*
 * #%L
 * Project - doppelganger
 * File - Tuple.java
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
 * The class {@code Tuple} represents a container which contains
 * a pair of two values of any type.
 *
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 12.12.12 16:40
 */
public class Tuple<X, Y> {

    private X value1;
    private Y value2;

    /**
     * Constructor of the class {@code Tuple}.
     *
     * @param value1 - value1 of the tuple pair
     * @param value2 - value2 of the tuple pair
     */
    public Tuple(X value1, Y value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    /**
     * returns the tuple pair in string format as follows: (value1, value2).
     *
     * @return string value of pair
     */
    @Override
    public String toString() {
        return "(" + value1 + ", " + value2 + ")";
    }

    /**
     * returns true if the passed object has the same values as an object of this class.
     *
     * @param other object to compare
     * @return true if values are the same otherwise false
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof Tuple)) {
            return false;
        }
        Tuple<X, Y> tmp = (Tuple<X, Y>) other;
        return tmp.value1 == this.value1 && tmp.value2 == this.value2;
    }

    /**
     * represents the hash code of an object of this class.
     *
     * @return hash code value
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value1 == null) ? 0 : value1.hashCode());
        result = prime * result + ((value1 == null) ? 0 : value2.hashCode());
        return result;
    }

    /**
     * returns the first value of the tuple pair.
     *
     * @return first value
     */
    public X getValue1() {
        return value1;
    }

    /**
     * returns the second value of the tuple pair.
     *
     * @return second value
     */
    public Y getValue2() {
        return value2;
    }

    /**
     * sets the first value of the tuple pair.
     *
     * @param value1 - value of first tuple pair to set
     */
    public void setValue1(X value1) {
        this.value1 = value1;
    }

    /**
     * sets the second value of the tuple pair.
     *
     * @param value2 - value of second tuple pair to set
     */
    public void setValue2(Y value2) {
        this.value2 = value2;
    }
}
