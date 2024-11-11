package com.envyful.api.forge.gui.type;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
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
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.util.TriConsumer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class MultiSelectionUI {

    private static void open(Builder config) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(config.config.guiSettings.getHeight())
                .build();

        Placeholder[] placeholders = config.transformers.toArray(new Placeholder[0]);

        UtilConfigInterface.fillBackground(pane, config.config.getGuiSettings(), placeholders);

        int optionPositionsSize = config.config.optionPositions.size();
        List<Map.Entry<String, ConfigItem>> items = new ArrayList<>(config.config.options.entrySet());
        items.sort(Map.Entry.comparingByKey());

        for (int i = (config.page * optionPositionsSize); i < ((config.page + 1) * optionPositionsSize); ++i) {
            int posX = config.config.optionPositions.get(i % optionPositionsSize) % 9;
            int posY = config.config.optionPositions.get(i % optionPositionsSize) / 9;

            if (i >= items.size()) {
                break;
            }

            Map.Entry<String, ConfigItem> item = items.get(i);
            ItemStack itemStack = UtilConfigItem.fromConfigItem(item.getValue(), config.transformers);

            pane.set(posX, posY,
                     GuiFactory.displayableBuilder(itemStack)
                             .clickHandler((envyPlayer, clickType) -> {
                                 if (config.confirm != null) {
                                     config.confirm.descriptionItem(itemStack);
                                     config.confirm.returnHandler((envyPlayer1, clickType1) -> open(config));
                                     config.confirm.confirmHandler((clicker, clickerType) -> config.acceptHandler.accept(clicker, clickerType, item.getKey()));
                                     config.confirm.playerManager(config.playerManager);
                                     config.confirm.player(envyPlayer);
                                     config.confirm.transformers(config.transformers);
                                     config.confirm.open();
                                 } else {
                                     config.selectHandler.accept(envyPlayer, clickType, item.getKey());
                                 }
                             })
                             .build()
            );
        }

        UtilConfigItem.builder()
                .clickHandler(config.returnHandler)
                .extendedConfigItem((ForgeEnvyPlayer) config.player, pane, config.config.getBackButton(), placeholders);

        if (items.size() > optionPositionsSize) {
            UtilConfigItem.builder()
                    .clickHandler((envyPlayer, clickType) -> {
                        if (config.page == 0) {
                            config.page = (config.config.options.size() / config.config.optionPositions.size());
                        } else {
                            config.page -= 1;
                        }

                        open(config);
                    })
                    .extendedConfigItem((ForgeEnvyPlayer) config.player, pane, config.config.getPreviousPageButton(), placeholders);

            UtilConfigItem.builder()
                    .clickHandler((envyPlayer, clickType) -> {
                        if (config.page == (config.config.options.size() / config.config.optionPositions.size())) {
                            config.page = 0;
                        } else {
                            config.page += 1;
                        }

                        open(config);
                    })
                    .extendedConfigItem((ForgeEnvyPlayer) config.player, pane, config.config.getNextPageButton(), placeholders);
        }

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
        private MultiSelectionConfig config = null;
        private BiConsumer<EnvyPlayer<?>, Displayable.ClickType> returnHandler = null;
        private TriConsumer<EnvyPlayer<?>, Displayable.ClickType, String> acceptHandler = null;
        private TriConsumer<EnvyPlayer<?>, Displayable.ClickType, String> selectHandler = null;
        private ConfirmationUI.Builder confirm = null;
        private List<ExtendedConfigItem> displayConfigItems = new ArrayList<>();
        private List<PositionableItem> displayItems = new ArrayList<>();
        private int page = 0;
        private List<Placeholder> transformers = new ArrayList<>();

        protected Builder() {}

        public Builder player(EnvyPlayer<?> player) {
            this.player = player;
            return this;
        }

        public Builder playerManager(PlayerManager<?, ?> playerManager) {
            this.playerManager = playerManager;
            return this;
        }

        public Builder config(MultiSelectionConfig config) {
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

        public Builder selectHandler(TriConsumer<EnvyPlayer<?>, Displayable.ClickType, String> selectHandler) {
            this.selectHandler = selectHandler;
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

        public Builder page(int page) {
            this.page = page;
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
            if (this.player == null || this.playerManager == null || this.config == null || this.returnHandler == null) {
                return;
            }

            MultiSelectionUI.open(this);
        }
    }

    @ConfigSerializable
    public static class MultiSelectionConfig {

        private ConfigInterface guiSettings;

        private Map<String, ConfigItem> options;
        private List<Integer> optionPositions;

        private ExtendedConfigItem backButton = ExtendedConfigItem.builder()
                .type("minecraft:barrier")
                .amount(1)
                .name("&cBack")
                .positions(0, 0)
                .build();

        private ExtendedConfigItem nextPageButton = ExtendedConfigItem.builder()
                .type("minecraft:arrow")
                .name("&aNext Page")
                .amount(1)
                .positions(8, 5)
                .build();

        private ExtendedConfigItem previousPageButton = ExtendedConfigItem.builder()
                .type("minecraft:arrow")
                .name("&aPrevious Page")
                .amount(1)
                .positions(0, 5)
                .build();

        public MultiSelectionConfig(String title, int height, Map<String, ConfigItem> options,
                                    List<Integer> optionPositions) {
            this.options = options;
            this.optionPositions = optionPositions;
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

        public MultiSelectionConfig() {}

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public Map<String, ConfigItem> getOptions() {
            return this.options;
        }

        public List<Integer> getOptionPositions() {
            return this.optionPositions;
        }

        public ExtendedConfigItem getBackButton() {
            return this.backButton;
        }

        public ExtendedConfigItem getNextPageButton() {
            return this.nextPageButton;
        }

        public ExtendedConfigItem getPreviousPageButton() {
            return this.previousPageButton;
        }
    }
}
