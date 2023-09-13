package com.envyful.api.type;

@FunctionalInterface
public interface BooleanBiFunction<A, B> {

    boolean test(A a, B b);

}
