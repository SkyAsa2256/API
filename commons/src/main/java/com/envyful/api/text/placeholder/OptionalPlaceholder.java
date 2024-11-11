package com.envyful.api.text.placeholder;

import com.envyful.api.text.ParseResult;
import com.envyful.api.text.Placeholder;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.BooleanSupplier;

/**
 *
 * A placeholder that will only call the placeholder if the test is true otherwise it will call the else placeholder
 *
 */
public class OptionalPlaceholder implements Placeholder {

    protected final BooleanSupplier test;
    protected final Placeholder placeholder;
    protected final Placeholder elsePlaceholder;

    protected OptionalPlaceholder(BooleanSupplier test, Placeholder placeholder, Placeholder elsePlaceholder) {
        this.test = test;
        this.placeholder = placeholder;
        this.elsePlaceholder = elsePlaceholder;
    }

    @NonNull
    @Override
    public ParseResult replace(@NonNull ParseResult line) {
        if (!this.test.getAsBoolean()) {
            if (this.elsePlaceholder != null) {
                return this.elsePlaceholder.replace(line);
            }

            return line;
        }

        return this.placeholder.replace(line);
    }

    public static class Builder {

        protected BooleanSupplier test;
        protected Placeholder placeholder;
        protected Placeholder elsePlaceholder;

        public Builder() {}

        public Builder test(BooleanSupplier test) {
            this.test = test;
            return this;
        }

        public Builder placeholder(Placeholder placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder elsePlaceholder(Placeholder elsePlaceholder) {
            this.elsePlaceholder = elsePlaceholder;
            return this;
        }

        public OptionalPlaceholder build() {
            return new OptionalPlaceholder(this.test, this.placeholder, this.elsePlaceholder);
        }
    }
}
