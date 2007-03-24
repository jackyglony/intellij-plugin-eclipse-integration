package com.javaexpert.common;

import java.util.*;

/**
 * User: piotrga
 * Date: 2007-03-24
 * Time: 12:18:39
 */
public class SimpleList<T> implements Iterable<T> {

    ArrayList<T> internalList;

    public SimpleList(T[] elements) {
        this(Arrays.asList(elements));
    }

    public SimpleList(Collection<? extends T> ts) {
        internalList = new ArrayList<T>();
        internalList.addAll(ts);
    }

    public SimpleList<T> add(T t) {
        internalList.add(t);
        return this;
    }

    public SimpleList<T> add(T[] elements) {
        return add(list(elements));
    }

    public SimpleList<T> add(SimpleList<T> t) {
        internalList.addAll(t.elements());
        return this;
    }

    public T first() {
        return internalList.get(0);
    }

    public T last() {
        return internalList.get(internalList.size() - 1);
    }

    public boolean isEmpty() {
        return internalList.isEmpty();
    }

    private Collection<? extends T> elements() {
        return Collections.unmodifiableCollection(internalList);
    }

    public static <T> SimpleList<T> list(T[] elements) {
        return new SimpleList<T>(elements);
    }

    public Iterator<T> iterator() {
        return internalList.iterator();
    }

    public T[] toArray() {
        return (T[]) internalList.toArray();
    }
}
