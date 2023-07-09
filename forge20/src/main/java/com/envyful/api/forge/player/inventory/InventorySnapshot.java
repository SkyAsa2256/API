package com.envyful.api.forge.player.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class InventorySnapshot {

    private final ItemStack[] items;
    private final ItemStack[] armor;
    private final ItemStack[] offhand;

    private InventorySnapshot(ItemStack[] items, ItemStack[] armor, ItemStack[] offhand) {
        this.items = items;
        this.armor = armor;
        this.offhand = offhand;
    }

    public static InventorySnapshot of(ServerPlayer player) {
        ItemStack[] items = player.getInventory().items.toArray(new ItemStack[0]);
        ItemStack[] armor = player.getInventory().armor.toArray(new ItemStack[0]);
        ItemStack[] offhand = player.getInventory().offhand.toArray(new ItemStack[0]);

        return new InventorySnapshot(items, armor, offhand);
    }

    public ItemStack[] getItems() {
        return this.items;
    }

    public ItemStack[] getArmor() {
        return this.armor;
    }

    public ItemStack[] getOffhand() {
        return this.offhand;
    }

    public void restore(ServerPlayer player) {
        restore(player.getInventory().items, this.items);
        restore(player.getInventory().offhand, this.offhand);
        restore(player.getInventory().armor, this.armor);
    }

    private void restore(NonNullList<ItemStack> list, ItemStack[] array) {
        for (int i = 0; i < array.length; i++) {
            list.set(i, array[i]);
        }
    }
}
