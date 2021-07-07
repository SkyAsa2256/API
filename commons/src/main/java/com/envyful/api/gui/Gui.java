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

    /**
     *
     * Gui builder interface
     *
     */
    interface Builder {

        /**
         *
         * Sets the title of the GUI
         *
         * @param title The title of the GUI
         * @return The builder
         */
        Builder title(String title);

        /**
         *
         * Sets the height of the GUI
         *
         * @param height The height of the GUI
         * @return The builder
         */
        Builder height(int height);

        /**
         *
         * Builds the GUI from the given specifications
         *
         * @return The new GUI
         */
        Gui build();

    }
}
