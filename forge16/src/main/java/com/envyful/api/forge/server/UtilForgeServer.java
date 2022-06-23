package com.envyful.api.forge.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 *
 * Static utility class for handling forge server function
 *
 */
public class UtilForgeServer {

    private static final MinecraftServer SERVER = ServerLifecycleHooks.getCurrentServer();

    /**
     *
     * Executes the given command from the server
     *
     * Ensure to set the server first
     *
     * @param command The command to execute
     */
    public static void executeCommand(String command) {
        SERVER.getCommands().performCommand(SERVER.createCommandSourceStack(), command);
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
        SERVER.getCommands().performCommand(player.createCommandSourceStack(), command);
    }
}
