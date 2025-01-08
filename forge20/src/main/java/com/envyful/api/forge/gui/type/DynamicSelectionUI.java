package com.envyful.api.forge.gui.type;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.forge.config.UtilConfigInterface;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.gui.item.PositionableItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.text.Placeholder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.TriConsumer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class DynamicSelectionUI {

    private static void open(Builder config) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(config.config.guiSettings.getHeight())
                .build();

        Placeholder[] placeholders = config.placeholders.toArray(new Placeholder[0]);

        UtilConfigInterface.fillBackground(pane, config.config.getGuiSettings(), placeholders);

        for (int i = 0; i < config.config.optionPositions.size(); i++) {
            int pos = config.config.optionPositions.get(i);
            int posX = pos % 9;
            int posY = pos / 9;

            if (config.displayNames.size() <= i) {
                break;
            }

            String displayName = config.displayNames.get(i);
            ItemStack itemStack = new ItemBuilder(UtilConfigItem.fromConfigItem(config.config.getDisplayItem(), config.placeholders))
                            .name(PlatformProxy.<Component>parse(config.config.getNameColour() + displayName)).build();

            pane.set(posX, posY,
                     GuiFactory.displayableBuilder(itemStack)
                             .clickHandler((envyPlayer, clickType) -> {
                                 config.confirm.descriptionItem(itemStack);
                                 config.confirm.returnHandler((envyPlayer1, clickType1) -> open(config));
                                 config.confirm.confirmHandler((clicker, clickerType) -> config.acceptHandler.accept(clicker, clickerType, displayName));
                                 config.confirm.playerManager(config.playerManager);
                                 config.confirm.player(envyPlayer);
                                 config.confirm.transformers(config.placeholders);
                                 config.confirm.open();
                             })
                             .build()
            );
        }

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
                .addPane(pane)
                .height(config.config.guiSettings.getHeight())
                .title(PlatformProxy.parse(config.config.guiSettings.getTitle(), placeholders).get(0))
                .build().open(config.player);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private EnvyPlayer<?> player = null;
        private PlayerManager<?, ?> playerManager = null;
        private DynamicSelectionConfig config = null;
        private BiConsumer<EnvyPlayer<?>, Displayable.ClickType> returnHandler = null;
        private TriConsumer<EnvyPlayer<?>, Displayable.ClickType, String> acceptHandler = null;
        private ConfirmationUI.Builder confirm = null;
        private List<ExtendedConfigItem> displayConfigItems = new ArrayList<>();
        private List<PositionableItem> displayItems = new ArrayList<>();
        private List<String> displayNames = new ArrayList<>();
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

        public Builder config(DynamicSelectionConfig config) {
            this.config = config;
            return this;
        }

        public Builder returnHandler(BiConsumer<EnvyPlayer<?>, Displayable.ClickType> returnHandler) {
            this.returnHandler = returnHandler;
            return this;
        }

        public Builder acceptHandler(TriConsumer<EnvyPlayer<?>, Displayable.ClickType, String> acceptHandler) {
            this.acceptHandler = acceptHandler;
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

        public Builder displayNames(List<String> displayNames) {
            this.displayNames.addAll(displayNames);
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayNames.add(displayName);
            return this;
        }

        public Builder displayNames(String... displayNames) {
            this.displayNames.addAll(Arrays.asList(displayNames));
            return this;
        }

        public Builder transformers(List<Placeholder> placeholders) {
            this.placeholders.addAll(placeholders);
            return this;
        }

        public Builder transformer(Placeholder placeholder) {
            this.placeholders.add(placeholder);
            return this;
        }

        public Builder transformers(Placeholder... placeholders) {
            this.placeholders.addAll(Arrays.asList(placeholders));
            return this;
        }

        public void open() {
            if (this.player == null || this.playerManager == null || this.config == null ||
                    this.returnHandler == null || this.confirm == null || this.acceptHandler == null) {
                return;
            }

            DynamicSelectionUI.open(this);
        }
    }

    @ConfigSerializable
    public static class DynamicSelectionConfig {

        private ConfigInterface guiSettings;

        private List<Integer> optionPositions;

        private ExtendedConfigItem backButton = ExtendedConfigItem.builder()
                .type("minecraft:barrier")
                .name("&cBack")
                .amount(1)
                .positions(0, 0)
                .build();

        private String nameColour;

        private ConfigItem displayItem;

        public DynamicSelectionConfig(String title, int height, String nameColour, List<Integer> optionPositions,
                                      ConfigItem displayItem) {
            this.nameColour = nameColour;
            this.optionPositions = optionPositions;
            this.displayItem = displayItem;
            this.guiSettings = ConfigInterface.builder()
                    .title(title)
                    .height(height)
                    .fillType(ConfigInterface.FillType.BLOCK)
                    .fillerItem(ConfigItem.builder()
                            .type("minecraft:black_stained_glass_pane")
                            .amount(1)
                            .name(" ")
                            .build())
                    .build();
        }

        public DynamicSelectionConfig() {}

        public String getNameColour() {
            return this.nameColour;
        }

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public List<Integer> getOptionPositions() {
            return this.optionPositions;
        }

        public ExtendedConfigItem getBackButton() {
            return this.backButton;
        }

        public ConfigItem getDisplayItem() {
            return this.displayItem;
        }
    }
}
