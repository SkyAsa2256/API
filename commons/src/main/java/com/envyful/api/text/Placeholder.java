package com.envyful.api.text;

import javax.annotation.Nonnull;

/**
 *
 * A high level interface for replacing text in a String, or collection of Strings
 *
 */
public interface Placeholder {

    /**
     *
     * Attempts to parse the results and then return new results
     *
     * @param line The results to parse
     * @return The new parse results
     */
    @Nonnull
    ParseResult replace(@Nonnull ParseResult line);

}
