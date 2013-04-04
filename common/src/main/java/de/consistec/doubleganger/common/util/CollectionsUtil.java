package de.consistec.doubleganger.common.util;

/*
 * #%L
 * Project - doubleganger
 * File - CollectionsUtil.java
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
import de.consistec.doubleganger.common.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class with helper methods to operate on collections and to facilitate creation of generic collections in jdk6.
 *
 * @author Piotr Wieczorek
 * @company consistec Engineering and Consulting GmbH
 * @date 02.11.2012 15:07:25
 * @since 0.0.1-SNAPSHOT
 */
public final class CollectionsUtil {

    private CollectionsUtil() {
        throw new AssertionError("No instances allowed");
    }

    /*
     * CHECKSTYLE:OFF
     */

    /**
     * Fabricates generic HashMap instance.
     * <p/>
     *
     * @param <K> key type
     * @param <V> value type
     * @return new instance of generic HashMap
     */
    public static <K, V> HashMap<K, V> newHashMap() { //NOSONAR
        return new HashMap<K, V>();
    }

    /**
     * Fabricates synchronized generic Map instance backed by {@link java.util.HashMap}.
     * <p/>
     *
     * @param <K> key type
     * @param <V> value type
     * @return new synchronized instance of generic HashMap
     * @see java.util.Collections#synchronizedMap(java.util.Map)
     */
    public static <K, V> Map<K, V> newSyncMap() { //NOSONAR
        return Collections.synchronizedMap(new HashMap<K, V>());
    }

    /**
     * Fabricates concurrent generic Map instance.
     * <p/>
     *
     * @param <K> key type
     * @param <V> value type
     * @return new instance of generic ConcurrentHashMap.
     * @see java.util.concurrent.ConcurrentHashMap
     */
    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() { //NOSONAR
        return new ConcurrentHashMap<K, V>();
    }

    /**
     * Fabricates concurrent generic Map instance.
     * <p/>
     *
     * @param <K> key type
     * @param <V> value type
     * @param initialCapacity Initial capacity of new map.
     * @return new instance of generic ConcurrentHashMap.
     * @see java.util.concurrent.ConcurrentHashMap
     */
    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(int initialCapacity) { //NOSONAR
        return new ConcurrentHashMap<K, V>(initialCapacity);
    }

    /**
     * Fabricates generic HashMap instance with initial capacity.
     * <p/>
     *
     * @param <K> key type
     * @param <V> value type
     * @param initialCapacity
     * @return new instance of generic HashMap
     * @see java.util.HashMap#HashMap(int)
     */
    public static <K, V> HashMap<K, V> newHashMap(int initialCapacity) { //NOSONAR
        return new HashMap<K, V>(initialCapacity);
    }

    /**
     * Fabricates generic HashMap instance with initial capacity and load factor.
     * <p/>
     *
     * @param <K> key type
     * @param <V> value type
     * @param initialCapacity
     * @param loadFactor
     * @return new instance of generic HashMap
     * @see java.util.HashMap#HashMap(int, float)
     */
    public static <K, V> HashMap<K, V> newHashMap(int initialCapacity, float loadFactor) { //NOSONAR
        return new HashMap<K, V>(initialCapacity, loadFactor);
    }

    /**
     * Fabricates generic HashMap instance based on given map .
     * <p/>
     *
     * @param <K> key type
     * @param <V> value type
     * @param m base map
     * @return new instance of generic HashMap
     * @see java.util.HashMap#HashMap(java.util.Map)
     */
    public static <K, V> HashMap<K, V> newHashMap(Map<? extends K, ? extends V> m) { //NOSONAR
        return new HashMap<K, V>(m);
    }

    /**
     * Fabricates generic HashSet instance.
     * <p/>
     *
     * @param <T> value type
     * @return new instance of generic HashSet
     */
    public static <T> HashSet<T> newHashSet() { //NOSONAR
        return new HashSet<T>();
    }

    /**
     * Fabricates synchronized generic Set instance backed by {@link java.util.HashSet}.
     * <p/>
     *
     * @param <T> value type
     * @return new instance of synchronized generic Set
     * @see java.util.Collections#synchronizedSet(java.util.Set)
     */
    public static <T> Set<T> newSyncSet() { //NOSONAR
        return Collections.synchronizedSet(new HashSet<T>());
    }

    /**
     * Fabricates generic HashSet instance based on given collection.
     * <p/>
     *
     * @param <T> value type
     * @param c base collection
     * @return new instance of generic HashSet
     * @see java.util.HashSet#HashSet(java.util.Collection)
     */
    public static <T> HashSet<T> newHashSet(Collection<? extends T> c) { //NOSONAR
        return new HashSet<T>(c);
    }

    /**
     * Fabricate generic HashSet instance with initial capacity.
     * <p/>
     *
     * @param <T> value type
     * @param initialCapacity
     * @return new instance of generic HashSet
     * @see java.util.HashSet#HashSet(int)
     */
    public static <T> HashSet<T> newHashSet(int initialCapacity) { //NOSONAR
        return new HashSet<T>(initialCapacity);
    }

    /**
     * Fabricate generic HashSet instance with initial capacity and load factor.
     * <p/>
     *
     * @param <T> value type
     * @param initialCapacity
     * @param loadFactor
     * @return new instance of generic HashSet
     * @see java.util.HashSet#HashSet(int, float)
     */
    public static <T> HashSet<T> newHashSet(int initialCapacity, float loadFactor) { //NOSONAR
        return new HashSet<T>(initialCapacity, loadFactor);
    }

    /**
     * Fabricates generic ArrayList instance.
     * <p/>
     *
     * @param <V> value type
     * @return new instance of generic ArrayList
     */
    public static <V> ArrayList<V> newArrayList() { //NOSONAR
        return new ArrayList<V>();
    }

    /**
     * Fabricates generic ArrayList instance populated with collection <i>c</i>.
     * <p/>
     *
     * @param <V> value type
     * @param c
     * @return new instance of generic ArrayList
     * @see java.util.ArrayList#ArrayList(java.util.Collection)
     */
    public static <V> ArrayList<V> newArrayList(Collection<? extends V> c) { //NOSONAR
        return new ArrayList<V>(c);
    }

    /**
     * Fabricates generic ArrayList instance with <i>initialCapacity</i>.
     * <p/>
     *
     * @param <V> value type
     * @param initialCapacity
     * @return new instance of generic ArrayList
     * @see java.util.ArrayList#ArrayList(int)
     */
    public static <V> ArrayList<V> newArrayList(int initialCapacity) { //NOSONAR
        return new ArrayList<V>(initialCapacity);
    }

    public static <X, Y> List<Tuple<X, Y>> removeNullableValues(List<Tuple<X, Y>> tuples) {

        List<Tuple<X, Y>> tupleList = newArrayList();
        Collections.copy(tuples, tupleList);

        for (Tuple<X, Y> value : tupleList) {
            if (value.getValue1() == null) {
                tupleList.remove(value);
            }
        }
        return tupleList;
    }

    /**
     * Returns true iff a is a sub-collection of b, that is,
     * if the cardinality of e in a is less than or equal to the cardinality of e in b,
     * for each element e in a.
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean isSubCollection(final Collection a, final Collection b) {
        Map mapa = getCardinalityMap(a);
        Map mapb = getCardinalityMap(b);
        Iterator it = a.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (getFreq(obj, mapa) > getFreq(obj, mapb)) {
                return false;
            }
        }
        return true;
    }

    private static final int getFreq(final Object obj, final Map freqMap) {
        try {
            return ((Integer) (freqMap.get(obj))).intValue();
        } catch (NullPointerException e) {
            // ignored
        } catch (NoSuchElementException e) {
            // ignored
        }
        return 0;
    }

    private static Map getCardinalityMap(final Collection col) {
        HashMap count = new HashMap();
        Iterator it = col.iterator();

        while (it.hasNext()) {
            Object obj = it.next();
            Integer c = (Integer) (count.get(obj));
            if (null == c) {
                count.put(obj, Integer.valueOf(1));
            } else {
                count.put(obj, Integer.valueOf(c.intValue() + 1));
            }
        }
        return count;
    }

    /*
     * CHECKSTYLE:ON
     */

}
