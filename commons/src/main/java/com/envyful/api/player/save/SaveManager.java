package com.envyful.api.player.save;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.attribute.Attribute;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 *
 * Used for handling the saving and loading of attribute data
 *
 * @param <T> The player type
 */
public interface SaveManager<T> {

    /**
     *
     * Registers an attribute for how this class should handle saving and loading
     *
     * @param attribute The class of the attribute being registered
     */
    void registerAttribute(Class<? extends Attribute<?>> attribute);

    /**
     *
     * Saves the player's data from the given attribute
     * This will call {@link Attribute#saveWithGenericId(Object)}} if this class has not been registered using
     * {@link SaveManager#registerAttribute(Class)}
     *
     *
     * @param player The player whose data is being saved
     * @param attribute The attribute being saved
     */
    default void saveData(EnvyPlayer<T> player, Attribute<?> attribute) {
        this.saveData(player.getUuid(), attribute);
    }

    /**
     *
     * Saves the player's data from the given attribute
     *
     * @param uuid The offline UUID
     * @param attribute The attribute being saved
     */
    void saveData(UUID uuid, Attribute<?> attribute);

    /**
     *
     * Load the player's data for all registered {@link Attribute} using
     * {@link SaveManager#registerAttribute(Class)}
     *
     * @param player The player whose data is being loaded
     * @return All successfully loaded attributes
     */
    default CompletableFuture<List<Attribute<?>>> loadData(EnvyPlayer<T> player) {
        return this.loadData(player.getUuid());
    }

    /**
     *
     * Loads the data for a single attribute using the given id
     *
     * @param attributeClass The class of the attribute
     * @param id The id to load the data using
     * @return The attribute instance
     * @param <A> The attribute type
     * @param <B> The id type
     */
    <A extends Attribute<?>, B> CompletableFuture<A> loadAttribute(Class<? extends A> attributeClass, B id);

    /**
     *
     * Load the player's data for all registered {@link Attribute}
     * using {@link SaveManager#registerAttribute(Class)}
     *
     * @param uuid The offline player's UUID
     * @return All successfully loaded attributes
     */
    CompletableFuture<List<Attribute<?>>> loadData(UUID uuid);

}
