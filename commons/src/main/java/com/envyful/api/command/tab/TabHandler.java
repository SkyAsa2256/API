package com.envyful.api.command.tab;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface TabHandler<A> {

    CompletableFuture<List<String>> getCompletions(A sender, String[] args);

}
