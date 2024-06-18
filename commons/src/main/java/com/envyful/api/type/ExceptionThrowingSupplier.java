package com.envyful.api.type;

@FunctionalInterface
public interface ExceptionThrowingSupplier<T, D extends Throwable> {

    T get() throws D;

}
