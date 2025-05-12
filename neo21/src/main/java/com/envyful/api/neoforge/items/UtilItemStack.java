package com.envyful.api.neoforge.items;

import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.text.Placeholder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * Static utility class for generic {@link ItemStack} methods.
 *
 */
public class UtilItemStack {

    /**
     *
     * Returns the lore of the {@param itemStack} as a {@link List} of Strings.
     *
     * Will return {@link Collections#emptyList()} if the parameter is null
     *
     * @param itemStack The item to get the lore of
     * @return The lore of the item
     */
    public static List<String> getLore(ItemStack itemStack) {
        if (!itemStack.has(DataComponents.LORE)) {
            return List.of();
        }

        var lore = itemStack.get(DataComponents.LORE);

        if (lore == null) {
            return List.of();
        }

        List<String> convertedLore = new ArrayList<>();

        for (Component line : lore.lines()) {
            convertedLore.add(line.getString());
        }

        return convertedLore;
    }

    /**
     *
     * Returns the lore of the {@param itemStack} as a {@link List} of ITextComponent.
     *
     * Will return {@link Collections#emptyList()} if the parameter is null
     *
     * @param itemStack The item to get the lore of
     * @return The lore of the item
     */
    public static List<Component> getRealLore(ItemStack itemStack) {
        if (!itemStack.has(DataComponents.LORE)) {
            return List.of();
        }

        var lore = itemStack.get(DataComponents.LORE);

        if (lore == null) {
            return List.of();
        }

        return lore.lines();
    }

    /**
     *
     * Sets the lore of the specified itemstack to the given list
     *
     * @param itemStack The itemstack to update the lore of
     * @param lore The new lore
     */
    public static void setLore(ItemStack itemStack, List<String> lore, Placeholder... placeholders) {
        List<Component> parsedLore = new ArrayList<>();

        for (String s : lore) {
            List<Component> components = PlatformProxy.parse(s, placeholders);
            parsedLore.addAll(components);
        }

        itemStack.set(DataComponents.LORE, new ItemLore(parsedLore, parsedLore));
    }

    public static void setLore(ItemStack itemStack, List<Component> lore) {
        itemStack.set(DataComponents.LORE, new ItemLore(lore, lore));
    }

    public static void setName(ItemStack itemStack, Component name) {
        itemStack.set(DataComponents.CUSTOM_NAME, name);
    }
}
