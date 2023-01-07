package com.envyful.api.text.results;

import com.envyful.api.text.ParseResult;

import java.util.List;

/**
 *
 * A parse result where one line was turned into potentially many
 *
 */
public class ListParseResult implements ParseResult {

    protected final String original;
    protected final List<String> lines;

    private ListParseResult(String original, List<String> lines) {
        this.original = original;
        this.lines = lines;
    }

    @Override
    public String getOriginal() {
        return this.original;
    }

    @Override
    public List<String> getCurrentResult() {
        return this.lines;
    }

    public static ParseResult of(String original, List<String> lines) {
        return new ListParseResult(original, lines);
    }
}
