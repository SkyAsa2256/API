package com.envyful.api.forge.gui.item;

import com.envyful.api.gui.item.Displayable;
import com.envyful.api.player.EnvyPlayer;
import net.minecraft.item.ItemStack;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 *
 * A static Forge implementation of the {@link Displayable} interface. Meaning the itemstack cannot be changed once initially
 * set.
 *
 */
public class ForgeStaticDisplayable implements Displayable {

    private final ItemStack itemStack;
    private final BiFunction<EnvyPlayer<?>, ClickType, Boolean> clickHandler;
    private final Consumer<EnvyPlayer<?>> updateHandler;

    public ForgeStaticDisplayable(ItemStack itemStack, BiFunction<EnvyPlayer<?>, ClickType, Boolean> clickHandler,
                                  Consumer<EnvyPlayer<?>> updateHandler) {
        this.itemStack = itemStack;
        this.clickHandler = clickHandler;
        this.updateHandler = updateHandler;
    }

    @Override
    public boolean onClick(EnvyPlayer<?> player, ClickType clickType) {
        return this.clickHandler.apply(player, clickType);
    }

    @Override
    public void update(EnvyPlayer<?> viewer) {
        this.updateHandler.accept(viewer);
    }

    public static final class Converter {
        public static ItemStack toNative(ForgeStaticDisplayable displayable) {
            return displayable.itemStack;
        }
    }

    public static final class Builder implements Displayable.Builder<ItemStack> {

        private ItemStack itemStack;
        private BiFunction<EnvyPlayer<?>, ClickType, Boolean> clickHandler;
        private Consumer<EnvyPlayer<?>> updateHandler;

        @Override
        public Displayable.Builder<ItemStack> itemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        @Override
        public Displayable.Builder<ItemStack> clickHandler(BiFunction<EnvyPlayer<?>, ClickType, Boolean> clickHandler) {
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

            return new ForgeStaticDisplayable(this.itemStack, this.clickHandler, this.updateHandler);
        }
    }
}
