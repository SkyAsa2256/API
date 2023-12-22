package com.envyful.api.command.injector;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 *
 * An interface for handling when a specific data type needs to have tab completions.
 *
 * @param <A> represents the sender object for the specific platform
 */
@FunctionalInterface
public interface TabCompleter<A> {

    /**
     *
     * Gets the tab completions for the sender with the given data (can be empty)
     *
     * @param sender The sender
     * @param currentData The data already provided by the sender
     * @param completionData The annotation provided on the tab complete
     * @return The tab completions generated
     */
    List<String> getCompletions(A sender, String[] currentData, Annotation... completionData);

}
