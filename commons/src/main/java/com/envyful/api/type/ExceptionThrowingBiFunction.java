package com.envyful.api.type;

@FunctionalInterface
public interface ExceptionThrowingBiFunction<A, B, C, D extends Throwable> {

    C get(A a, B b) throws D;

}
