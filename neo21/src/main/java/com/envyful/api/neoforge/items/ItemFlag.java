package com.envyful.api.neoforge.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.function.Consumer;

/**
 *
 * Enum class representing the old ItemFlags that could be applied to an ItemStack.
 *
 */
public enum ItemFlag {

    HIDE_ENCHANTS(itemStack -> {
        var enchantData = itemStack.get(DataComponents.ENCHANTMENTS);

        if (enchantData != null) {
            itemStack.set(DataComponents.ENCHANTMENTS, enchantData.withTooltip(false));
        } else {
            itemStack.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY.withTooltip(false));
        }
    }),
    HIDE_MODIFIERS(itemStack -> {
        var modifiers = itemStack.get(DataComponents.ATTRIBUTE_MODIFIERS);

        if (modifiers != null) {
            itemStack.set(DataComponents.ATTRIBUTE_MODIFIERS, modifiers.withTooltip(false));
        } else {
            itemStack.set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY.withTooltip(false));
        }
    }),
    HIDE_UNBREAKABLE(itemStack -> {
        var unbreakable = itemStack.get(DataComponents.UNBREAKABLE);

        if (unbreakable != null) {
            itemStack.set(DataComponents.UNBREAKABLE, unbreakable.withTooltip(false));
        }
    }),
    HIDE_CAN_DESTROY(itemStack -> {
    }),
    HIDE_CAN_PLACE(itemStack -> {
    }),
    HIDE_EXTRA(itemStack ->  {
        if (!itemStack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)) {
            itemStack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
        }
    }),
    HIDE_DYE(itemStack -> {
        var dye = itemStack.get(DataComponents.DYED_COLOR);

        if (dye != null) {
            itemStack.set(DataComponents.DYED_COLOR, dye.withTooltip(false));
        }
    }),
    HIDE_TOOLTIP(itemStack -> {
        if (!itemStack.has(DataComponents.HIDE_TOOLTIP)) {
            itemStack.set(DataComponents.HIDE_TOOLTIP, Unit.INSTANCE);
        }
    })

    ;

    private final Consumer<ItemStack> application;

    ItemFlag(Consumer<ItemStack> application) {
        this.application = application;
    }

    public void apply(ItemStack stack) {
        this.application.accept(stack);
    }
}
