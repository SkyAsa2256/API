package com.envyful.api.forge.gui.item;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 *
 * Class to represent an empty slot in a GUI so that minecraft / forge / sponge won't throw an NPE
 *
 */
public class EmptySlot extends Slot {

    public EmptySlot(int index) {
        super(null, index, 0, 0);
    }

    @Override
    public ItemStack getStack() {
        return ItemStack.EMPTY;
    }
}
