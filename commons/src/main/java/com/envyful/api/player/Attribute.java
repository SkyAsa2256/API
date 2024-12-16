package com.envyful.api.player;

import com.envyful.api.player.attribute.AttributeHolder;

import java.util.UUID;

/**
 *
 * An interface representing data stored about something, typically a player
 *
 */
@SuppressWarnings({"unused"})
public interface Attribute {

    /**
     *
     * Gets the unique identifier for the attribute
     *
     * @return The unique identifier
     */
    UUID getId();

    /**
     *
     * If the attribute can save to the database at this time
     *
     * @return True if it can save
     */
    default boolean shouldSave() {
        return true;
    }

    /**
     *
     * This method is called when all the registered attributes have finished their
     * loading logic. This is useful for when you need to perform logic that requires
     * other attributes to be loaded first.
     *
     */
    default void onPlayerLoaded() {}

    /**
     *
     * This method is called when the player logs out of the server
     *
     */
    default void onPlayerQuit() {}

    /**
     *
     * Creates a new instance of the attribute builder
     *
     * @param attributeClass The class of the attribute
     * @return The builder
     * @param <A> The attribute type
     * @param <B> The platform player type
     */
    static <A extends Attribute, B extends AttributeHolder> AttributeBuilder<A, B> builder(Class<A> attributeClass) {
        return new AttributeBuilder<A, B>().attributeClass(attributeClass);
    }

    /**
     *
     * Creates a new instance of the attribute builder
     *
     * @param attributeClass The class of the attribute
     * @return The builder
     * @param <A> The attribute type
     * @param <B> The platform player type
     */
    static <A extends Attribute, B extends AttributeHolder> AttributeBuilder<A, B> builder(Class<A> attributeClass, Class<B> playerClass) {
        return new AttributeBuilder<A, B>().attributeClass(attributeClass);
    }

}
