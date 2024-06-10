package com.envyful.api.forge.player.util;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.EnvyPlayer;
import com.google.common.collect.Maps;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

/**
 *
 * Static utility class for handling getting online and offline players
 *
 */
public class UtilPlayer {

    private static final Map<String, PermissionNode<Boolean>> PERMISSION_NODES = Maps.newConcurrentMap();

    static {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false,
                PermissionGatherEvent.Nodes.class, event -> {
            for (var node : PERMISSION_NODES.values()) {
                event.addNodes(node);
            }
        });
    }

    public static String getName(CommandSource source) {
        if (source instanceof ServerPlayer) {
            return ((ServerPlayer) source).getName().getString();
        }

        return "CONSOLE";
    }

    /**
     *
     * Used for checking if a player has a permisson
     *
     * @param player The player
     * @param permission The permission
     * @return true if they have access to said permission
     * @deprecated Use {@link PlatformProxy#hasPermission(EnvyPlayer, String)} instead
     */
    @Deprecated
    public static boolean hasPermission(ServerPlayer player, String permission) {
        if (player == null || permission == null) {
            return false;
        }

        var permissionNode = PERMISSION_NODES.get(permission);

        if (permissionNode == null) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Unregistered permission node is attempted to be used: {}", permission));
            return false;
        }

        return (PermissionAPI.getPermission(player, permissionNode) || player.hasPermissions(4) || isOP(player));
    }

    /**
     *
     * Registers a permission node with this class
     *
     * @param permissionNode The node
     * @return The permission
     */
    public static PermissionNode<Boolean> registerPermission(String permissionNode) {
        if (!permissionNode.contains(".")) {
            return registerPermission("envyapi", permissionNode);
        }

        String[] split = permissionNode.split("\\.");
        return registerPermission(split[0], String.join(".", Arrays.copyOfRange(split, 1, split.length)));
    }

    /**
     *
     * Registers a permission node with this class
     *
     * @param modId The mod id
     * @param permissionNode The node
     * @return The permission
     */
    public static PermissionNode<Boolean> registerPermission(String modId, String permissionNode) {
        var registeredPermission = new PermissionNode<>(modId, permissionNode,
                PermissionTypes.BOOLEAN,
                (player, uuid, contexts) -> false);

        PERMISSION_NODES.put(modId + "." + permissionNode, registeredPermission);
        return registeredPermission;
    }

    public static PermissionNode<Boolean> getPermission(String permissionNode) {
        return PERMISSION_NODES.get(permissionNode);
    }

    public static boolean isOP(ServerPlayer player) {
        ServerOpListEntry entry = ServerLifecycleHooks.getCurrentServer().getPlayerList().getOps().get(player.getGameProfile());
        return entry != null && entry.getLevel() >= ServerLifecycleHooks.getCurrentServer().getOperatorUserPermissionLevel();
    }

    /**
     *
     * Forces the player to run a command
     *
     * @param player The player running the command
     * @param command The command
     */
    public static void runCommand(ServerPlayer player, String command) {
        ServerLifecycleHooks.getCurrentServer().getCommands().performPrefixedCommand(player.createCommandSourceStack(), command);
    }

    /**
     *
     * Gets the online player with the given name.
     * Returns null if not online
     *
     * @param name The name of the player
     * @return The online player
     */
    public static ServerPlayer findByName(String name) {
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
    public static ServerPlayer getOnlinePlayer(UUID uuid) {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
    }
}
