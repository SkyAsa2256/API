package com.envyful.api.type;

/**
 *
 * Functional interface for a consumer {@link java.util.function.Consumer} that throws a checked exception
 *
 * @param <T> The type of the input to the operation
 * @param <D> The type of the exception that can be thrown
 */
@FunctionalInterface
public interface ExceptionThrowingConsumer<T, D extends Throwable> {

    void accept(T t) throws D;

}
