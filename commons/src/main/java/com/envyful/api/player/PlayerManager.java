package com.envyful.api.player;

import com.envyful.api.player.attribute.manager.AttributeManager;
import com.envyful.api.player.name.NameStore;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.UUID;

/**
 *
 * An interface designed to store local user objects for the plugin using it.
 * Should handle all login and logout logic (reducing boilerplate / duplicate code) allowing for easy caching
 * with minimal code at the implementation level.
 * <br>
 * Check the implementation level module (i.e. forge module) for details on how to create an instance of this interface.
 *
 * @param <A> The generic for the API's player object
 * @param <B> The generic parameter for the platform's player object
 */
public interface PlayerManager<A extends EnvyPlayer<B>, B> extends AttributeManager<A> {

    /**
     *
     * Get the {@link EnvyPlayer} implementation from the platform's player implementation
     * <br>
     * Will return null if the player is not online
     *
     * @param player The platform's player object
     * @return The API's player implementation
     */
    A getPlayer(B player);

    /**
     *
     * Get the {@link EnvyPlayer} implementation from the minecraft player's UUID
     * <br>
     * Will return null if the player is not online
     *
     * @param uuid The minecraft player's UUID
     * @return The API's player implementation
     */
    A getPlayer(UUID uuid);

    /**
     *
     * Gets the {@link EnvyPlayer} implementation from the player's username (if they are online)
     * <br>
     * Will return null if the player is not online
     *
     * @param username The username of the minecraft player
     * @return The API's player implementation
     */
    A getOnlinePlayer(String username);

    /**
     *
     * Gets the {@link EnvyPlayer} implementation from the player's username (if they are online) (case insensitive)
     * <br>
     * Will return null if the player is not online
     *
     * @param username The username of the minecraft player
     * @return The API's player implementation
     */
    A getOnlinePlayerCaseInsensitive(String username);

    /**
     *
     * Gets a {@link List} of all online players in the {@link EnvyPlayer} form
     *
     *
     * @return All online players
     */
    List<A> getOnlinePlayers();

    /**
     *
     * Gets the name store for the player manager
     *
     * @return The name store
     */
    @Nullable
    NameStore getNameStore();

    /**
     *
     * Sets the name store for the player manager
     *
     * @param nameStore The new name store
     */
    void setNameStore(NameStore nameStore);
}
