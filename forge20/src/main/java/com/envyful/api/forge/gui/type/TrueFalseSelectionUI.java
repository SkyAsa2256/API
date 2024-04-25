package com.envyful.api.forge.gui.type;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigInterface;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.gui.item.PositionableItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.text.Placeholder;
import com.google.common.collect.Lists;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class TrueFalseSelectionUI {

    private static void open(Builder config) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(config.config.guiSettings.getHeight())
                .build();

        Placeholder[] placeholders = config.transformers.toArray(new Placeholder[0]);

        UtilConfigInterface.fillBackground(pane, config.config.getGuiSettings(), placeholders);

        if (config.startsTrue) {
            UtilConfigItem.builder().clickHandler((envyPlayer, clickType) -> {
                config.startsTrue = false;
                open(config);
            }).extendedConfigItem((ForgeEnvyPlayer) config.player, pane, config.config.getTrueItem(), placeholders);
        } else {
            UtilConfigItem.builder().clickHandler((envyPlayer, clickType) -> {
                config.startsTrue = true;
                open(config);
            }).extendedConfigItem((ForgeEnvyPlayer) config.player, pane, config.config.getFalseItem(), placeholders);
        }

        UtilConfigItem.builder().clickHandler((envyPlayer, clickType) -> {
            config.confirm.player(envyPlayer);
            config.confirm.playerManager(config.playerManager);
            config.confirm.returnHandler((envyPlayer1, clickType1) -> open(config));
            config.confirm.transformers(config.transformers);

            if (config.startsTrue) {
                config.confirm.confirmHandler(config.trueAcceptHandler);
                config.confirm.descriptionItem(UtilConfigItem.fromConfigItem(config.config.trueItem, config.transformers));

                config.confirm.open();
            } else {
                config.confirm.confirmHandler(config.falseAcceptHandler);
                config.confirm.descriptionItem(UtilConfigItem.fromConfigItem(config.config.falseItem, config.transformers));
                config.confirm.open();
            }
        }).extendedConfigItem((ForgeEnvyPlayer) config.player, pane, config.config.getAcceptItem(), placeholders);

        UtilConfigItem.builder()
                .clickHandler(config.returnHandler)
                .extendedConfigItem((ForgeEnvyPlayer) config.player, pane, config.config.getBackButton(), placeholders);

        for (ExtendedConfigItem displayItem : config.displayConfigItems) {
            UtilConfigItem.builder().extendedConfigItem((ForgeEnvyPlayer) config.player, pane, displayItem, placeholders);
        }

        for (PositionableItem displayItem : config.displayItems) {
            pane.set(displayItem.getPosX(), displayItem.getPosY(), GuiFactory.displayable(displayItem.getItemStack()));
        }

        GuiFactory.guiBuilder()
                .setPlayerManager(config.playerManager)
                .addPane(pane)
                .height(config.config.guiSettings.getHeight())
                .title(UtilChatColour.colour(config.config.guiSettings.getTitle()))
                .build().open(config.player);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private EnvyPlayer<?> player = null;
        private PlayerManager<?, ?> playerManager = null;
        private TrueFalseConfig config = null;
        private BiConsumer<EnvyPlayer<?>, Displayable.ClickType> returnHandler = null;
        private BiConsumer<EnvyPlayer<?>, Displayable.ClickType> trueAcceptHandler = null;
        private BiConsumer<EnvyPlayer<?>, Displayable.ClickType> falseAcceptHandler = null;
        private ConfirmationUI.Builder confirm = null;
        private boolean startsTrue = true;
        private List<ExtendedConfigItem> displayConfigItems = Lists.newArrayList();
        private List<PositionableItem> displayItems = Lists.newArrayList();
        private List<Placeholder> transformers = Lists.newArrayList();

        protected Builder() {}

        public Builder player(EnvyPlayer<?> player) {
            this.player = player;
            return this;
        }

        public Builder playerManager(PlayerManager<?, ?> playerManager) {
            this.playerManager = playerManager;
            return this;
        }

        public Builder config(TrueFalseConfig config) {
            this.config = config;
            return this;
        }

        public Builder returnHandler(BiConsumer<EnvyPlayer<?>, Displayable.ClickType> returnHandler) {
            this.returnHandler = returnHandler;
            return this;
        }

        public Builder trueAcceptHandler(BiConsumer<EnvyPlayer<?>, Displayable.ClickType> trueAcceptHandler) {
            this.trueAcceptHandler = trueAcceptHandler;
            return this;
        }

        public Builder falseAcceptHandler(BiConsumer<EnvyPlayer<?>, Displayable.ClickType> falseAcceptHandler) {
            this.falseAcceptHandler = falseAcceptHandler;
            return this;
        }

        public Builder startsTrue(boolean startsTrue) {
            this.startsTrue = startsTrue;
            return this;
        }

        public Builder confirm(ConfirmationUI.Builder confirm) {
            this.confirm = confirm;
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
            this.transformers.addAll(transformers);
            return this;
        }

        public Builder transformer(Placeholder transformer) {
            this.transformers.add(transformer);
            return this;
        }

        public Builder transformers(Placeholder... transformers) {
            this.transformers.addAll(Arrays.asList(transformers));
            return this;
        }

        public void open() {
            if (this.player == null || this.playerManager == null || this.config == null ||
                    this.returnHandler == null || this.trueAcceptHandler == null || this.falseAcceptHandler == null ||
                    this.confirm == null) {
                return;
            }

            TrueFalseSelectionUI.open(this);
        }
    }

    @ConfigSerializable
    public static class TrueFalseConfig {

        private ConfigInterface guiSettings = ConfigInterface.defaultInterface("True or False");

        private ExtendedConfigItem trueItem;
        private ExtendedConfigItem falseItem;

        private ExtendedConfigItem acceptItem = ExtendedConfigItem.builder()
                .type("minecraft:lime_stained_glass_pane")
                .amount(1)
                .name("&a&lCONFIRM")
                .positions(6, 1)
                .build();

        private ExtendedConfigItem backButton = ExtendedConfigItem.builder()
                .type("minecraft:barrier")
                .name("&cBack")
                .amount(1)
                .positions(0, 0)
                .build();

        public TrueFalseConfig(ExtendedConfigItem trueItem, ExtendedConfigItem falseItem) {
            this.trueItem = trueItem;
            this.falseItem = falseItem;
        }

        public TrueFalseConfig() {}

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public ExtendedConfigItem getTrueItem() {
            return this.trueItem;
        }

        public ExtendedConfigItem getFalseItem() {
            return this.falseItem;
        }

        public ExtendedConfigItem getAcceptItem() {
            return this.acceptItem;
        }

        public ExtendedConfigItem getBackButton() {
            return this.backButton;
        }
    }
}
