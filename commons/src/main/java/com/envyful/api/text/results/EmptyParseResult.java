package com.envyful.api.text.results;

import com.envyful.api.text.ParseResult;

import java.util.List;

/**
 *
 * An empty implementation of the {@link ParseResult} interface
 *
 */
public class EmptyParseResult implements ParseResult {

    protected final String original;

    private EmptyParseResult(String original) {
        this.original = original;
    }

    @Override
    public String getOriginal() {
        return this.original;
    }

    @Override
    public List<String> getCurrentResult() {
        return List.of();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    public static ParseResult of(String original) {
        return new EmptyParseResult(original);
    }
}
