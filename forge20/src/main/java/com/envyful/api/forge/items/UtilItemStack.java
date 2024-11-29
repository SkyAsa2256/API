package com.envyful.api.forge.items;

import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.text.Placeholder;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

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
        if (itemStack == null || !itemStack.hasTag()) {
            return List.of();
        }

        List<String> lore = new ArrayList<>();
        var tag = itemStack.getTag();

        if (!tag.contains("display")) {
            return lore;
        }

        var currentLore = itemStack.getTagElement("display").getList("Lore", 8);

        for (var nbtBase : currentLore) {
            if (nbtBase instanceof StringTag) {
                lore.add(nbtBase.getAsString());
            }
        }

        return lore;
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
        if (itemStack == null || !itemStack.hasTag()) {
            return List.of();
        }

        List<Component> lore = new ArrayList<>();
        var tag = itemStack.getTag();

        if (!tag.contains("display")) {
            return lore;
        }

        var currentLore = itemStack.getTagElement("display").getList("Lore", 8);

        for (Tag nbtBase : currentLore) {
            if (nbtBase instanceof StringTag) {
                try {
                    lore.add(Component.Serializer.fromJson(nbtBase.getAsString()));
                } catch (Exception ignored) {}
            }
        }

        return lore;
    }

    /**
     *
     * Sets the lore of the specified itemstack to the given list
     *
     * @param itemStack The itemstack to update the lore of
     * @param lore The new lore
     */
    public static void setLore(ItemStack itemStack, List<String> lore, Placeholder... placeholders) {
        var display = itemStack.getOrCreateTagElement("display");
        var newLore = new ListTag();

        lore.forEach(s -> {
            List<Component> components = PlatformProxy.parse(s, placeholders);

            for (var component : components) {
                if (component instanceof MutableComponent) {
                    component = ((MutableComponent) component).setStyle(component.getStyle().withItalic(false));
                }

                newLore.add(StringTag.valueOf(Component.Serializer.toJson(component)));
            }
        });

        display.put("Lore", newLore);
        itemStack.addTagElement("display", display);
    }
}
