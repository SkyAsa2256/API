package com.envyful.api.player.attribute;

import com.envyful.api.player.Attribute;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.Identifiable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 *
 * An interface representing an object that can have attributes
 * <br>
 * The only example in this API is {@link EnvyPlayer} and it's implementations
 *
 */
public interface AttributeHolder extends Identifiable {

    /**
     *
     * Gets all the attributes for the player
     *
     * @return All the attributes
     */
    List<Attribute<?>> getAttributes();

    /**
     *
     * Get the attribute for the player
     * <br>
     * Returns a completable future that is completed once the full attribute has been loaded
     *
     * @param attributeClass The attribute class
     * @return A completable future
     * @param <A> The attribute type
     * @param <B> The attribute id type
     */
    <A extends Attribute<B>, B> CompletableFuture<A> getAttribute(Class<A> attributeClass);

    /**
     *
     * Get the attribute for the player
     * <br>
     * Returns a completable future that is completed once the full attribute has been loaded
     *
     * @param attributeClass The attribute class
     * @param consumer The consumer to accept the attribute
     * @param <A> The attribute type
     * @param <B> The attribute id type
     */
    default <A extends Attribute<B>, B> void getAttribute(Class<A> attributeClass, Consumer<A> consumer) {
        var loading = this.getAttribute(attributeClass);

        if (loading == null) {
            return;
        }

        loading.thenAccept(consumer);
    }

    /**
     *
     * Checks if the player has the attribute
     *
     * @param attributeClass The attribute class
     * @return If the player has the attribute
     * @param <A> The attribute type
     * @param <B> The attribute id type
     */
    <A extends Attribute<B>, B> boolean hasAttribute(Class<A> attributeClass);

    /**
     *
     * A method to test if the player has the attribute and if it passes the test
     * <br>
     * If the player does not have the attribute, this will return false
     *
     * @param attributeClass The attribute class
     * @param test The test to run on the attribute
     * @return If the player has the attribute and it passes the test
     * @param <A> The attribute type
     * @param <B> The attribute id type
     */
    default <A extends Attribute<B>, B> boolean testAttribute(Class<A> attributeClass, Predicate<A> test) {
        if (!this.hasAttribute(attributeClass)) {
            return false;
        }

        return test.test(this.getAttributeNow(attributeClass));
    }

    /**
     *
     * Gets the attribute for the player immediately
     * <br>
     * NOTE: This will hold the current thread until the attribute has been loaded if it is not already
     *
     *
     * @param attributeClass The attribute class
     * @return The attribute instance
     * @param <A> The attribute type
     * @param <B> The attribute id type
     */
    <A extends Attribute<B>, B> A getAttributeNow(Class<A> attributeClass);

    /**
     *
     * Sets the attribute for the player
     *
     * @param attribute The attribute to set
     * @param <A> The attribute type
     * @param <B> The attribute id type
     */
    <A extends Attribute<B>, B> void setAttribute(Class<A> attributeClass, CompletableFuture<A> attribute);

    /**
     *
     * Sets the attribute for the player
     *
     * @param attribute The attribute to set
     * @param <A> The attribute type
     */
    <A extends Attribute<B>, B> void setAttribute(A attribute);

    /**
     *
     * Removes the attribute from the player
     *
     * @param attributeClass The attribute class
     * @param <A> The attribute type
     * @param <B> The attribute id type
     */
    <A extends Attribute<B>, B> A removeAttribute(Class<A> attributeClass);

}
