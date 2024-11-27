package com.envyful.api.forge.items;

import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.text.Placeholder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

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
     * Returns the lore of the itemStack as a {@link List} of Strings.
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
            return List.of();
        }

        var currentLore = itemStack.getTagElement("display").getList("Lore", 8);

        for (var nbtBase : currentLore) {
            if (nbtBase instanceof StringNBT) {
                lore.add(nbtBase.getAsString());
            }
        }

        return lore;
    }

    /**
     *
     * Returns the lore of the itemStack as a {@link List} of ITextComponent.
     *
     * Will return {@link Collections#emptyList()} if the parameter is null
     *
     * @param itemStack The item to get the lore of
     * @return The lore of the item
     */
    public static List<ITextComponent> getRealLore(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasTag()) {
            return List.of();
        }

        List<ITextComponent> lore = new ArrayList<>();
        var tag = itemStack.getTag();

        if (!tag.contains("display")) {
            return List.of();
        }

        var currentLore = itemStack.getTagElement("display").getList("Lore", 8);

        for (INBT nbtBase : currentLore) {
            if (nbtBase instanceof StringNBT) {
                try {
                    lore.add(ITextComponent.Serializer.fromJson(nbtBase.getAsString()));
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
        CompoundNBT display = itemStack.getOrCreateTagElement("display");
        ListNBT newLore = new ListNBT();

        lore.forEach(s -> {
            List<ITextComponent> components = PlatformProxy.parse(s, placeholders);

            for (var component : components) {
                if (component instanceof IFormattableTextComponent) {
                    component = ((IFormattableTextComponent) component).setStyle(component.getStyle().withItalic(false));
                }

                newLore.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(component)));
            }
        });

        display.put("Lore", newLore);
        itemStack.addTagElement("display", display);
    }
}
