package org.toop.framework.utils;

public class ImmutablePair<T, K> implements Pair<T,K> {
    final T left;
    final K right;

    public ImmutablePair(T left, K right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public T getLeft() {
        return left;
    }

    @Override
    public K getRight() {
        return right;
    }
}
