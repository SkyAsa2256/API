package com.envyful.api.forge.player.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 *
 * Static utility class for teleporting players
 *
 */
public class UtilTeleport {

    private static final PlayerList PLAYER_LIST = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

    /**
     *
     * Teleports the player to the given position in the world
     *
     * @param player The player
     * @param world The world
     * @param pos The x, y, z coords
     * @param pitch The pitch
     * @param yaw The yaw
     */
    public static void teleportPlayer(EntityPlayerMP player, World world, Vec3d pos, float pitch, float yaw) {
        if (player.getServerWorld().provider.getDimension() != world.provider.getDimension()) {
            PLAYER_LIST.transferPlayerToDimension(player, world.provider.getDimension(), new Teleporter((WorldServer) world));
        }

        player.connection.setPlayerLocation(pos.x, pos.y, pos.z, yaw, pitch);
    }
}
