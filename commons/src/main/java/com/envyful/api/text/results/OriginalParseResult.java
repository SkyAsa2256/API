package com.envyful.api.text.results;

import com.envyful.api.text.ParseResult;

import java.util.Collections;
import java.util.List;

/**
 *
 * A parse result where no operation has occurred
 *
 */
public class OriginalParseResult implements ParseResult {

    protected final String original;

    private OriginalParseResult(String original) {
        this.original = original;
    }

    @Override
    public String getOriginal() {
        return this.original;
    }

    @Override
    public List<String> getCurrentResult() {
        return Collections.singletonList(this.original);
    }

    public static ParseResult of(String original) {
        return new OriginalParseResult(original);
    }
}
