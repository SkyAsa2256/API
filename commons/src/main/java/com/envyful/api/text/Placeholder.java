package com.envyful.api.text;

import com.envyful.api.text.parse.MultiPlaceholder;
import com.envyful.api.text.parse.SimplePlaceholder;
import com.envyful.api.text.placeholder.OptionalPlaceholder;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.List;
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
    @NonNull
    ParseResult replace(@NonNull ParseResult line);

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
     * Gets a simple placeholder instance for the given key and result
     *
     * @param key The key to replace in the text
     * @param result The result to replace the key with
     * @return The placeholder instance
     */
    static Placeholder simple(String key, String result) {
        return simple(s -> s.replace(key, result));
    }

    /**
     *
     * Creates a placeholder that matches the key and then returns the given result
     *
     * @param key The key to match
     * @param result The result to return
     * @return The placeholder instance
     */
    static Placeholder multiLine(String key, List<String> result) {
        return (MultiPlaceholder) s -> {
            if (s.equals(key)) {
                return result;
            }

            return Collections.singletonList(s);
        };
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
