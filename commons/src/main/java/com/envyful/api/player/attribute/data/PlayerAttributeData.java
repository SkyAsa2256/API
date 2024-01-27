package com.envyful.api.player.attribute.data;

import com.envyful.api.player.attribute.Attribute;

/**
 *
 * A simple object used for efficiently instantiating the {@link Attribute} classes using reflection.
 *
 * The constructor of the {@link Attribute} is stored for future efficiency and thus allowing for easy attribute
 * instantiation
 *
 */
public class PlayerAttributeData {

    private final Class<? extends Attribute<?>> attributeClass;

    /**
     *
     * Passing the manager and attribute class allows for the object to get the necessary classes, and constructors
     * using this information for later instantiation
     *
     * @param attributeClass The class of the attribute being stored
     */
    public PlayerAttributeData(
            Class<? extends Attribute<?>> attributeClass
    ) {
        this.attributeClass = attributeClass;
    }

    public Class<? extends Attribute<?>> getAttributeClass() {
        return this.attributeClass;
    }

}
