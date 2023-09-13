package com.envyful.api.config;

import com.envyful.api.text.parse.SimplePlaceholder;

/**
 *
 * Simple interface for replacing text in a string in an abstract fashion
 *
 */
@Deprecated
public interface Replacer extends SimplePlaceholder {

    /**
     *
     * Updates the text using the replacer logic
     *
     * @param text The original text
     * @return The updated text
     */
    String replaceText(String text);

    @Override
    default String replace(String line) {
        return replaceText(line);
    }

}
