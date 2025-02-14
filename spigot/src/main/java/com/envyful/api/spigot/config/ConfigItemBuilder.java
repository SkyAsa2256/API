package com.envyful.api.spigot.config;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.spigot.gui.item.SpigotSimpleDisplayable;
import com.envyful.api.spigot.player.SpigotEnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.envyful.api.type.Pair;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

/**
 *
 * Builder class for creating {@link Displayable} instances from {@link ConfigItem} and {@link ExtendedConfigItem} instances
 *
 */
public class ConfigItemBuilder extends SpigotSimpleDisplayable.Builder {

    protected ConfigItemBuilder() {
        super();
    }

    public Displayable extendedConfigItem(SpigotEnvyPlayer player, Pane pane, ExtendedConfigItem configItem, Placeholder... transformers) {
        return this.extendedConfigItem(pane, UtilConfigItem.fromPermissibleItem(player.getParent(), configItem, transformers), configItem);
    }

    public Displayable extendedConfigItem(Pane pane, ItemStack item, ExtendedConfigItem configItem) {
        Displayable build = this.itemStack(item).build();

        if (build == null) {
            return null;
        }

        for (Pair<Integer, Integer> position : configItem.getPositions()) {
            pane.set(position.getX(), position.getY(), build);
        }

        if (this.clickHandler == null) {
            if (configItem.shouldCloseOnClick() || !configItem.getCommandsExecuted().isEmpty()) {
                if (!configItem.getCommandsExecuted().isEmpty()) {
                    this.asyncClick(false);
                }

                this.clickHandler((player, clickType) -> this.handleCommands(configItem, (SpigotEnvyPlayer) player));
            }
        }

        return build;
    }

    public ConfigItemBuilder configItem(ConfigItem configItem, Placeholder... transformers) {
        return this.itemStack(UtilConfigItem.fromConfigItem(configItem, transformers));
    }

    public ConfigItemBuilder combinedClickHandler(ExtendedConfigItem configItem, BiConsumer<EnvyPlayer<?>, Displayable.ClickType> clickHandler) {
        return this.clickHandler((player, clickType) -> {
            if (configItem.shouldCloseOnClick() || (configItem.getCommandsExecuted() != null && !configItem.getCommandsExecuted().isEmpty())) {
                this.handleCommands(configItem, (SpigotEnvyPlayer) player);
            }

            if (clickHandler != null) {
                clickHandler.accept(player, clickType);
            }
        });
    }

    protected void handleCommands(ExtendedConfigItem configItem, SpigotEnvyPlayer player) {
        if (!configItem.getCommandsExecuted().isEmpty()) {
            for (String s : configItem.getCommandsExecuted()) {
                Bukkit.getServer().dispatchCommand(player.getParent(), s);
            }
        }

        if (configItem.shouldCloseOnClick()) {
            player.getParent().closeInventory();
        }
    }

    @Override
    public Displayable build() {
        if (this.itemStack == null) {
            return null;
        }

        return super.build();
    }

    @Override
    public ConfigItemBuilder asyncClick() {
        return (ConfigItemBuilder) super.asyncClick();
    }

    @Override
    public ConfigItemBuilder singleClick() {
        return (ConfigItemBuilder) super.singleClick();
    }

    @Override
    public ConfigItemBuilder itemStack(ItemStack itemStack) {
        return (ConfigItemBuilder) super.itemStack(itemStack);
    }

    @Override
    public ConfigItemBuilder clickHandler(BiConsumer<EnvyPlayer<?>, Displayable.ClickType> clickHandler) {
        return (ConfigItemBuilder) super.clickHandler(clickHandler);
    }

    @Override
    public ConfigItemBuilder delayTicks(int tickDelay) {
        return (ConfigItemBuilder) super.delayTicks(tickDelay);
    }

    @Override
    public ConfigItemBuilder asyncClick(boolean async) {
        return (ConfigItemBuilder) super.asyncClick(async);
    }

    @Override
    public ConfigItemBuilder singleClick(boolean singleClick) {
        return (ConfigItemBuilder) super.singleClick(singleClick);
    }

    @Override
    public ConfigItemBuilder clickDelay(long milliseconds) {
        return (ConfigItemBuilder) super.clickDelay(milliseconds);
    }

    @Override
    public ConfigItemBuilder lockOutClicks(int clickLockCount) {
        return (ConfigItemBuilder) super.lockOutClicks(clickLockCount);
    }
}
