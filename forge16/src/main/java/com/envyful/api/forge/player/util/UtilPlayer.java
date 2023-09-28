package com.envyful.api.forge.player.util;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.OpEntry;
import net.minecraft.util.Util;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.List;
import java.util.UUID;

/**
 *
 * Static utility class for handling getting online and offline players
 *
 */
public class UtilPlayer {

    public static String getName(ICommandSource source) {
        if (source instanceof ServerPlayerEntity) {
            return ((ServerPlayerEntity) source).getName().getString();
        }

        return "CONSOLE";
    }

    public static boolean hasPermission(ServerPlayerEntity player, String permission) {
        if (player == null || permission == null) {
            return false;
        }

        return (PermissionAPI.hasPermission(player, permission) || isOP(player));
    }

    public static boolean isOP(ServerPlayerEntity player) {
        OpEntry entry = ServerLifecycleHooks.getCurrentServer().getPlayerList().getOps().get(player.getGameProfile());
        return entry != null;
    }

    /**
     *
     * Forces the player to run a command
     *
     * @param player The player running the command
     * @param command The command
     */
    public static void runCommand(ServerPlayerEntity player, String command) {
        ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(player.createCommandSourceStack(), command);
    }

    /**
     *
     * Gets the online player with the given name.
     * Returns null if not online
     *
     * @param name The name of the player
     * @return The online player
     */
    public static ServerPlayerEntity findByName(String name) {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(name);
    }

    /**
     *
     * Gets the online player with the given {@link UUID}.
     * Returns null if not online
     *
     * @param uuid The uuid of the player
     * @return The online player
     */
    public static ServerPlayerEntity getOnlinePlayer(UUID uuid) {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
    }

    /**
     *
     * Formats the message with the provided placeholders via the {@link PlaceholderFactory}
     * then sends them to the player
     *
     * @param player The player
     * @param message The message to send
     * @param placeholders The placeholders to use
     */
    public static void sendMessage(ServerPlayerEntity player, List<String> message, Placeholder... placeholders) {
        message = PlaceholderFactory.handlePlaceholders(message, placeholders);

        for (String s : message) {
            player.sendMessage(UtilChatColour.colour(s), Util.NIL_UUID);
        }
    }
}
