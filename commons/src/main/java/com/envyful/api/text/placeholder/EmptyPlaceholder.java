package com.envyful.api.text.placeholder;

import com.envyful.api.text.parse.SimplePlaceholder;

/**
 *
 * An empty placeholder that will remove the line if the key is present
 *
 */
public class EmptyPlaceholder implements SimplePlaceholder {

    private final String key;

    protected EmptyPlaceholder(String key) {
        this.key = key;
    }

    public static EmptyPlaceholder of(String key) {
        return new EmptyPlaceholder(key);
    }

    @Override
    public String replace(String line) {
        if (line.contains(this.key)) {
            return null;
        }

        return line;
    }
}
