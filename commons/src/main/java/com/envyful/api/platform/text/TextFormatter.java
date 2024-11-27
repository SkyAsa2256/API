package com.envyful.api.platform.text;

import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;

import java.util.Collection;
import java.util.List;

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
    };

    default List<T> parse(String text, Placeholder... placeholders) {
        return this.parse(List.of(text), placeholders);
    }

    default List<T> parse(Collection<String> text, Placeholder... placeholders) {
        return this.parse(List.copyOf(text), placeholders);
    }

    List<T> parse(List<String> text, Placeholder... placeholders);

    T parse(String text);

    static TextFormatter<String> plain() {
        return PLAIN;
    }
}
