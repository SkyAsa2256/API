package com.envyful.api.type;

@FunctionalInterface
public interface ExceptionThrowingConsumer<T, D extends Throwable> {

    void accept(T t) throws D;

}
