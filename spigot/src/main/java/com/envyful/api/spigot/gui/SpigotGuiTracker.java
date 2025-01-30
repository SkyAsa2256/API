package com.envyful.api.spigot.gui;

import com.envyful.api.player.EnvyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

/**
 *
 * A class to track all open {@link SpigotGui}s and update them every tick (to update any changed items after player clicks)
 *
 */
public class SpigotGuiTracker {

    private static final Map<UUID, InventoryDetails> OPEN_GUIS = new HashMap<>();
    private static final Set<UUID> REQUIRED_UPDATE = new HashSet<>();

    public static void addGui(EnvyPlayer<?> player, SpigotGui gui, Inventory inventory) {
        if (player == null) {
            return;
        }

        OPEN_GUIS.put(player.getUniqueId(), new InventoryDetails(player.getUniqueId(), inventory, gui));
    }

    public static InventoryDetails getDetails(EnvyPlayer<?> player) {
        return getDetails(player.getUniqueId());
    }

    public static InventoryDetails getDetails(Player player) {
        return getDetails(player.getUniqueId());
    }

    public static InventoryDetails getDetails(UUID uuid) {
        return OPEN_GUIS.get(uuid);
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

    public static boolean requiresUpdate(Player player) {
        return REQUIRED_UPDATE.contains(player.getUniqueId());
    }

    public static void dequeueUpdate(Player player) {
        REQUIRED_UPDATE.remove(player.getUniqueId());
    }

    public static class InventoryDetails {

        private final UUID player;
        private final Inventory inventory;
        private final SpigotGui gui;

        public InventoryDetails(UUID player, Inventory inventory, SpigotGui gui) {
            this.player = player;
            this.inventory = inventory;
            this.gui = gui;
        }

        public UUID getPlayer() {
            return this.player;
        }

        public SpigotGui getGui() {
            return this.gui;
        }

        public Inventory getInventory() {
            return this.inventory;
        }
    }
}
