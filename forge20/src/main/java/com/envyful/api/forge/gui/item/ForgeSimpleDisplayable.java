package com.envyful.api.forge.gui.item;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.EnvyPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;

/**
 *
 * A static Forge implementation of the {@link Displayable} interface. Meaning the itemstack cannot be changed once initially
 * set.
 *
 */
public class ForgeSimpleDisplayable implements Displayable {

    private final ItemStack itemStack;
    private final BiConsumer<EnvyPlayer<?>, ClickType> clickHandler;
    private final int tickDelay;
    private final boolean async;
    private final boolean singleClick;
    private final long clickDelay;
    private final int lockOutClicks;

    private boolean clicked = false;
    private long lastClick = -1L;
    private int clicks = 0;

    public ForgeSimpleDisplayable(ItemStack itemStack, BiConsumer<EnvyPlayer<?>, ClickType> clickHandler,
                                  int tickDelay, boolean async, boolean singleClick,
                                  long clickDelay, int lockOutClicks) {
        this.itemStack = itemStack;
        this.clickHandler = clickHandler;
        this.tickDelay = tickDelay;
        this.async = async;
        this.singleClick = singleClick;
        this.clickDelay = clickDelay;
        this.lockOutClicks = lockOutClicks;
    }

    @Override
    public void onClick(EnvyPlayer<?> player, ClickType clickType) {
        if (this.clicked && this.singleClick) {
            return;
        }

        this.clicked = true;

        if (++this.clicks >= this.lockOutClicks) {
            return;
        }

        if (this.lastClick != -1 && (System.currentTimeMillis() - this.lastClick) <= this.clickDelay) {
            return;
        }

        this.lastClick = System.currentTimeMillis();

        if (this.tickDelay <= 0) {
            if (this.async) {
                UtilConcurrency.runAsync(() -> this.clickHandler.accept(player, clickType));
            } else {
                PlatformProxy.runSync(() -> this.clickHandler.accept(player, clickType));
            }

            return;
        }

        if (this.async) {
            UtilConcurrency.runLater(() -> this.clickHandler.accept(player, clickType), this.tickDelay * 50L);
        } else {
            PlatformProxy.runLater(() -> this.clickHandler.accept(player, clickType), this.tickDelay);
        }
    }

    public static final class Converter {
        public static ItemStack toNative(ForgeSimpleDisplayable displayable) {
            return displayable.itemStack;
        }
    }

    public static class Builder implements Displayable.Builder<ItemStack> {

        protected ItemStack itemStack;
        protected BiConsumer<EnvyPlayer<?>, ClickType> clickHandler = (envyPlayer, clickType) -> {};
        protected int tickDelay = 0;
        protected boolean async = true;
        protected boolean singleClick = false;
        protected long clickDelay = 50L;
        protected int lockOutClicks = 100;

        @Override
        public Displayable.Builder<ItemStack> itemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        @Override
        public BiConsumer<EnvyPlayer<?>, ClickType> clickHandler() {
            return this.clickHandler;
        }

        @Override
        public Displayable.Builder<ItemStack> clickHandler(BiConsumer<EnvyPlayer<?>, ClickType> clickHandler) {
            this.clickHandler = clickHandler;
            return this;
        }

        @Override
        public Displayable.Builder<ItemStack> delayTicks(int tickDelay) {
            this.tickDelay = tickDelay;
            return this;
        }

        @Override
        public Displayable.Builder<ItemStack> asyncClick(boolean async) {
            this.async = async;
            return this;
        }

        @Override
        public Displayable.Builder<ItemStack> singleClick(boolean singleClick) {
            this.singleClick = singleClick;
            return this;
        }

        @Override
        public Displayable.Builder<ItemStack> clickDelay(long milliseconds) {
            this.clickDelay = milliseconds;
            return this;
        }

        @Override
        public Displayable.Builder<ItemStack> lockOutClicks(int clickLockCount) {
            this.lockOutClicks = clickLockCount;
            return this;
        }

        @Override
        public Displayable build() {
            if (this.itemStack == null) {
                throw new RuntimeException("Cannot create displayable without itemstack");
            }

            return new ForgeSimpleDisplayable(this.itemStack, this.clickHandler, this.tickDelay,
                    this.async, this.singleClick, this.clickDelay, this.lockOutClicks);
        }
    }
}
