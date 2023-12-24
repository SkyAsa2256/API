package com.envyful.api.platform;

import com.envyful.api.text.Placeholder;

import java.util.Collection;

/**
 *
 * An interface designed to abstract the platform specific code from the rest of the plugin/mod
 * <br>
 * This prevents breaking changes from Mojang and other platforms from affecting the rest of the code
 * and allowing for the plugin/mod to be ported to other platforms with minimal changes and protected
 * from breaking changes between versions
 *
 * @param <A> The player type
 */
public interface PlatformHandler<A> {

    /**
     *
     * Broadcast a message to all players on the server with placeholders
     *
     * @param message The messages to broadcast
     * @param placeholders The placeholders to replace
     */
    void broadcastMessage(Collection<String> message, Placeholder... placeholders);

    /**
     *
     * Send a message to a player with placeholders
     *
     * @param player The player to send the message to
     * @param message The message to send
     * @param placeholders The placeholders to replace
     */
    void sendMessage(A player, Collection<String> message, Placeholder... placeholders);

    /**
     *
     * Execute a runnable on the main server's thread
     *
     * @param runnable The runnable to execute
     */
    void runSync(Runnable runnable);

    /**
     *
     * Execute a runnable on the main server's thread after a delay
     *
     * @param runnable The runnable to execute
     * @param delayTicks The delay in ticks
     */
    void runLater(Runnable runnable, int delayTicks);

    /**
     *
     * Gets the current TPS of the server
     *
     * @return The current TPS
     */
    double getTPS();

}
