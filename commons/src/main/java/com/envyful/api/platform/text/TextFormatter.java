package com.envyful.api.platform.text;

import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;

import java.util.Collection;
import java.util.List;

/**
 *
 * An interface for handling text formatting and conversion
 * <br>
 * This is used to handle the conversion of standard text to a specific type
 * such as a Component for Forge, or MiniMessage for Spigot.
 * <br>
 * This interface allows the internal conversion logic to be replaced by
 * the implementer, allowing for custom text formatting to be used.
 *
 * @param <T> The type of text to convert to
 */
public interface TextFormatter<T> {

    /**
     *
     * The default text formatter that does not convert the text.
     * Acts as {@link java.util.function.Function#identity()} for text.
     * <br>
     * This should be used when no conversion is needed and so is the default.
     *
     */
    TextFormatter<String> PLAIN = new TextFormatter<>() {
        @Override
        public List<String> parse(List<String> text, Placeholder... placeholders) {
            return PlaceholderFactory.handlePlaceholders(text, placeholders);
        }

        @Override
        public String parse(String text) {
            return text;
        }

        @Override
        public String unresolve(String text) {
            return text;
        }

        @Override
        public String strip(String text) {
            return text;
        }
    };

    /**
     *
     * Parses the text and replaces the placeholders
     *
     * @param text The text to parse
     * @param placeholders The placeholders to replace
     * @return The parsed text
     */
    default List<T> parse(String text, Placeholder... placeholders) {
        return this.parse(List.of(text), placeholders);
    }

    /**
     *
     * Parses the text and replaces the placeholders
     *
     * @param text The text to parse
     * @param placeholders The placeholders to replace
     * @return The parsed text
     */
    default List<T> parse(Collection<String> text, Placeholder... placeholders) {
        return this.parse(List.copyOf(text), placeholders);
    }

    /**
     *
     * Parses the text and replaces the placeholders
     *
     * @param text The text to parse
     * @param placeholders The placeholders to replace
     * @return The parsed text
     */
    List<T> parse(List<String> text, Placeholder... placeholders);

    /**
     *
     * Parses the text
     *
     * @param text The text to parse
     * @return The parsed text
     */
    T parse(String text);

    /**
     *
     * Unresolves the text from the type to it's original form
     *
     * @param text The text to unresolve
     * @return The unresolved text
     */
    String unresolve(T text);

    /**
     *
     * Strips the text of any formatting
     *
     * @param text The text to strip
     * @return The stripped text
     */
    String strip(String text);

    /**
     *
     * Gets the default text formatter that does not convert the text
     *
     * @return The default text formatter
     */
    static TextFormatter<String> plain() {
        return PLAIN;
    }
}
