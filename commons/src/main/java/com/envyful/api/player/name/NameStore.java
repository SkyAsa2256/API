package com.envyful.api.player.name;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 *
 * A representation of a store for player names to UUIDs
 *
 */
public interface NameStore {

    /**
     *
     * Gets the name of the player with the given UUID
     *
     * @param uuid The uuid of the player
     * @return The name of the player
     */
    CompletableFuture<String> getName(UUID uuid);

    /**
     *
     * Gets the UUID of the player with the given name
     *
     * @param name The name of the player
     * @return The UUID of the player
     */
    CompletableFuture<UUID> getUUID(String name);

    /**
     *
     * Updates the stored name for the given UUID
     *
     * @param uuid The uuid of the player
     * @param name The new name of the player
     */
    void updateStored(UUID uuid, String name);

}
