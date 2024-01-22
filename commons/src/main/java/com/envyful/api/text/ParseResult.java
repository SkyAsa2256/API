package com.envyful.api.text;

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

}
