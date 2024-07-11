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
        ItemStack[] items = createArray(player.inventory.items);
        ItemStack[] armor = createArray(player.inventory.armor);
        ItemStack[] offhand = createArray(player.inventory.offhand);

        return new InventorySnapshot(items, armor, offhand);
    }

    private static ItemStack[] createArray(NonNullList<ItemStack> list) {
        ItemStack[] array = new ItemStack[list.size()];

        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i).copy();
        }

        return array;
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
