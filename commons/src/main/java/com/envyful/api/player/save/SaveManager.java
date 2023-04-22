package com.envyful.api.player.save;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.attribute.Attribute;

import java.util.List;
import java.util.UUID;

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
     * @param manager The manager object
     * @param attribute The class of the attribute being registered
     */
    void registerAttribute(Object manager, Class<? extends Attribute<?, ?>> attribute);

    /**
     *
     * Saves the player's data from the given attribute
     * This will call {@link Attribute#saveWithGenericId(Object)}} if this class has not been registered using
     * {@link SaveManager#registerAttribute(Object, Class)}
     *
     *
     * @param player The player whose data is being saved
     * @param attribute The attribute being saved
     */
    default void saveData(EnvyPlayer<T> player, Attribute<?, ?> attribute) {
        this.saveData(player.getUuid(), attribute);
    }

    /**
     *
     * Saves the player's data from the given attribute
     *
     * @param uuid The offline UUID
     * @param attribute The attribute being saved
     */
    void saveData(UUID uuid, Attribute<?, ?> attribute);

    /**
     *
     * Load the player's data for all registered {@link Attribute} using
     *{@link SaveManager#registerAttribute(Object, Class)}
     *
     * @param player The player whose data is being loaded
     * @return All successfully loaded attributes
     */
    default List<Attribute<?, ?>> loadData(EnvyPlayer<T> player) {
        return this.loadData(player.getUuid());
    }

    /**
     *
     * Load the player's data for all registered {@link Attribute} using {@link SaveManager#registerAttribute(Object, Class)}
     *
     * @param uuid The offline player's UUID
     * @return All successfully loaded attributes
     */
    List<Attribute<?, ?>> loadData(UUID uuid);

}
