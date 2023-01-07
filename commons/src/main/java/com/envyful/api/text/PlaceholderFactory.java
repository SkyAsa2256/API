package com.envyful.api.text;

import com.envyful.api.text.results.OriginalParseResult;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 *
 * This class is used for handling replacing placeholders in text
 *
 * Registering a global placeholder here will allow it to be replaced in the mod that this is shaded in
 * Global placeholders are parsed after the local ones however
 *
 */
public class PlaceholderFactory {

    private static final List<Placeholder> GLOBAL_PLACEHOLDERS = Lists.newArrayList();

    private PlaceholderFactory() {
        throw new UnsupportedOperationException("This is a static factory class");
    }

    /**
     *
     * Adds a global placeholder that will attempt to parse every time text is parsed for placeholders
     *
     * @param placeholder The placeholder being added
     */
    public static void addGlobalPlaceholder(@Nonnull Placeholder placeholder) {
        GLOBAL_PLACEHOLDERS.add(placeholder);
    }

    /**
     *
     * Gets all the global placeholders
     *
     * @return The global placeholders
     */
    @Nonnull
    public static List<Placeholder> getGlobalPlaceholders() {
        return GLOBAL_PLACEHOLDERS;
    }

    @Nonnull
    public static List<String> handlePlaceholders(List<String> text, Collection<Placeholder> placeholders) {
        return handlePlaceholders(text, placeholders.toArray(new Placeholder[0]));
    }

    /**
     *
     * Handles replacing the placeholders given, and global placeholders, in a list of Strings
     *
     * @param text The strings to replace in
     * @param placeholders The local placeholders to look for
     * @return The replaced text
     */
    @Nonnull
    public static List<String> handlePlaceholders(List<String> text, Placeholder... placeholders) {
        List<String> computedText = Lists.newArrayList();

        for (int i = 0; i < text.size(); i++) {
            String line = text.get(i);

            if (line == null) {
                continue;
            }

            ParseResult result = OriginalParseResult.of(line);

            for (Placeholder placeholder : placeholders) {
                result = placeholder.replace(result);
            }

            for (Placeholder globalPlaceholder : getGlobalPlaceholders()) {
                result = globalPlaceholder.replace(result);
            }

            computedText.addAll(result.getCurrentResult());
        }

        return computedText;
    }
    @Nonnull
    public static <T> List<T> handlePlaceholders(List<String> text, Function<String, T> mapper, Collection<Placeholder> placeholders) {
        return handlePlaceholders(text, mapper, placeholders.toArray(new Placeholder[0]));
    }

    /**
     *
     * Handles replacing the placeholders given, and global placeholders, in a list of Strings and then convert the Strings
     * into the desired type
     *
     * @param text The strings to replace in
     * @param mapper This is used to map from the String to the desired object
     * @param placeholders The local placeholders to look for
     * @return The replaced text
     * @param <T> The type you're mapping to
     */
    @Nonnull
    public static <T> List<T> handlePlaceholders(List<String> text, Function<String, T> mapper, Placeholder... placeholders) {
        List<T> computedText = Lists.newArrayList();

        for (int i = 0; i < text.size(); i++) {
            String line = text.get(i);

            if (line == null) {
                continue;
            }

            ParseResult result = OriginalParseResult.of(line);

            for (Placeholder placeholder : placeholders) {
                result = placeholder.replace(result);
            }

            for (Placeholder globalPlaceholder : getGlobalPlaceholders()) {
                result = globalPlaceholder.replace(result);
            }

            for (String s : result.getCurrentResult()) {
                computedText.add(mapper.apply(s));
            }
        }

        return computedText;
    }
}
