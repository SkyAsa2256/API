package com.envyful.api.text.parse;

import com.envyful.api.text.ParseResult;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.results.ListParseResult;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Lower level interface for lambda usage of replacing text with potentially multiple resultant lines
 *
 */
public interface MultiPlaceholder extends Placeholder {

    @Nonnull
    @Override
    default ParseResult replace(@Nonnull ParseResult line) {
        List<String> list = new ArrayList<>();

        for (String s : line.getCurrentResult()) {
            list.addAll(replace(s));
        }

        return ListParseResult.of(line.getOriginal(), list);
    }

    List<String> replace(String text);

}
