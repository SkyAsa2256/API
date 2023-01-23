package com.envyful.api.forge.player.util;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.google.common.collect.Sets;
import io.netty.util.AttributeKey;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.OpEntry;
import net.minecraftforge.fml.network.FMLConnectionData;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.PermissionAPI;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 *
 * Static utility class for handling getting online & offline players
 *
 */
public class UtilPlayer {

    private static AttributeKey<FMLConnectionData> ATTRIBUTE;

    static {
        try {
            Field connectionData = FMLNetworkConstants.class.getDeclaredField("FML_CONNECTION_DATA");
            ATTRIBUTE = (AttributeKey<FMLConnectionData>) connectionData.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static String getName(ICommandSource source) {
        if (source instanceof ServerPlayerEntity) {
            return ((ServerPlayerEntity) source).getName().getString();
        }

        return "CONSOLE";
    }

    public static boolean hasPermission(ServerPlayerEntity player, String permission) {
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
     * Gets the player's client side mods
     *
     * @param player The player
     * @return The mod names/ids
     */
    public static Set<String> getMods(ForgeEnvyPlayer player) {
        if (player == null) {
            return Collections.emptySet();
        }

        return getMods(player.getParent());
    }

    /**
     *
     * Gets the player's client side mods
     *
     * @param player The player
     * @return The mod names/ids
     */
    public static Set<String> getMods(ServerPlayerEntity player) {
        if (player == null || player.connection == null || player.connection.connection == null ||
                player.connection.connection.channel() == null) {
            return Collections.emptySet();
        }

        FMLConnectionData networkDispatcher = player.connection.connection.channel().attr(ATTRIBUTE).get();
        return Sets.newHashSet(networkDispatcher.getModList());
    }
}
