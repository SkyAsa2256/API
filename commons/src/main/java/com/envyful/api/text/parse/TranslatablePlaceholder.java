package com.envyful.api.text.parse;

import java.util.function.UnaryOperator;


/**
 *
 * A placeholder implementation that can be translated
 *
 */
public interface TranslatablePlaceholder extends SimplePlaceholder {

    /**
     *
     * Applies the modifier to the placeholder key.
     * <br>
     * The modifier is a function that takes the key and returns the new key
     *
     * @param keyModifier The modifier
     * @return The new placeholder
     */
    TranslatablePlaceholder transform(UnaryOperator<String> keyModifier);

    /**
     *
     * Adds the prefix to the start of the placeholder key
     *
     * @param prefix The prefix
     * @return The new placeholder
     */
    TranslatablePlaceholder prefix(String prefix);

    /**
     *
     * Adds the suffix to the end of the placeholder key
     *
     * @param suffix The suffix
     * @return The new placeholder
     */
    TranslatablePlaceholder suffix(String suffix);

}
