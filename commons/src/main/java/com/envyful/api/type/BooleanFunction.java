package com.envyful.api.type;

@FunctionalInterface
public interface BooleanFunction<A> {

    boolean test(A a);

}
