package com.envyful.api.forge.server;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

/**
 *
 * Static utility class for handling forge server function
 *
 */
public class UtilForgeServer {

    private static MinecraftServer server;

    /**
     *
     * Sets the server to be cached and used later
     *
     * @param server The minecraft server
     */
    public static void setServer(MinecraftServer server) {
        UtilForgeServer.server = server;
    }

    /**
     *
     * Executes the given command from the server
     *
     * Ensure to set the server first
     *
     * @param command The command to execyte
     */
    public static void executeCommand(String command) {
        if (server == null) {
            return;
        }

        server.getCommandManager().executeCommand(server, command);
    }
}
