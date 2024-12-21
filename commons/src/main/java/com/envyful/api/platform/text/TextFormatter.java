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

    TextFormatter<String> PLAIN = new TextFormatter<String>() {
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

    default List<T> parse(String text, Placeholder... placeholders) {
        return this.parse(List.of(text), placeholders);
    }

    default List<T> parse(Collection<String> text, Placeholder... placeholders) {
        return this.parse(List.copyOf(text), placeholders);
    }

    List<T> parse(List<String> text, Placeholder... placeholders);

    T parse(String text);

    String unresolve(T text);

    String strip(String text);

    static TextFormatter<String> plain() {
        return PLAIN;
    }
}
