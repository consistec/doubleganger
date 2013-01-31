package de.consistec.syncframework.common;

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
