package com.envyful.api.forge.config;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.PermissibleConfigItem;
import com.envyful.api.config.type.PositionableConfigItem;
import com.envyful.api.forge.gui.item.ForgeSimpleDisplayable;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.Transformer;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.type.Pair;
import net.minecraft.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ConfigItemBuilder extends ForgeSimpleDisplayable.Builder {

    protected ConfigItemBuilder() {
        super();
    }

    public Displayable permissibleConfigItem(ForgeEnvyPlayer player, Pane pane, PermissibleConfigItem configItem, Transformer... transformers) {
        return this.positionableConfigItem(pane, UtilConfigItem.fromPermissibleItem(player.getParent(), configItem, transformers), configItem);
    }

    public Displayable positionableConfigItem(Pane pane, PositionableConfigItem configItem, Transformer... transformers) {
        return this.positionableConfigItem(pane, UtilConfigItem.fromConfigItem(configItem, transformers), configItem);
    }

    public Displayable positionableConfigItem(Pane pane, ItemStack item, PositionableConfigItem configItem) {
        Displayable build = this.itemStack(item).build();

        if (build == null) {
            return null;
        }

        for (Pair<Integer, Integer> position : configItem.getPositions()) {
            pane.set(position.getX(), position.getY(), build);
        }

        return build;
    }

    public ConfigItemBuilder configItem(ConfigItem configItem, Transformer... transformers) {
        return this.itemStack(UtilConfigItem.fromConfigItem(configItem, transformers));
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
    public ConfigItemBuilder updateHandler(Consumer<EnvyPlayer<?>> updateHandler) {
        return (ConfigItemBuilder) super.updateHandler(updateHandler);
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
