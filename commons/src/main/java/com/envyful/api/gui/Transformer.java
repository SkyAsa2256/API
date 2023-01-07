package com.envyful.api.gui;

import com.envyful.api.text.parse.SimplePlaceholder;
import com.google.common.collect.Lists;

import java.util.List;

/**
 *
 * An interface used for placeholders in the item API for replacing said placeholders with given text.
 *
 * @deprecated Use {@link SimplePlaceholder}
 */
@Deprecated
public interface Transformer extends SimplePlaceholder {

    @Override
    default String replace(String line) {
        return this.transformName(line);
    }

    /**
     *
     * Applies the placeholder to the given name
     *
     * @param name The original name
     * @return The transformed name
     */
    String transformName(String name);

    /**
     *
     * Applies the placeholder to the given lore
     *
     * @param lore The original lore
     * @return The transformed lore
     */
    default List<String> transformLore(List<String> lore) {
        List<String> newLore = Lists.newArrayList();

        for (String s : lore) {
            newLore.add(this.transformName(s));
        }

        return newLore;
    }

}
