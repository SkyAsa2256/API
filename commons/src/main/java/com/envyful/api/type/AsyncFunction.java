package com.envyful.api.type;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 *
 * A function that will execute and return a CompletableFuture
 *
 * @param <A> The input type
 * @param <B> The output type
 */
@FunctionalInterface
public interface AsyncFunction<A, B> extends Function<A, CompletableFuture<B>> {

}
