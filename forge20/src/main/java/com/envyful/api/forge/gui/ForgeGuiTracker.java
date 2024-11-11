package com.envyful.api.forge.gui;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.player.EnvyPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * A class to track all open {@link ForgeGui}s and update them every tick (to update any changed items after player clicks)
 *
 */
public class ForgeGuiTracker {

    private static final Map<UUID, ForgeGui> OPEN_GUIS = new ConcurrentHashMap<>();
    private static final Set<UUID> REQUIRED_UPDATE = Collections.newSetFromMap(new ConcurrentHashMap<>());

    static {
        UtilConcurrency.runRepeatingTask(() -> {
            if (ServerLifecycleHooks.getCurrentServer() == null) {
                return;
            }

            for (ForgeGui value : OPEN_GUIS.values()) {
                value.update();
            }
        }, 50L, 50L);
    }

    public static void addGui(EnvyPlayer<?> player, ForgeGui gui) {
        if (player == null) {
            return;
        }

        OPEN_GUIS.put(player.getUniqueId(), gui);
    }

    public static boolean inGui(EnvyPlayer<?> player) {
        return OPEN_GUIS.containsKey(player.getUniqueId());
    }

    public static void removePlayer(EnvyPlayer<?> player) {
        if (player == null) {
            return;
        }

        OPEN_GUIS.remove(player.getUniqueId());
    }

    public static void enqueueUpdate(EnvyPlayer<?> player) {
        if (player == null) {
            return;
        }

        REQUIRED_UPDATE.add(player.getUniqueId());
    }

    public static boolean requiresUpdate(ServerPlayer player) {
        return REQUIRED_UPDATE.contains(player.getUUID());
    }

    public static void dequeueUpdate(ServerPlayer player) {
        REQUIRED_UPDATE.remove(player.getUUID());
    }

}
