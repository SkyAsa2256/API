package com.envyful.api.player.attribute.manager;

import com.envyful.api.config.database.DatabaseDetailsConfig;
import com.envyful.api.config.database.DatabaseDetailsRegistry;
import com.envyful.api.player.Attribute;
import com.envyful.api.player.AttributeBuilder;
import com.envyful.api.player.attribute.AttributeHolder;
import com.envyful.api.player.attribute.adapter.AttributeAdapter;
import com.envyful.api.player.attribute.data.AttributeData;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface AttributeManager<A extends AttributeHolder> {

    /**
     *
     * Loads the data for a single attribute using the given id
     *
     * @param attributeClass The class of the attribute
     * @param id The id to load the data using
     * @return The attribute instance
     * @param <X> The attribute type
     */
    <X extends Attribute> CompletableFuture<X> loadAttribute(Class<? extends X> attributeClass, UUID id);

    /**
     *
     * Saves the given attribute
     *
     * @param attribute The attribute to save
     * @param <X> The attribute type
     * @return The future
     */
    <X extends Attribute> CompletableFuture<Void> saveAttribute(X attribute);

    /**
     *
     * Maps the given uuid to the id for the attribute
     *
     * @param attributeClass The class of the attribute
     * @param uuid The id to map
     * @param <T> The attribute type
     * @return The attribute instance
     */
    <T extends Attribute> UUID mapId(Class<T> attributeClass, UUID uuid);

    /**
     *
     * Gets the adapter for the given attribute class
     *
     * @param attributeClass The class of the attribute
     * @return The adapter
     * @param <T> The attribute type
     */
    <T extends Attribute> AttributeAdapter<T> getAdapter(Class<T> attributeClass);

    /**
     *
     * Gets the save mode for the given attribute
     *
     * @param attributeClass The class of the attribute
     * @return The save mode
     */
    String getSaveMode(Class<? extends Attribute> attributeClass);

    /**
     *
     * Sets the save mode for the given attribute
     *
     * @param attributeClass The class of the attribute
     * @param saveMode The save mode
     */
    void overrideSaveMode(Class<? extends Attribute> attributeClass, String saveMode);

    /**
     *
     * Sets the global save mode for all attributes
     *
     * @param saveMode The save mode
     */
    @SuppressWarnings("unchecked")
    default void setGlobalSaveMode(DatabaseDetailsConfig saveMode) {
        this.setGlobalSaveMode(DatabaseDetailsRegistry.getRegistry().getKey((Class<DatabaseDetailsConfig>) saveMode.getClass()));
    }

    /**
     *
     * Sets the global save mode for all attributes as
     * a fallback if {@link #overrideSaveMode(Class, String)} has not been called
     *
     * @param saveMode The save mode
     */
    void setGlobalSaveMode(String saveMode);

    /**
     *
     * Registers an {@link com.envyful.api.player.attribute.PlayerAttribute} class so that when the player object is
     * instantiated it can be created (using reflection) from the registry in the PlayerManager implementation.
     *
     * @param attribute The class of the attribute being registered
     */
    default <X extends Attribute> void registerAttribute(Class<X> attribute, Function<UUID, X> constructor) {
        this.registerAttribute(Attribute.<X, A>builder(attribute).constructor(constructor));
    }

    /**
     *
     * Registers an {@link Attribute} class so that when the player object is
     * instantiated it can be created from the registry.
     *
     * @param builder The builder for the attribute
     * @param <X> The attribute type
     */
    default <X extends Attribute> void registerAttribute(AttributeBuilder<X, A> builder) {
        var data = new AttributeData<>(
                builder.attributeClass(),
                builder.isShared(),
                builder.constructor(),
                builder.idMapper(),
                builder.predicates(),
                builder.triggers(),
                builder.offlineIdMapper(),
                this,
                builder.registeredAdapters()
        );

        this.registerAttribute(data);

        if (builder.overrideSaveMode() != null) {
            this.overrideSaveMode(builder.attributeClass(), builder.overrideSaveMode());
        }
    }

    /**
     *
     * Registers an {@link com.envyful.api.player.attribute.PlayerAttribute} class so that when the player object is
     * instantiated it can be created from the registry.
     *
     * @param attributeData The data for the attribute
     * @param <X> The attribute type
     */
    <X extends Attribute> void registerAttribute(AttributeData<X, A> attributeData);

    /**
     *
     * Creates a new {@link AttributeBuilder} for the given attribute class
     *
     * @param attributeClass The class of the attribute
     * @param <X> The attribute type
     * @return The attribute builder
     */
    default <X extends Attribute> AttributeBuilder<X, A> builder(Class<X> attributeClass) {
        return Attribute.builder(attributeClass);
    }

}
