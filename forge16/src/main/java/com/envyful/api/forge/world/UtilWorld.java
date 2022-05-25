package com.envyful.api.forge.world;

import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ServerWorldInfo;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 *
 * Static utility class for world methods
 *
 */
public class UtilWorld {

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
