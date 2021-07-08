package com.envyful.api.forge.gui.item;

import com.envyful.api.forge.gui.pane.ForgeStaticPane;
import net.minecraft.item.ItemStack;

/**
 *
 * Class to represent an empty slot in a GUI so that minecraft / forge / sponge won't throw an NPE
 *
 */
public class EmptySlot extends ForgeStaticPane.ForgeStaticPaneDisplayable {

    public EmptySlot(ForgeStaticPane pane, int index) {
        super(pane, new ForgeStaticDisplayable(ItemStack.EMPTY, (envyPlayer, clickType) -> {}, envyPlayer -> {}),
                0, 0);
    }

    @Override
    public ItemStack getStack() {
        return ItemStack.EMPTY;
    }
}
