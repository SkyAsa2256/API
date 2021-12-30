package com.envyful.api.forge.gui.item;

import com.envyful.api.gui.item.Displayable;
import com.envyful.api.player.EnvyPlayer;
import net.minecraft.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * A static Forge implementation of the {@link Displayable} interface. Meaning the itemstack cannot be changed once initially
 * set.
 *
 */
public class ForgeSimpleDisplayable implements Displayable {

    private final ItemStack itemStack;
    private final BiConsumer<EnvyPlayer<?>, ClickType> clickHandler;
    private final Consumer<EnvyPlayer<?>> updateHandler;

    public ForgeSimpleDisplayable(ItemStack itemStack, BiConsumer<EnvyPlayer<?>, ClickType> clickHandler,
                                  Consumer<EnvyPlayer<?>> updateHandler) {
        this.itemStack = itemStack;
        this.clickHandler = clickHandler;
        this.updateHandler = updateHandler;
    }

    @Override
    public void onClick(EnvyPlayer<?> player, ClickType clickType) {
        this.clickHandler.accept(player, clickType);
    }

    @Override
    public void update(EnvyPlayer<?> viewer) {
        this.updateHandler.accept(viewer);
    }

    public static final class Converter {
        public static ItemStack toNative(ForgeSimpleDisplayable displayable) {
            return displayable.itemStack;
        }
    }

    public static final class Builder implements Displayable.Builder<ItemStack> {

        private ItemStack itemStack;
        private BiConsumer<EnvyPlayer<?>, ClickType> clickHandler = (envyPlayer, clickType) -> {};
        private Consumer<EnvyPlayer<?>> updateHandler = envyPlayer -> {};

        @Override
        public Displayable.Builder<ItemStack> itemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        @Override
        public Displayable.Builder<ItemStack> clickHandler(BiConsumer<EnvyPlayer<?>, ClickType> clickHandler) {
            this.clickHandler = clickHandler;
            return this;
        }

        @Override
        public Displayable.Builder<ItemStack> updateHandler(Consumer<EnvyPlayer<?>> updateHandler) {
            this.updateHandler = updateHandler;
            return this;
        }

        @Override
        public Displayable build() {
            if (this.itemStack == null) {
                throw new RuntimeException("Cannot create displayable without itemstack");
            }

            return new ForgeSimpleDisplayable(this.itemStack, this.clickHandler, this.updateHandler);
        }
    }
}
