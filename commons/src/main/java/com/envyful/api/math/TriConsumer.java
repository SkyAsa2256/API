package com.envyful.api.math;

/**
 *
 * A functional interface for a consumer that takes three arguments
 *
 * @param <A> The first argument type
 * @param <B> The second argument type
 * @param <C> The third argument type
 */
@FunctionalInterface
public interface TriConsumer<A, B, C> {

    /**
     *
     * Executes the consumer
     *
     * @param a The first argument
     * @param b The second argument
     * @param c The third argument
     */
    void execute(A a, B b, C c);

}
