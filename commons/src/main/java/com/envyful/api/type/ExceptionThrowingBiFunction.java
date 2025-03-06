package com.envyful.api.type;

/**
 *
 * Functional interface for a bi-function {@link java.util.function.BiFunction} that can throw a checked exception
 *
 * @param <A> The first parameter type
 * @param <B> The second parameter type
 * @param <C> The return type
 * @param <D> The exception type
 */
@FunctionalInterface
public interface ExceptionThrowingBiFunction<A, B, C, D extends Throwable> {

    C get(A a, B b) throws D;

}
