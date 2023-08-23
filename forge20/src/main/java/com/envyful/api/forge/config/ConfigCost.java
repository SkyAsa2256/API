package com.envyful.api.forge.config;

import com.envyful.api.text.Placeholder;
import net.minecraft.server.level.ServerPlayer;
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
    boolean has(ServerPlayer player);

    /**
     *
     * Takes the cost from the given player
     *
     * @param player The player
     */
    void take(ServerPlayer player, Placeholder... placeholders);

    String getFailureMessage();

}
