package com.envyful.api.forge.server;

import com.envyful.api.forge.chat.UtilChatColour;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Collection;

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
        if (!ServerLifecycleHooks.getCurrentServer().isSameThread()) {
            ServerLifecycleHooks.getCurrentServer().execute(() -> executeCommand(command));
            return;
        }

        ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(ServerLifecycleHooks.getCurrentServer().createCommandSourceStack(), command);
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
    public static void executeCommand(ServerPlayerEntity player, String command) {
        ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(player.createCommandSourceStack(), command);
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
     */
    public static void broadcast(Collection<String> messages) {
        for (String message : messages) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(
                    UtilChatColour.colour(message),
                    ChatType.CHAT,
                    Util.NIL_UUID
            );
        }
    }

    /**
     *
     * Broadcast the messages to all players online
     *
     * @param messages The messages
     */
    public static void formattedBroadcast(ITextComponent... messages) {
        formattedBroadcast(Lists.newArrayList(messages));
    }

    /**
     *
     * Broadcast the messages to all players online
     *
     * @param messages The messages
     */
    public static void formattedBroadcast(Collection<ITextComponent> messages) {
        for (ITextComponent message : messages) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(
                    message,
                    ChatType.CHAT,
                    Util.NIL_UUID
            );
        }
    }
}
