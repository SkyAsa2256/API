package com.envyful.api.type;

import java.util.Objects;

/**
 *
 * A simple generic Pair implementation that takes two parameters and stores them providing as X and Y.
 * Provides a {@link Object#hashCode()} implementation and {@link Object#equals(Object)} implementation so that it
 * can be used in {@link java.util.HashMap}s and other hash types
 *
 * @param <X> The type of the first parameter
 * @param <Y> The type of the second parameter
 */
public class Pair<X, Y> {

    private final X x;
    private final Y y;

    protected Pair(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public X getX() {
        return this.x;
    }

    public Y getY() {
        return this.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Pair<?, ?> pair = (Pair<?, ?>) o;

        return Objects.equals(this.x, pair.x) && Objects.equals(this.y, pair.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    public static <A, B> Pair<A, B> of(A a, B b) {
        return new Pair<>(a, b);
    }
}
