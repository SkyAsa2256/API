package com.envyful.api.forge.gui.factory;

import com.envyful.api.forge.gui.ForgeGui;
import com.envyful.api.forge.gui.item.ForgeStaticDisplayable;
import com.envyful.api.forge.gui.pane.ForgeStaticPane;
import com.envyful.api.gui.Gui;
import com.envyful.api.gui.factory.PlatformGuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.gui.pane.type.PagedPane;
import net.minecraft.item.ItemStack;

/**
 *
 * Forge implementation of the {@link PlatformGuiFactory} interface
 *
 */
public class ForgeGuiFactory implements PlatformGuiFactory<ItemStack> {

    @Override
    public Displayable.Builder<ItemStack> displayableBuilder() {
        return new ForgeStaticDisplayable.Builder();
    }

    @Override
    public Pane.Builder paneBuilder() {
        return new ForgeStaticPane.Builder();
    }

    @Override
    public PagedPane.Builder pagedPaneBuilder() {
        return null; //TODO: not made yet
    }

    @Override
    public Gui.Builder guiBuilder() {
        return new ForgeGui.Builder();
    }
}
