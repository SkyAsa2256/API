package com.envyful.api.gui;

import java.util.List;

/**
 *
 * An interface used for placeholders in the item API for replacing said placeholders with given text.
 *
 */
public interface Transformer {

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
    List<String> transformLore(List<String> lore);

}
