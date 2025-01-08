package com.envyful.api.gui.factory;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.gui.Gui;
import com.envyful.api.gui.close.CloseConsumer;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.gui.pane.TickHandler;
import com.envyful.api.text.Placeholder;

/**
 *
 * A static proxy class for an easy way to get new builder instances
 *
 */
@SuppressWarnings("unchecked")
public class GuiFactory {

    private static PlatformGuiFactory<?> platformFactory = null;
    private static CloseConsumer<?, ?> empty = null;

    private GuiFactory() {
        throw new UnsupportedOperationException("Static factory");
    }


    /**
     *
     * Gets the platform factory instance
     *
     * @return The platform factory
     */
    public static PlatformGuiFactory<?> getPlatformFactory() {
        return platformFactory;
    }

    /**
     *
     * Sets the platform factory instance (to be done on startup)
     *
     * @param platformFactory The platform factory instance
     */
    public static void setPlatformFactory(
            PlatformGuiFactory<?> platformFactory) {
        GuiFactory.platformFactory = platformFactory;
    }

    /**
     *
     * Creates a pane from a given config interface
     *
     * @param guiSettings The settings for the pane
     * @deprecated Use {@link ConfigInterface#toPane(Placeholder...)} instead
     * @return The new pane
     */
    @Deprecated(forRemoval = true)
    public static Pane createPane(ConfigInterface guiSettings) {
        return guiSettings.toPane();
    }

    /**
     *
     * Gets a new instance of the playform's displayable with the given item
     *
     * @param t The item provided
     * @param <T> The type for the displayable
     * @return The displayable
     */
    public static <T> Displayable displayable(T t) {
        checkThenThrowSetupException();
        return ((Displayable.Builder<T>) platformFactory.displayableBuilder())
                .itemStack(t).build();
    }

    /**
     *
     * Gets a new instance of the playform's displayable
     * builder with the given item
     *
     * @param t The item provided
     * @param <T> The type for the displayable
     * @return The builder
     */
    public static <T> Displayable.Builder<T> displayableBuilder(T t) {
        checkThenThrowSetupException();
        return ((Displayable.Builder<T>) platformFactory.displayableBuilder())
                .itemStack(t);
    }

    /**
     *
     * Gets a new instance of the platform's displayable builder
     *
     * @param unused Used for automatic type detection
     * @return The new displayable builder
     * @param <T> The type for the displayable
     */
    @SuppressWarnings({"unchecked", "unused"})
    public static <T> Displayable.Builder<T> displayableBuilder(
            Class<T> unused) {
        checkThenThrowSetupException();
        return (Displayable.Builder<T>) platformFactory.displayableBuilder();
    }

    /**
     *
     * Gets a new instance of the platform's pane builder
     *
     * @return The new pane builder
     */
    public static Pane.Builder paneBuilder() {
        checkThenThrowSetupException();
        return platformFactory.paneBuilder();
    }

    /**
     *
     * Creates a GUI using a single pane and {@link ConfigInterface}
     *
     * @param guiSettings The gui settings
     * @param pane The pane
     * @return The gui builder for the given platform
     */
    public static Gui singlePaneGui(ConfigInterface guiSettings, Pane pane) {
        checkThenThrowSetupException();
        return platformFactory.singlePaneGui(guiSettings, pane);
    }

    /**
     *
     * Gets a new instance of the platform's GUI builder
     *
     * @return The new GUI builder
     */
    public static Gui.Builder guiBuilder() {
        checkThenThrowSetupException();
        return platformFactory.guiBuilder();
    }

    /**
     *
     * Creates a tick handler builder instance
     *
     * @return The builder
     */
    public static TickHandler.Builder tickBuilder() {
        checkThenThrowSetupException();
        return platformFactory.tickBuilder();
    }

    /**
     *
     * Gets a close consumer builder instance
     *
     * @return The builder instance
     */
    public static CloseConsumer.Builder<?, ?> closeConsumerBuilder() {
        checkThenThrowSetupException();
        return platformFactory.closeConsumerBuilder();
    }

    /**
     *
     * Gets a cached empty instance
     *
     * @return The empty instance
     */
    public static CloseConsumer<?, ?> empty() {
        checkThenThrowSetupException();

        if (empty == null) {
            empty = closeConsumerBuilder().build();
        }

        return empty;
    }

    /**
     *
     * Parses a config item to a {@link Displayable}
     *
     * @param configItem The config item
     * @param placeholders The placeholders to apply
     * @return The displayable
     */
    public static Displayable convertConfigItem(ConfigItem configItem, Placeholder... placeholders) {
        checkThenThrowSetupException();
        return platformFactory.convertConfigItem(configItem, placeholders);
    }

    /**
     *
     * Parses a config item to a {@link Displayable}
     *
     * @param configItem The config item
     * @param placeholders The placeholders to apply
     * @return The displayable
     */
    public static <T> Displayable.Builder<T> convertConfigItemBuilder(ConfigItem configItem, Placeholder... placeholders) {
        checkThenThrowSetupException();
        return (Displayable.Builder<T>) platformFactory.convertConfigItemBuilder(configItem, placeholders);
    }

    private static void checkThenThrowSetupException() {
        if (platformFactory != null) {
            return;
        }

        throw new UnsupportedOperationException(
                "Platform's factory hasn't been set yet!"
        );
    }
}
