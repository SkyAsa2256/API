package com.envyful.api.gui.factory;

import com.envyful.api.gui.Gui;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.gui.pane.type.PagedPane;

/**
 *
 * A static proxy class for an easy way to get new builder instances
 *
 */
public class GuiFactory {

    private static PlatformGuiFactory platformFactory = null;

    /**
     *
     * Sets the platform factory instance (to be done on startup)
     *
     * @param platformFactory The platform factory instance
     */
    public static void setPlatformFactory(PlatformGuiFactory platformFactory) {
        GuiFactory.platformFactory = platformFactory;
    }

    /**
     *
     * Gets a new instance of the platform's pane builder
     *
     * @return The new pane builder
     */
    public static Pane.Builder paneBuilder() {
        if (platformFactory == null) {
            throw new RuntimeException("Platform's factory hasn't been set yet!");
        }

        return platformFactory.paneBuilder();
    }

    /**
     *
     * Gets a new instance of the platform's paged pane builder
     *
     * @return The new paged pane builder
     */
    public static PagedPane.Builder pagedPaneBuilder() {
        if (platformFactory == null) {
            throw new RuntimeException("Platform's factory hasn't been set yet!");
        }

        return platformFactory.pagedPaneBuilder();
    }

    /**
     *
     * Gets a new instance of the platform's GUI builder
     *
     * @return The new GUI builder
     */
    public static Gui.Builder guiBuilder() {
        if (platformFactory == null) {
            throw new RuntimeException("Platform's factory hasn't been set yet!");
        }

        return platformFactory.guiBuilder();
    }
}
