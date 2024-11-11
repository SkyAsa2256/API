package com.envyful.api.text;

import com.envyful.api.text.results.OriginalParseResult;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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

    private static final List<Placeholder> GLOBAL_PLACEHOLDERS = new CopyOnWriteArrayList<>();

    private PlaceholderFactory() {
        throw new UnsupportedOperationException("This is a static factory class");
    }

    /**
     *
     * Adds a global placeholder that will attempt to parse every time text is parsed for placeholders
     *
     * @param placeholder The placeholder being added
     */
    public static void addGlobalPlaceholder(@NonNull Placeholder placeholder) {
        GLOBAL_PLACEHOLDERS.add(placeholder);
    }

    /**
     *
     * Gets all the global placeholders
     *
     * @return The global placeholders
     */
    @NonNull
    public static List<Placeholder> getGlobalPlaceholders() {
        return GLOBAL_PLACEHOLDERS;
    }

    @NonNull
    public static List<String> handlePlaceholders(String text, Placeholder... placeholders) {
        return handlePlaceholders(Collections.singletonList(text), placeholders);
    }

    @NonNull
    public static List<String> handlePlaceholders(String text, Collection<Placeholder> placeholders) {
        return handlePlaceholders(Collections.singletonList(text), placeholders);
    }

    @NonNull
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
    @NonNull
    public static List<String> handlePlaceholders(List<String> text, Placeholder... placeholders) {
        List<String> computedText = new ArrayList<>();

        for (var line : text) {
            if (line == null) {
                continue;
            }

            var result = OriginalParseResult.of(line);

            for (var placeholder : placeholders) {
                result = placeholder.replace(result);
            }

            for (var globalPlaceholder : getGlobalPlaceholders()) {
                result = globalPlaceholder.replace(result);
            }

            computedText.addAll(result.getCurrentResult());
        }

        return computedText;
    }

    @NonNull
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
    @NonNull
    public static <T> List<T> handlePlaceholders(List<String> text, Function<String, T> mapper, Placeholder... placeholders) {
        List<T> computedText = new ArrayList<>();

        for (String line : text) {
            if (line == null) {
                continue;
            }

            var result = OriginalParseResult.of(line);

            for (var placeholder : placeholders) {
                result = placeholder.replace(result);
            }

            for (var globalPlaceholder : getGlobalPlaceholders()) {
                result = globalPlaceholder.replace(result);
            }

            for (var s : result.getCurrentResult()) {
                if (s != null) {
                    computedText.add(mapper.apply(s));
                }
            }
        }

        return computedText;
    }
}
