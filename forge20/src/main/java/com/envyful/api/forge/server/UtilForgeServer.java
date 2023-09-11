package com.envyful.api.forge.server;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * Static utility class for handling forge server function
 *
 */
public class UtilForgeServer {

    /**
     *
     * Executes the given command from the server
     *
     * Ensure to set the server first
     *
     * @param command The command to execute
     */
    public static void executeCommand(String command) {
        ServerLifecycleHooks.getCurrentServer().getCommands().performPrefixedCommand(ServerLifecycleHooks.getCurrentServer().createCommandSourceStack(), command);
    }


    /**
     *
     * Executes the given command from the given player
     *
     * Ensure to set the server first
     *
     * @param player THe player to execute the command as
     * @param command The command to execute
     */
    public static void executeCommand(ServerPlayer player, String command) {
        ServerLifecycleHooks.getCurrentServer().getCommands().performPrefixedCommand(player.createCommandSourceStack(), command);
    }

    /**
     *
     * Broadcast the messages to all players online
     *
     * @param messages The messages
     */
    public static void broadcast(String... messages) {
        broadcast(Lists.newArrayList(messages));
    }

    /**
     *
     * Broadcast the messages to all players online
     *
     * @param messages The messages
     * @param placeholders Placeholders
     */
    public static void broadcast(Collection<String> messages, Placeholder... placeholders) {
        for (String message : messages) {
            List<String> parsedMessage = PlaceholderFactory.handlePlaceholders(Collections.singletonList(message), placeholders);

            for (String parsed : parsedMessage) {
                ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastSystemMessage(
                        UtilChatColour.colour(parsed), true
                );
            }
        }
    }

    /**
     *
     * Broadcast the messages to all players online
     *
     * @param messages The messages
     */
    public static void formattedBroadcast(Component... messages) {
        formattedBroadcast(Lists.newArrayList(messages));
    }

    /**
     *
     * Broadcast the messages to all players online
     *
     * @param messages The messages
     */
    public static void formattedBroadcast(Collection<Component> messages) {
        for (Component message : messages) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastSystemMessage(
                    message, true
            );
        }
    }
}
