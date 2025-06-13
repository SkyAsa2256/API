package com.envyful.api.config.type;

import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.Placeholder;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ExtendedConfigItemDisplayableBuilder<T> implements Displayable.Builder<T> {

    private final ExtendedConfigItem configItem;
    private final EnvyPlayer<?> player;
    private final Placeholder[] placeholders;
    private final Pane pane;

    protected T itemStack;
    protected BiConsumer<EnvyPlayer<?>, Displayable.ClickType> clickHandler = (envyPlayer, clickType) -> {};
    protected int tickDelay = 0;
    protected boolean async = true;
    protected boolean singleClick = false;
    protected long clickDelay = 50L;
    protected int lockOutClicks = 100;

    public ExtendedConfigItemDisplayableBuilder(ExtendedConfigItem configItem, EnvyPlayer<?> player, Placeholder... placeholders) {
        this(configItem, player, null, placeholders);
    }

    public ExtendedConfigItemDisplayableBuilder(ExtendedConfigItem configItem, EnvyPlayer<?> player, Pane pane, Placeholder[] placeholders) {
        this.configItem = configItem;
        this.player = player;
        this.pane = pane;
        this.placeholders = placeholders;
    }

    @Override
    public Displayable.Builder<T> lockOutClicks(int clickLockCount) {
        this.lockOutClicks = clickLockCount;
        return this;
    }

    @Override
    public Displayable.Builder<T> clickDelay(long milliseconds) {
        this.clickDelay = milliseconds;
        return this;
    }

    @Override
    public Displayable.Builder<T> singleClick(boolean singleClick) {
        this.singleClick = singleClick;
        return this;
    }

    @Override
    public Displayable.Builder<T> asyncClick(boolean async) {
        this.async = async;
        return this;
    }

    @Override
    public Displayable.Builder<T> delayTicks(int tickDelay) {
        this.tickDelay = tickDelay;
        return this;
    }

    @Override
    public Displayable.Builder<T> clickHandler(BiConsumer<EnvyPlayer<?>, Displayable.ClickType> clickHandler) {
        this.clickHandler = clickHandler;
        return this;
    }

    @Override
    public BiConsumer<EnvyPlayer<?>, Displayable.ClickType> clickHandler() {
        return this.clickHandler;
    }

    @Override
    public Displayable.Builder<T> itemStack(T itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    @Override
    public Displayable build(Consumer<T> itemConsumer) {
        if (!this.configItem.isEnabled()) {
            return null;
        }

        Displayable.Builder<T> builder = (Displayable.Builder<T>) this.configItem.convert(this.player, this.placeholders);

        if (builder == null) {
            return null;
        }

        builder.asyncClick(this.async)
                .clickDelay(this.clickDelay)
                .lockOutClicks(this.lockOutClicks)
                .singleClick(this.singleClick)
                .delayTicks(this.tickDelay);

        if (this.clickHandler != null) {
            var originalHandler = builder.clickHandler();
            builder.clickHandler((envyPlayer, clickType) -> {
                if (originalHandler != null) {
                    originalHandler.accept(envyPlayer, clickType);
                }

                this.clickHandler.accept(envyPlayer, clickType);
            });
        }

        var displayable = builder.build(itemConsumer);

        if (this.pane != null) {
            for (var position : this.configItem.getPositions()) {
                pane.set(position.getX(), position.getY(), displayable);
            }
        }

        return displayable;
    }
}
