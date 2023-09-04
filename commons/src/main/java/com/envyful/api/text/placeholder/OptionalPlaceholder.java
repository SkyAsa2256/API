package com.envyful.api.text.placeholder;

import com.envyful.api.text.ParseResult;
import com.envyful.api.text.Placeholder;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

public class OptionalPlaceholder implements Placeholder {

    protected final BooleanSupplier test;
    protected final Placeholder placeholder;

    protected OptionalPlaceholder(BooleanSupplier test, Placeholder placeholder) {
        this.test = test;
        this.placeholder = placeholder;
    }

    @Nonnull
    @Override
    public ParseResult replace(@Nonnull ParseResult line) {
        if (!this.test.getAsBoolean()) {
            return line;
        }

        return this.placeholder.replace(line);
    }

    public static class Builder {

        protected BooleanSupplier test;
        protected Placeholder placeholder;

        public Builder() {}

        public Builder test(BooleanSupplier test) {
            this.test = test;
            return this;
        }

        public Builder placeholder(Placeholder placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public OptionalPlaceholder build() {
            return new OptionalPlaceholder(this.test, this.placeholder);
        }
    }
}
