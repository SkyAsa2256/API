package com.envyful.api.spigot.config;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.PaginatedConfigInterface;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.math.TriConsumer;
import com.envyful.api.spigot.gui.close.SpigotCloseConsumer;
import com.envyful.api.spigot.player.SpigotEnvyPlayer;
import com.envyful.api.spigot.player.SpigotPlayerManager;
import com.envyful.api.text.Placeholder;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class UtilConfigInterface {

    public static void fillBackground(Pane pane, ConfigInterface settings, Placeholder... transformers) {
        for (ConfigItem fillerItem : settings.getFillerItems()) {
            if (!fillerItem.isEnabled()) {
                continue;
            }

            pane.add(GuiFactory.displayable(UtilConfigItem.fromConfigItem(fillerItem, transformers)));
        }
    }


    public static <T> PaginatedBuilder<T> paginatedBuilder(List<T> items) {
        return new PaginatedBuilder<T>().items(items);
    }

    public static class PaginatedBuilder<T> {

        private PaginatedConfigInterface configInterface;
        private List<T> items = Lists.newArrayList();
        private Function<T, ConfigItem> itemDisplayableConversion;
        private SpigotPlayerManager playerManager;
        private SpigotCloseConsumer closeConsumer = (SpigotCloseConsumer) GuiFactory.closeConsumerBuilder().build();
        private TriConsumer<SpigotEnvyPlayer, Displayable.ClickType, T> pageItemClickHandler = (forgeEnvyPlayer, clickType, t) -> {};
        private List<Consumer<Pane>> extraItems = Lists.newArrayList();

        private PaginatedBuilder() {
            // Private constructor for static factory method
        }

        public PaginatedBuilder<T> configSettings(PaginatedConfigInterface configInterface) {
            this.configInterface = configInterface;
            return this;
        }

        public PaginatedBuilder<T> items(Collection<? extends T> items) {
            this.items.addAll(items);
            return this;
        }

        public PaginatedBuilder<T> items(T... items) {
            this.items.addAll(Arrays.asList(items));
            return this;
        }

        public PaginatedBuilder<T> itemDisplayableConversion(Function<T, ConfigItem> itemDisplayableConversion) {
            this.itemDisplayableConversion = itemDisplayableConversion;
            return this;
        }

        public PaginatedBuilder<T> playerManager(SpigotPlayerManager playerManager) {
            this.playerManager = playerManager;
            return this;
        }

        public PaginatedBuilder<T> closeConsumer(SpigotCloseConsumer closeConsumer) {
            this.closeConsumer = closeConsumer;
            return this;
        }

        public PaginatedBuilder<T> pageItemClickHandler(TriConsumer<SpigotEnvyPlayer, Displayable.ClickType, T> pageItemClickHandler) {
            this.pageItemClickHandler = pageItemClickHandler;
            return this;
        }

        public PaginatedBuilder<T> extraItems(Collection<Consumer<Pane>> extraItems) {
            this.extraItems.addAll(extraItems);
            return this;
        }

        public PaginatedBuilder<T> extraItems(Consumer<Pane>... extraItems) {
            this.extraItems.addAll(Arrays.asList(extraItems));
            return this;
        }

        public void open(SpigotEnvyPlayer player, Placeholder... placeholders) {
            open(player, 1, placeholders);
        }

        private void open(SpigotEnvyPlayer player, int page, Placeholder... placeholders) {
            Pane pane = GuiFactory.paneBuilder()
                    .topLeftX(0)
                    .topLeftY(0)
                    .width(9)
                    .height(this.configInterface.getHeight())
                    .build();

            UtilConfigInterface.fillBackground(pane, this.configInterface, placeholders);

            int pages = this.items.size() / this.configInterface.getPositions().size();

            if (this.shouldShowChangePageButtons(page, pages)) {
                UtilConfigItem.builder()
                        .clickHandler((envyPlayer, clickType) -> {
                            if (this.configInterface.isLoopPages()) {
                                open(player, page == pages ? 1 : page + 1, placeholders);
                            }
                        }).extendedConfigItem(player, pane, this.configInterface.getNextPageButton(), placeholders);

                UtilConfigItem.builder()
                        .clickHandler((envyPlayer, clickType) -> {
                            if (this.configInterface.isLoopPages()) {
                                open(player, page == 1 ? pages : page - 1, placeholders);
                            }
                        }).extendedConfigItem(player, pane, this.configInterface.getPreviousPageButton(), placeholders);
            }

            for (int i = 0; i < this.configInterface.getPositions().size(); i++) {
                int itemId = ((page - 1) * this.configInterface.getPositions().size()) + i;

                if (itemId > (this.items.size())) {
                    continue;
                }

                int position = this.configInterface.getPositions().get(i);
                int posX = position / 9;
                int posY = position % 9;
                T item = this.items.get(itemId);


                pane.set(posX, posY, GuiFactory.displayableBuilder(
                                UtilConfigItem.fromConfigItem(this.itemDisplayableConversion.apply(item), placeholders))
                        .singleClick()
                        .asyncClick()
                        .clickHandler((envyPlayer, clickType) -> this.pageItemClickHandler.execute(player, clickType, item))
                        .build());
            }

            for (Consumer<Pane> extraItem : this.extraItems) {
                extraItem.accept(pane);
            }

            GuiFactory.guiBuilder()
                    .setPlayerManager(this.playerManager)
                    .addPane(pane)
                    .height(this.configInterface.getHeight())
                    .closeConsumer(this.closeConsumer)
                    .title(this.configInterface.getTitle())
                    .build().open(player);
        }

        private boolean shouldShowChangePageButtons(int page, int pages) {
            if (this.configInterface.isDisplayPageButtonsAtLimits()) {
                return true;
            }

            return page == pages || page == 1;
        }
    }
}
