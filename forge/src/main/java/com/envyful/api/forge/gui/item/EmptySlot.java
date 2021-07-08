package com.envyful.api.forge.gui.item;

import com.envyful.api.forge.gui.pane.ForgeSimplePane;
import net.minecraft.item.ItemStack;

/**
 *
 * Class to represent an empty slot in a GUI so that minecraft / forge / sponge won't throw an NPE
 *
 */
public class EmptySlot extends ForgeSimplePane.SimpleDisplayableSlot {

    public EmptySlot(ForgeSimplePane pane, int index) {
        super(pane, new ForgeSimpleDisplayable(ItemStack.EMPTY, (envyPlayer, clickType) -> {}, envyPlayer -> {}),
                0, 0);
    }

    @Override
    public ItemStack getStack() {
        return ItemStack.EMPTY;
    }
}
