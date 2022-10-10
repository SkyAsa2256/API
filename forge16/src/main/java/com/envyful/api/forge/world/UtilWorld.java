package com.envyful.api.forge.world;

import com.envyful.api.math.UtilRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ServerWorldInfo;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 *
 * Static utility class for world methods
 *
 */
public class UtilWorld {

    public static BlockPos getRandomPosition(World world, int radius) {
        return getRandomPosition(world, radius, radius);
    }

    public static BlockPos getRandomPosition(World world, int radiusX, int radiusZ) {
        BlockPos pos = new BlockPos(
                (UtilRandom.randomBoolean() ? 1 : -1) * UtilRandom.randomInteger(0, radiusX),
                0,
                (UtilRandom.randomBoolean() ? 1 : -1) * UtilRandom.randomInteger(0, radiusZ));

        int y = world.getChunk(pos).getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
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
    public static World findWorld(String name) {
        for (ServerWorld world : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
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
    public static String getName(World world) {
        if (!(world instanceof ServerWorld) || !(world.getLevelData() instanceof ServerWorldInfo)) {
            return "NONE";
        }

        return ((ServerWorldInfo) world.getLevelData()).getLevelName();
    }
}
