package com.envyful.api.player;

/**
 *
 * An interface representing data stored about something, typically a player
 *
 * @param <A> The unique identifier type
 */
@SuppressWarnings({"unused"})
public interface Attribute<A> {

    /**
     *
     * Gets the unique identifier for the attribute
     *
     * @return The unique identifier
     */
    A getId();

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
     * @param <B> The id type
     * @param <C> The platform player type
     */
    static <A extends Attribute<B>, B, C> AttributeBuilder<A, B, C> builder(Class<A> attributeClass) {
        return new AttributeBuilder<A, B, C>().attributeClass(attributeClass);
    }

    /**
     *
     * Creates a new instance of the attribute builder
     *
     * @param attributeClass The class of the attribute
     * @return The builder
     * @param <A> The attribute type
     * @param <B> The id type
     * @param <C> The platform player type
     */
    static <A extends Attribute<B>, B, C> AttributeBuilder<A, B, C> builder(Class<A> attributeClass, Class<C> playerClass) {
        return new AttributeBuilder<A, B, C>().attributeClass(attributeClass);
    }

}
