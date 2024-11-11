package com.envyful.api.forge.gui.config;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigInterface;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.Placeholder;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 *
 * A Confirmation UI config class
 *
 */
@ConfigSerializable
public class ConfirmationUI {

    private ConfigInterface guiSettings = ConfigInterface.builder()
            .height(3)
            .title("&7Are you sure?")
            .fillType(ConfigInterface.FillType.BLOCK)
            .fillerItem(ConfigItem.builder()
                    .type("minecraft:black_stained_glass_pane")
                    .name(" ")
                    .amount(1)
                    .build()
            )
            .build();
    private ExtendedConfigItem confirmItem = ExtendedConfigItem.builder()
            .type("minecraft:lime_stained_glass_pane")
            .name("&aConfirm")
            .lore("&7Click to confirm")
            .positions(5, 1)
            .amount(1)
            .build();
    private ExtendedConfigItem denyItem = ExtendedConfigItem.builder()
            .type("minecraft:red_stained_glass_pane")
            .name("&cDeny")
            .lore("&7Click to deny")
            .positions(3, 1)
            .amount(1)
            .build();
    private ExtendedConfigItem returnItem = ExtendedConfigItem.builder()
            .disable()
            .type("minecraft:barrier")
            .name("&cDeny")
            .lore("&7Click to deny")
            .positions(0, 0)
            .amount(1)
            .build();
    private Map<String, ExtendedConfigItem> additionalItems = Map.of();

    public ConfirmationUI() {
    }

    protected void open(ForgeEnvyPlayer player, Builder builder) {
        var placeholders = builder.placeholders.toArray(new Placeholder[0]);
        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(this.guiSettings.getHeight())
                .build();

        UtilConfigInterface.fillBackground(pane, this.guiSettings, placeholders);

        UtilConfigItem.builder()
                .singleClick()
                .clickHandler(builder.confirmHandler)
                .extendedConfigItem(player, pane, this.confirmItem, placeholders);

        UtilConfigItem.builder()
                .singleClick()
                .clickHandler(builder.returnHandler)
                .extendedConfigItem(player, pane, this.returnItem, placeholders);

        UtilConfigItem.builder()
                .singleClick()
                .clickHandler(builder.denyHandler)
                .extendedConfigItem(player, pane, this.denyItem, placeholders);

        for (ExtendedConfigItem value : this.additionalItems.values()) {
            UtilConfigItem.builder()
                    .singleClick()
                    .extendedConfigItem(player, pane, value, placeholders);
        }

        GuiFactory.guiBuilder()
                .setPlayerManager(PlatformProxy.getPlayerManager())
                .addPane(pane)
                .height(this.guiSettings.getHeight())
                .title(UtilChatColour.colour(this.guiSettings.getTitle()))
                .build().open(player);
    }

    /**
     *
     * Creates a builder to open the GUI
     *
     * @return The builder
     */
    public Builder builder() {
        return new Builder(this);
    }

    /**
     *
     * The builder to allow for controlling how players interact with the GUI
     *
     */
    public static class Builder {

        private ConfirmationUI ui;
        private BiConsumer<EnvyPlayer<?>, Displayable.ClickType> confirmHandler;
        private BiConsumer<EnvyPlayer<?>, Displayable.ClickType> denyHandler;
        private BiConsumer<EnvyPlayer<?>, Displayable.ClickType> returnHandler;
        private List<Placeholder> placeholders = new ArrayList<>();

        private Builder(ConfirmationUI ui) {
            this.ui = ui;
        }

        public Builder confirmHandler(BiConsumer<EnvyPlayer<?>, Displayable.ClickType> confirmHandler) {
            this.confirmHandler = confirmHandler;
            return this;
        }

        public Builder denyHandler(BiConsumer<EnvyPlayer<?>, Displayable.ClickType> denyHandler) {
            this.denyHandler = denyHandler;
            return this;
        }

        public Builder returnHandler(BiConsumer<EnvyPlayer<?>, Displayable.ClickType> returnHandler) {
            this.returnHandler = returnHandler;
            return this;
        }

        public Builder placeholders(Placeholder... placeholder) {
            this.placeholders.addAll(List.of(placeholder));
            return this;
        }

        public void open(ForgeEnvyPlayer player) {
            this.ui.open(player, this);
        }
    }
}
