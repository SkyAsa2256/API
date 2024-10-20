package com.envyful.api.spigot.player.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 *
 * Class for storing a snapshot of a player's inventory
 *
 */
public class InventorySnapshot {

    private final ItemStack[] items;

    private InventorySnapshot(ItemStack[] items) {
        this.items = items;
    }

    public static InventorySnapshot of(Player player) {
        ItemStack[] contents = player.getInventory().getContents();

        if (contents == null) {
            return new InventorySnapshot(null);
        }

        return new InventorySnapshot(Arrays.copyOf(contents, contents.length));
    }

    public ItemStack[] getItems() {
        return this.items;
    }

    public void restore(Player player) {
        player.getInventory().setContents(this.items);
    }
}
