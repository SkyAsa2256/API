package com.envyful.api.spigot.player.util;

import com.envyful.api.spigot.player.SpigotEnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 *
 * Static utility class for handling getting online and offline players
 *
 */
public class UtilPlayer {

    public static String getName(CommandSender source) {
        return source.getName();
    }

    public static boolean hasPermission(CommandSender player, String permission) {
        return player.hasPermission(permission);
    }

    public static boolean isOP(CommandSender player) {
        return player.isOp();
    }

    /**
     *
     * Forces the player to run a command
     *
     * @param player The player running the command
     * @param command The command
     */
    public static void runCommand(CommandSender player, String command) {
        Bukkit.getServer().dispatchCommand(player, command);
    }

    /**
     *
     * Gets the online player with the given name.
     * Returns null if not online
     *
     * @param name The name of the player
     * @return The online player
     */
    public static Player findByName(String name) {
        return Bukkit.getPlayer(name);
    }

    /**
     *
     * Gets the online player with the given {@link UUID}.
     * Returns null if not online
     *
     * @param uuid The uuid of the player
     * @return The online player
     */
    public static Player getOnlinePlayer(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * Formats the message with the provided placeholders via the {@link PlaceholderFactory}
     * then sends them to the player
     *
     * @param player       The player
     * @param message      The message to send
     * @param placeholders The placeholders to use
     */
    public static void sendMessage(SpigotEnvyPlayer player, List<String> message, Placeholder... placeholders) {
        sendMessage(player.getParent(), message, placeholders);
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
    public static void sendMessage(CommandSender player, List<String> message, Placeholder... placeholders) {
        message = PlaceholderFactory.handlePlaceholders(message, placeholders);

        for (String s : message) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(s));
        }
    }
}
