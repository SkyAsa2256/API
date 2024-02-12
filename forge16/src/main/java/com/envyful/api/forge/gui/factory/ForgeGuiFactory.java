package com.envyful.api.forge.gui.factory;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.forge.config.UtilConfigInterface;
import com.envyful.api.forge.gui.ForgeGuiBuilder;
import com.envyful.api.forge.gui.close.ForgeCloseConsumer;
import com.envyful.api.forge.gui.item.ForgeSimpleDisplayable;
import com.envyful.api.forge.gui.pane.ForgeSimplePane;
import com.envyful.api.forge.gui.ticker.ForgeGuiTickHandler;
import com.envyful.api.gui.Gui;
import com.envyful.api.gui.close.CloseConsumer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.factory.PlatformGuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.gui.pane.TickHandler;
import com.envyful.api.gui.pane.type.PagedPane;
import com.envyful.api.player.PlayerManager;
import net.minecraft.item.ItemStack;

/**
 *
 * Forge implementation of the {@link PlatformGuiFactory} interface
 *
 */
public class ForgeGuiFactory implements PlatformGuiFactory<ItemStack> {

    private PlayerManager<?, ?> playerManager;

    @Override
    public Pane createPane(ConfigInterface guiSettings) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(guiSettings.getHeight())
                .build();

        UtilConfigInterface.fillBackground(pane, guiSettings);
        return pane;
    }

    @Override
    public void setPlayerManager(PlayerManager<?, ?> playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public Displayable.Builder<ItemStack> displayableBuilder() {
        return new ForgeSimpleDisplayable.Builder();
    }

    @Override
    public Pane.Builder paneBuilder() {
        return new ForgeSimplePane.Builder();
    }

    @Override
    public PagedPane.Builder pagedPaneBuilder() {
        return null; //TODO: not made yet
    }

    @Override
    public Gui singlePaneGui(ConfigInterface guiSettings, Pane pane) {
        return GuiFactory.guiBuilder()
                .setPlayerManager(this.playerManager)
                .addPane(pane)
                .height(guiSettings.getHeight())
                .title(guiSettings.getTitle())
                .build();
    }

    @Override
    public Gui.Builder guiBuilder() {
        return new ForgeGuiBuilder();
    }

    @Override
    public TickHandler.Builder tickBuilder() {
        return new ForgeGuiTickHandler.Builder();
    }

    @Override
    public CloseConsumer.Builder<?, ?> closeConsumerBuilder() {
        return new ForgeCloseConsumer.Builder();
    }
}
