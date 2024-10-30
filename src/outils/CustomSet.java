package outils;

import java.util.HashSet;

import Exception.DuplicateMethodException;

public class CustomSet<E> extends HashSet<E> {
    @Override
    public boolean add(E e) {
        if (contains(e)) {
            throw new DuplicateMethodException("Duplicate method detected: " + e);
        }
        return super.add(e);
    }
}
