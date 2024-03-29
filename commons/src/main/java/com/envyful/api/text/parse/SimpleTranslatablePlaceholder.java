package com.envyful.api.text.parse;

import java.util.function.UnaryOperator;

/**
 *
 * A simple implementation of a translatable placeholder
 *
 */
public class SimpleTranslatablePlaceholder implements TranslatablePlaceholder {

    private final String key;
    private final String value;
    private final String replaced;

    private SimpleTranslatablePlaceholder(String key, String value) {
        this.key = key;
        this.value = value;
        this.replaced = "%" + key + "%";
    }

    @Override
    public String replace(String line) {
        return line.replace(this.replaced, this.value);
    }

    @Override
    public TranslatablePlaceholder transform(UnaryOperator<String> keyModifier) {
        return new SimpleTranslatablePlaceholder(keyModifier.apply(this.key), this.value);
    }

    @Override
    public TranslatablePlaceholder prefix(String prefix) {
        return new SimpleTranslatablePlaceholder(prefix + this.key, this.value);
    }

    @Override
    public TranslatablePlaceholder suffix(String suffix) {
        return new SimpleTranslatablePlaceholder(this.key + suffix, this.value);
    }

    public static TranslatablePlaceholder of(String key, String value) {
        return new SimpleTranslatablePlaceholder(key, value);
    }
}
