package com.envyful.api.type;

/**
 *
 * Functional interface for a supplier {@link java.util.function.Supplier} that can throw a checked exception
 *
 * @param <T> The type of the object being supplied
 * @param <D> The type of the exception being thrown
 */
@FunctionalInterface
public interface ExceptionThrowingSupplier<T, D extends Throwable> {

    T get() throws D;

}
