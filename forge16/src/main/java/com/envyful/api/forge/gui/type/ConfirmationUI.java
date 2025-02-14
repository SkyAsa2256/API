package com.envyful.api.forge.gui.type;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.gui.item.PositionableItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.text.Placeholder;
import net.minecraft.item.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

/**
 *
 * A utility class for building generic Confirmation UIs
 *
 */
public class ConfirmationUI {

    /**
     *
     * Opens the UI using the builder as the settings
     *
     * @param builder The UI settings
     */
    private static void open(Builder builder) {
        var config = builder.confirmConfig;
        var placeholders = builder.placeholders.toArray(new Placeholder[0]);
        var pane = config.getGuiSettings().toPane(placeholders);

        UtilConfigItem.builder()
                .clickHandler(builder.confirmHandler)
                .extendedConfigItem((ForgeEnvyPlayer) builder.player, pane, config.getAcceptItem(), placeholders);

        UtilConfigItem.builder()
                .clickHandler(builder.returnHandler)
                .extendedConfigItem((ForgeEnvyPlayer) builder.player, pane, config.getDeclineItem(), placeholders);

        if (builder.descriptionItem != null) {
            pane.set(config.getDescriptionPosition() % 9, config.getDescriptionPosition() / 9,
                     GuiFactory.displayable(builder.descriptionItem)
            );
        }

        for (ExtendedConfigItem displayItem : builder.displayConfigItems) {
            UtilConfigItem.builder().extendedConfigItem((ForgeEnvyPlayer) builder.player, pane, displayItem, placeholders);
        }

        for (PositionableItem displayItem : builder.displayItems) {
            pane.set(displayItem.getPosX(), displayItem.getPosY(), GuiFactory.displayable(displayItem.getItemStack()));
        }

        GuiFactory.guiBuilder()
                .addPane(pane)
                .height(config.getGuiSettings().getHeight())
                .title(PlatformProxy.parse(config.getGuiSettings().getTitle(), placeholders).get(0))
                .build().open(builder.player);
    }

    /**
     *
     * Gets a new instance of the Builder class
     *
     * @return The builder class
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     *
     * For building the settings to the confirmation UI
     *
     */
    public static class Builder {

        private EnvyPlayer<?> player;
        private ItemStack descriptionItem;
        private ConfirmConfig confirmConfig;
        private PlayerManager<?, ?> playerManager;
        private BiConsumer<EnvyPlayer<?>, Displayable.ClickType> returnHandler;
        private BiConsumer<EnvyPlayer<?>, Displayable.ClickType> confirmHandler;
        private List<ExtendedConfigItem> displayConfigItems = new ArrayList<>();
        private List<PositionableItem> displayItems = new ArrayList<>();
        private List<Placeholder> placeholders = new ArrayList<>();

        protected Builder() {}

        public Builder player(EnvyPlayer<?> player) {
            this.player = player;
            return this;
        }

        public Builder playerManager(PlayerManager<?, ?> playerManager) {
            this.playerManager = playerManager;
            return this;
        }

        public Builder descriptionItem(ItemStack descriptionItem) {
            this.descriptionItem = descriptionItem.copy();
            return this;
        }

        public Builder config(ConfirmConfig config) {
            this.confirmConfig = config;
            return this;
        }

        public Builder returnHandler(BiConsumer<EnvyPlayer<?>, Displayable.ClickType> returnHandler) {
            this.returnHandler = returnHandler;
            return this;
        }

        public Builder confirmHandler(BiConsumer<EnvyPlayer<?>, Displayable.ClickType> confirmHandler) {
            this.confirmHandler = confirmHandler;
            return this;
        }

        public Builder displayConfigItems(List<ExtendedConfigItem> displayConfigItems) {
            this.displayConfigItems.addAll(displayConfigItems);
            return this;
        }

        public Builder displayConfigItem(ExtendedConfigItem displayConfigItem) {
            this.displayConfigItems.add(displayConfigItem);
            return this;
        }

        public Builder displayConfigItems(ExtendedConfigItem... displayConfigItems) {
            this.displayConfigItems.addAll(Arrays.asList(displayConfigItems));
            return this;
        }

        public Builder displayItems(List<PositionableItem> displayItems) {
            this.displayItems.addAll(displayItems);
            return this;
        }

        public Builder displayItem(PositionableItem displayItem) {
            this.displayItems.add(displayItem);
            return this;
        }

        public Builder displayItems(PositionableItem... displayItems) {
            this.displayItems.addAll(Arrays.asList(displayItems));
            return this;
        }

        public Builder transformers(List<Placeholder> transformers) {
            this.placeholders.addAll(transformers);
            return this;
        }

        public Builder transformer(Placeholder transformer) {
            this.placeholders.add(transformer);
            return this;
        }

        public Builder transformers(Placeholder... transformers) {
            this.placeholders.addAll(Arrays.asList(transformers));
            return this;
        }

        public void open() {
            if (this.player == null || this.confirmConfig == null || this.playerManager == null) {
                return;
            }

            ConfirmationUI.open(this);
        }
    }

    /**
     *
     * The config settings for the UI
     *
     */
    @ConfigSerializable
    public static class ConfirmConfig {

        private ConfigInterface guiSettings = ConfigInterface.defaultInterface("Confirm");

        private ExtendedConfigItem declineItem = ExtendedConfigItem.builder()
                .type("minecraft:red_wool")
                .name("&c&lDECLINE")
                .amount(1)
                .positions(6, 1)
                .build();

        private ExtendedConfigItem acceptItem = ExtendedConfigItem.builder()
                .type("minecraft:lime_wool")
                .name("&a&lACCEPT")
                .amount(1)
                .positions(6, 1)
                .build();

        private int descriptionPosition = 13;

        public ConfirmConfig() {
        }

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public ExtendedConfigItem getDeclineItem() {
            return this.declineItem;
        }

        public ExtendedConfigItem getAcceptItem() {
            return this.acceptItem;
        }

        public int getDescriptionPosition() {
            return this.descriptionPosition;
        }
    }
}
