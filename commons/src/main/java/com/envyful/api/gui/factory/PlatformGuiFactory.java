package com.envyful.api.gui.factory;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.gui.Gui;
import com.envyful.api.gui.close.CloseConsumer;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.gui.pane.TickHandler;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.text.Placeholder;

/**
 *
 * Factory interface for handling creating the platform specific builder implementations
 *
 */
public interface PlatformGuiFactory<T> {

    /**
     *
     * Caches the player manager
     *
     * @param playerManager The player manager
     */
    void setPlayerManager(PlayerManager<?, ?> playerManager);

    /**
     *
     * Gets the platform's player manager
     *
     * @return The player manager
     */
    PlayerManager<?, ?> getPlayerManager();

    /**
     *
     * Gets the platform's {@link Displayable.Builder} class as a new instance
     *
     * @return The new displayable builder for the given platform
     */
    Displayable.Builder<T> displayableBuilder();

    /**
     *
     * Gets the platform's {@link Pane.Builder} class as a new instance
     *
     * @return The pane builder for the given platform
     */
    Pane.Builder paneBuilder();

    /**
     *
     * Creates a GUI using a single pane and {@link ConfigInterface}
     *
     * @param guiSettings The gui settings
     * @param pane The pane
     * @return The gui builder for the given platform
     */
    Gui singlePaneGui(ConfigInterface guiSettings, Pane pane);

    /**
     *
     * Gets the platform's {@link Gui.Builder} class as a new instance
     *
     * @return The gui builder for the given platform
     */
    Gui.Builder guiBuilder();

    /**
     *
     * Creates a tick handler builder instance
     *
     * @return The builder
     */
    TickHandler.Builder tickBuilder();

    /**
     *
     * Gets a close consumer builder instance
     *
     * @return The builder instance
     */
    CloseConsumer.Builder<?, ?> closeConsumerBuilder();

    /**
     *
     * Parses a config item to a {@link Displayable}
     *
     * @param configItem The config item
     * @param placeholders The placeholders to apply
     * @return The displayable
     */
    Displayable convertConfigItem(ConfigItem configItem, Placeholder... placeholders);

    /**
     *
     * Parses a config item to a {@link Displayable}
     *
     * @param configItem The config item
     * @param placeholders The placeholders to apply
     * @return The displayable
     */
    Displayable.Builder<T> convertConfigItemBuilder(ConfigItem configItem, Placeholder... placeholders);

}
