package com.envyful.api.player.save;

import com.envyful.api.player.Attribute;
import com.envyful.api.player.attribute.AttributeHolder;
import com.envyful.api.player.attribute.adapter.AttributeAdapter;
import com.envyful.api.player.attribute.data.AttributeData;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 *
 * Used for handling the saving and loading of attribute data
 *
 * @param <T> The player type
 */
public interface SaveManager<T extends AttributeHolder> {

    /**
     *
     * Gets the key used to identify the type of save mode globally used
     *
     * @return The save mode key
     */
    String getSaveMode();

    /**
     *
     * Sets the save mode for this save manager
     *
     * @param saveMode The save mode key
     */
    void setSaveMode(String saveMode);

    /**
     *
     * Gets the save mode for a specific attribute
     *
     * @param attributeClass The class of the attribute
     * @param <A> The attribute type
     * @return The save mode key
     */
    <A extends Attribute> String getSaveMode(Class<A> attributeClass);

    /**
     *
     * Gets the adapter for a specific attribute
     *
     * @param attributeClass The class of the attribute
     * @param <A> The attribute type
     * @return The adapter
     */
    <A extends Attribute> AttributeAdapter<A> getAdapter(Class<A> attributeClass);

    /**
     *
     * Overrides the save mode for a specific attribute
     *
     * @param attributeClass The class of the attribute
     * @param saveMode The save mode key
     * @param <A> The attribute type
     */
    <A extends Attribute> void overrideSaveMode(Class<A> attributeClass, String saveMode);

    /**
     *
     * Registers an attribute for how this class should handle saving and loading
     *
     * @param attribute The class of the attribute being registered
     * @param <A> The attribute type
     */
    <A extends Attribute> void registerAttribute(AttributeData<A, T> attribute);

    /**
     *
     * Gets the error handler set for the save manager.
     * <br>
     * The error handler is used exclusively for when an error occurs
     * during the loading of an attribute.
     * <br>
     * The default error logger will always just log the error directly to
     * {@link com.envyful.api.concurrency.UtilLogger} and then return
     *
     * @return The error handler
     */
    BiConsumer<T, Throwable> getErrorHandler();

    /**
     *
     * Saves the player's data from the given attribute
     *
     * @param attribute The attribute being saved
     */
    void saveData(Attribute attribute);

    /**
     *
     * Loads the data for a single attribute using the given id
     *
     * @param attributeClass The class of the attribute
     * @param id The id to load the data using
     * @return The attribute instance
     * @param <A> The attribute type
     */
    <A extends Attribute> CompletableFuture<A> loadAttribute(Class<? extends A> attributeClass, UUID id);

}
