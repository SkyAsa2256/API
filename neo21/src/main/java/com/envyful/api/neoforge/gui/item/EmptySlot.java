package com.envyful.api.neoforge.gui.item;

import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.neoforge.gui.pane.ForgeSimplePane;
import net.minecraft.world.item.ItemStack;

/**
 *
 * Class to represent an empty slot in a GUI so that minecraft / forge / sponge won't throw an NPE
 *
 */
public class EmptySlot extends ForgeSimplePane.SimpleDisplayableSlot {

    public EmptySlot(ForgeSimplePane pane, int index) {
        super(pane, GuiFactory.displayable(ItemStack.EMPTY), 0, 0);
    }

    @Override
    public ItemStack getItem() {
        return ItemStack.EMPTY;
    }
}
