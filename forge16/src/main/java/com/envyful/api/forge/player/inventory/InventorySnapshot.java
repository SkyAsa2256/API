package com.envyful.api.forge.player.inventory;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InventorySnapshot {

    private final ItemStack[] items;
    private final ItemStack[] armor;
    private final ItemStack[] offhand;

    private InventorySnapshot(ItemStack[] items, ItemStack[] armor, ItemStack[] offhand) {
        this.items = items;
        this.armor = armor;
        this.offhand = offhand;
    }

    public static InventorySnapshot of(ServerPlayerEntity player) {
        ItemStack[] items = player.inventory.items.toArray(new ItemStack[0]);
        ItemStack[] armor = player.inventory.armor.toArray(new ItemStack[0]);
        ItemStack[] offhand = player.inventory.offhand.toArray(new ItemStack[0]);

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

    public void restore(ServerPlayerEntity player) {
        restore(player.inventory.items, this.items);
        restore(player.inventory.offhand, this.offhand);
        restore(player.inventory.armor, this.armor);
    }

    private void restore(NonNullList<ItemStack> list, ItemStack[] array) {
        for (int i = 0; i < array.length; i++) {
            list.set(i, array[i]);
        }
    }
}
