package com.envyful.api.text;

import com.envyful.api.text.results.EmptyParseResult;

import java.util.List;

/**
 *
 * The result of attempting to replace placeholders in text
 *
 */
public interface ParseResult {

    /**
     *
     * The original singular line
     *
     * @return The original text
     */
    String getOriginal();

    /**
     *
     * The result of trying to parse the text
     *
     * @return The result
     */
    List<String> getCurrentResult();

    /**
     *
     * Checks if the result is empty
     *
     * @return If the result is empty
     */
    default boolean isEmpty() {
        return this.getCurrentResult().isEmpty();
    }

    /**
     *
     * Gets an empty parse result
     *
     * @param original The original text
     * @return The empty parse result
     */
    static ParseResult empty(String original) {
        return EmptyParseResult.of(original);
    }

}
