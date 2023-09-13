package com.envyful.api.command.tab;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface SimpleTabHandler<A> extends TabHandler<A> {

    List<String> complete(A sender, String[] args);

    @Override
    default CompletableFuture<List<String>> getCompletions(A sender, String[] args) {
        return CompletableFuture.completedFuture(this.complete(sender, args));
    }
}
