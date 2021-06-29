package com.envyful.api.gui;

import com.envyful.api.player.EnvyPlayer;

/**
 *
 * An interface representing chest GUIs for the platform specific implementation
 *
 */
public interface Gui {

    /**
     *
     * Opens the GUI for the given player
     *
     * @param player The player to open the GUI for
     */
    void open(EnvyPlayer<?> player);

}
