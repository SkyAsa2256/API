package com.envyful.api.text.parse;

import com.envyful.api.text.ParseResult;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.results.ListParseResult;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Lower level interface for lambda usage of simply replacing text and returning the updated line
 *
 */
@FunctionalInterface
public interface SimplePlaceholder extends Placeholder {

    @Nonnull
    @Override
    default ParseResult replace(@Nonnull ParseResult line) {
        if (line.isEmpty()) {
            return line;
        }

        List<String> list = new ArrayList<>();

        for (String s : line.getCurrentResult()) {
            var replacement = replace(s);

            if (replacement == null) {
                continue;
            }

            list.add(replacement);
        }

        if (list.isEmpty()) {
            return ParseResult.empty(line.getOriginal());
        }

        return ListParseResult.of(line.getOriginal(), list);
    }

    String replace(String line);

}
