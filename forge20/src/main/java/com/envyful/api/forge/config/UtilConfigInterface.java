package com.envyful.api.forge.config;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.PaginatedConfigInterface;
import com.envyful.api.forge.gui.close.ForgeCloseConsumer;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.math.TriConsumer;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.text.Placeholder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;

public class UtilConfigInterface {

    public static <T> PaginatedBuilder<T> paginatedBuilder(List<T> items) {
        return new PaginatedBuilder<T>().items(items);
    }

    public static class PaginatedBuilder<T> {

        private PaginatedConfigInterface configInterface;
        private List<T> items = new ArrayList<>();
        private Function<T, ConfigItem> itemConfigItemConversion;
        private Function<T, Displayable> itemDisplayableConversion;
        private ForgeCloseConsumer closeConsumer = (ForgeCloseConsumer) GuiFactory.closeConsumerBuilder().build();
        private TriConsumer<ForgeEnvyPlayer, Displayable.ClickType, T> pageItemClickHandler = (forgeEnvyPlayer, clickType, t) -> {};
        private List<ObjIntConsumer<Pane>> extraItems = new ArrayList<>();

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
            this.itemConfigItemConversion = itemDisplayableConversion;
            return this;
        }

        public PaginatedBuilder<T> itemConversion(Function<T, Displayable> itemDisplayableConversion) {
            this.itemDisplayableConversion = itemDisplayableConversion;
            return this;
        }

        public PaginatedBuilder<T> closeConsumer(ForgeCloseConsumer closeConsumer) {
            this.closeConsumer = closeConsumer;
            return this;
        }

        public PaginatedBuilder<T> pageItemClickHandler(TriConsumer<ForgeEnvyPlayer, Displayable.ClickType, T> pageItemClickHandler) {
            this.pageItemClickHandler = pageItemClickHandler;
            return this;
        }

        public PaginatedBuilder<T> extraItems(Collection<ObjIntConsumer<Pane>> extraItems) {
            this.extraItems.addAll(extraItems);
            return this;
        }

        public PaginatedBuilder<T> extraItems(ObjIntConsumer<Pane>... extraItems) {
            this.extraItems.addAll(Arrays.asList(extraItems));
            return this;
        }

        public void open(ForgeEnvyPlayer player, Placeholder... placeholders) {
            open(player, 1, placeholders);
        }

        public void open(ForgeEnvyPlayer player, int page, Placeholder... placeholders) {
            var pane = this.configInterface.toPane(placeholders);
            int pages = this.items.size() / this.configInterface.getPositions().size();

            if (this.shouldShowChangePageButtons(page, pages)) {
                this.configInterface.getNextPageButton()
                        .convertToBuilder(player, pane, placeholders)
                        .clickHandler((envyPlayer, clickType) -> {
                            if (this.configInterface.isLoopPages()) {
                                open(player, page == pages ? 1 : page + 1, placeholders);
                            }
                        })
                        .build();

                this.configInterface.getPreviousPageButton()
                        .convertToBuilder(player, pane, placeholders)
                        .clickHandler((envyPlayer, clickType) -> {
                            if (this.configInterface.isLoopPages()) {
                                open(player, page == 1 ? pages : page - 1, placeholders);
                            }
                        })
                        .build();
            }

            for (int i = 0; i < this.configInterface.getPositions().size(); i++) {
                int itemId = ((page - 1) * this.configInterface.getPositions().size()) + i;

                if (itemId >= (this.items.size())) {
                    continue;
                }

                int position = this.configInterface.getPositions().get(i);
                int posX = position % 9;
                int posY = position / 9;
                T item = this.items.get(itemId);

                pane.set(posX, posY, this.getDisplayable(player, item, placeholders));
            }

            for (var extraItem : this.extraItems) {
                extraItem.accept(pane, page);
            }

            GuiFactory.guiBuilder()
                    .addPane(pane)
                    .height(this.configInterface.getHeight())
                    .closeConsumer(this.closeConsumer)
                    .title(PlatformProxy.flatParse(this.configInterface.getTitle(), placeholders))
                    .build()
                    .open(player);
        }

        private boolean shouldShowChangePageButtons(int page, int pages) {
            if (this.configInterface.isDisplayPageButtonsAtLimits()) {
                return true;
            }

            return page == pages || page == 1;
        }

        private Displayable getDisplayable(ForgeEnvyPlayer player, T item, Placeholder... placeholders) {
            if (this.itemDisplayableConversion != null) {
                return this.itemDisplayableConversion.apply(item);
            }

            return GuiFactory.displayableBuilder(
                            UtilConfigItem.fromConfigItem(this.itemConfigItemConversion.apply(item), placeholders))
                    .singleClick()
                    .asyncClick()
                    .clickHandler((envyPlayer, clickType) -> this.pageItemClickHandler.execute(player, clickType, item))
                    .build();
        }
    }
}
