package com.envyful.api.forge.world;

import com.envyful.api.math.UtilRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 *
 * Static utility class for world methods
 *
 */
public class UtilWorld {

    public static BlockPos getRandomPosition(Level world, int radius) {
        return getRandomPosition(world, radius, radius);
    }

    public static BlockPos getRandomPosition(Level world, int radiusX, int radiusZ) {
        BlockPos pos = new BlockPos(
                (UtilRandom.randomBoolean() ? 1 : -1) * UtilRandom.randomInteger(0, radiusX),
                0,
                (UtilRandom.randomBoolean() ? 1 : -1) * UtilRandom.randomInteger(0, radiusZ));

        int y = world.getChunk(pos).getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
        return new BlockPos(pos.getX(), y, pos.getZ());
    }

    /**
     *
     * Finds a world represented by the given name.
     * Returns null if not found
     *
     * @param name The name of the world to be found
     * @return The world found
     */
    public static Level findWorld(String name) {
        for (ServerLevel world : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
            if (getName(world).equalsIgnoreCase(name)) {
                return world;
            }
        }

        return null;
    }

    /**
     *
     * Obtains the name of the world and abstracts the impl away from the platform.
     *
     * @param world The world
     * @return The name of the world
     */
    public static String getName(Level world) {
        if (!(world instanceof ServerLevel) || !(world.getLevelData() instanceof ServerLevelData)) {
            return "NONE";
        }

        return ((ServerLevelData) world.getLevelData()).getLevelName();
    }
}
