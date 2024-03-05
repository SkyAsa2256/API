package com.envyful.api.command.tab;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 *
 * An interface for handling tab completions for commands
 * but covering the case where no async operations are needed
 *
 * @param <A> The sender type
 */
@FunctionalInterface
public interface SimpleTabHandler<A> extends TabHandler<A> {

    /**
     *
     * The method to complete the tab
     *
     * @param sender The sender
     * @param args The arguments
     * @return The list of completions
     */
    List<String> complete(A sender, String[] args);

    @Override
    default CompletableFuture<List<String>> getCompletions(A sender, String[] args) {
        return CompletableFuture.completedFuture(this.complete(sender, args));
    }
}
