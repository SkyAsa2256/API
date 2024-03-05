package com.envyful.api.command.tab;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 *
 * Interface for handling tab completions for commands
 *
 * @param <A> The sender type
 */
@FunctionalInterface
public interface TabHandler<A> {

    /**
     *
     * Gets the completions for the sender and args
     *
     * @param sender The sender
     * @param args The arguments
     * @return The future of the completions
     */
    CompletableFuture<List<String>> getCompletions(A sender, String[] args);

}
