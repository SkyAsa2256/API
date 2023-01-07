package com.envyful.api.text.parse;

import com.envyful.api.text.ParseResult;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.results.ListParseResult;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.List;

/**
 *
 * Lower level interface for lambda usage of simply replacing text and returning the updated line
 *
 */
public interface SimplePlaceholder extends Placeholder {

    @Nonnull
    @Override
    default ParseResult replace(@Nonnull ParseResult line) {
        List<String> list = Lists.newArrayList();

        for (String s : line.getCurrentResult()) {
            list.add(replace(s));
        }

        return ListParseResult.of(line.getOriginal(), list);
    }

    String replace(String line);

}
