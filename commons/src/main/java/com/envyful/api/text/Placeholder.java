package com.envyful.api.text;

import com.envyful.api.text.parse.SimplePlaceholder;
import com.envyful.api.text.placeholder.OptionalPlaceholder;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;
import java.util.function.UnaryOperator;

/**
 *
 * A high level interface for replacing text in a
 * String, or collection of Strings
 *
 */
public interface Placeholder {

    /**
     *
     * Attempts to parse the results and then return new results
     *
     * @param line The results to parse
     * @return The new parse results
     */
    @Nonnull
    ParseResult replace(@Nonnull ParseResult line);

    /**
     *
     * Gets a simple placeholder instance for the given function
     *
     * @param replacer The function handling text replacement
     * @return The placeholder instance
     */
    static Placeholder simple(UnaryOperator<String> replacer) {
        return (SimplePlaceholder) replacer::apply;
    }

    /**
     *
     * Creates a builder instance for an {@link OptionalPlaceholder}
     * with the given test as the predicate
     *
     * @param test The required test
     * @return The placeholder builder created
     */
    static OptionalPlaceholder.Builder require(BooleanSupplier test) {
        return new OptionalPlaceholder.Builder().test(test);
    }
}
