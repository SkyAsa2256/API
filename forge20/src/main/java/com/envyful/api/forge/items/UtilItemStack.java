package com.envyful.api.forge.items;

import com.envyful.api.forge.chat.UtilChatColour;
import com.google.common.collect.Lists;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

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
        if (itemStack == null) {
            return Collections.emptyList();
        }

        List<String> lore = Lists.newArrayList();

        ListTag currentLore = itemStack.getOrCreateTagElement("display").getList("Lore", 8);

        for (Tag nbtBase : currentLore) {
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
        if (itemStack == null) {
            return Collections.emptyList();
        }

        List<Component> lore = Lists.newArrayList();

        ListTag currentLore = itemStack.getOrCreateTagElement("display").getList("Lore", 8);

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
    public static void setLore(ItemStack itemStack, List<String> lore) {
        var display = itemStack.getOrCreateTagElement("display");
        var newLore = new ListTag();

        lore.forEach(s -> {
            var component = UtilChatColour.colour(s);
            if (component instanceof MutableComponent) {
                component = ((MutableComponent) component).setStyle(component.getStyle().withItalic(false));
            }

            newLore.add(StringTag.valueOf(Component.Serializer.toJson(component)));
        });

        display.put("Lore", newLore);
        itemStack.addTagElement("display", display);
    }
}
