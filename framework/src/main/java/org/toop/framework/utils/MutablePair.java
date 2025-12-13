package org.toop.framework.utils;

public class MutablePair<T, K> implements Pair<T,K> {
    T left;
    K right;

    public MutablePair(T left, K right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public T getLeft() {
        return left;
    }

    public void setLeft(T left) {
        this.left = left;
    }

    @Override
    public K getRight() {
        return right;
    }

    public void setRight(K right) {
        this.right = right;
    }
}
