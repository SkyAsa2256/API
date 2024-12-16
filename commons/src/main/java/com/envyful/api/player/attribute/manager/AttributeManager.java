package com.envyful.api.player.attribute.manager;

import com.envyful.api.player.Attribute;
import com.envyful.api.player.AttributeBuilder;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.AttributeHolder;
import com.envyful.api.player.attribute.data.AttributeData;
import com.envyful.api.player.save.SaveManager;

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
     * Registers an {@link com.envyful.api.player.attribute.PlayerAttribute} class so that when the player object is
     * instantiated it can be created (using reflection) from the registry in the PlayerManager implementation.
     * <br>
     * If {@link PlayerManager#setSaveManager(SaveManager)} has been called then it will call
     * {@link SaveManager#registerAttribute(AttributeData)}  on the given class
     *
     * @param attribute The class of the attribute being registered
     */
    default <X extends Attribute> void registerAttribute(Class<X> attribute, Function<UUID, X> constructor) {
        this.registerAttribute(Attribute.<X, A>builder(attribute).constructor(constructor));
    }

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

    /**
     *
     * Registers an {@link com.envyful.api.player.attribute.PlayerAttribute} class so that when the player object is
     * instantiated it can be created (using reflection) from the registry in the PlayerManager implementation.
     * <br>
     * If {@link PlayerManager#setSaveManager(SaveManager)} has been called then it will call
     * {@link SaveManager#registerAttribute(AttributeData)} on the given class
     *
     * @param builder The builder for the attribute
     * @param <X> The attribute type
     */
    <X extends Attribute> void registerAttribute(AttributeBuilder<X, A> builder);

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
     * Registers an {@link com.envyful.api.player.attribute.PlayerAttribute} class so that when the player object is
     * instantiated it can be created (using reflection) from the registry in the PlayerManager implementation.
     * <br>
     * If {@link PlayerManager#setSaveManager(SaveManager)} has been called then it will call
     * {@link SaveManager#registerAttribute(AttributeData)}  on the given class
     *
     * @param attributeData The data for the attribute
     * @param <X> The attribute type
     */
    <X extends Attribute> void registerAttribute(AttributeData<X, A> attributeData);

}
