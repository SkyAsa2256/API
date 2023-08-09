package com.envyful.api.forge.config;

import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 *
 *
 * Represents a configurable cost
 *
 */
@ConfigSerializable
public interface ConfigCost {

    /**
     *
     * Checks if the given player has the cost
     *
     * @param player The player
     * @return true if they can afford the cost, false if not
     */
    boolean has(ServerPlayerEntity player);

    /**
     *
     * Takes the cost from the given player
     *
     * @param player The player
     */
    void take(ServerPlayerEntity player);

}
