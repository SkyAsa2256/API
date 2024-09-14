package com.envyful.api.type;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 *
 * A function that will execute and return a CompletableFuture
 *
 * @param <A> The input type
 * @param <B> The second input type
 * @param <C> The output type
 */
@FunctionalInterface
public interface BiAsyncFunction<A, B, C> extends BiFunction<A, B, CompletableFuture<C>> {

}
