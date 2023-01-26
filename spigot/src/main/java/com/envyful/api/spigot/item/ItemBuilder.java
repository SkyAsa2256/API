package com.envyful.api.spigot.item;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.Consumer;

/**
 *
 * Spigot {@link ItemStack} builder class.
 *
 */
public class ItemBuilder extends ItemStack {

    public ItemBuilder() {}

    public ItemBuilder type(Material type) {
        this.setType(type);
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.setAmount(amount);
        return this;
    }

    public ItemBuilder name(Component name) {
        return this.updateItemMeta(itemMeta -> itemMeta.displayName(name));
    }

    public ItemBuilder lore(Component... lore) {
        this.lore(Lists.newArrayList(lore));
        return this;
    }

    public ItemBuilder addLore(Component... lore) {
        List<Component> lore1 = this.lore();
        lore1.addAll(Lists.newArrayList(lore));
        this.lore(lore1);
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        return this.updateItemMeta(itemMeta -> itemMeta.setUnbreakable(unbreakable));
    }

    public ItemBuilder itemFlags(ItemFlag... itemFlags) {
        return this.updateItemMeta(itemMeta -> itemMeta.addItemFlags(itemFlags));
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        return this.updateItemMeta(itemMeta -> itemMeta.addEnchant(enchantment, level, true));
    }

    public ItemBuilder updateItemMeta(Consumer<ItemMeta> consumer) {
        ItemMeta itemMeta = this.getItemMeta();
        consumer.accept(itemMeta);
        this.setItemMeta(itemMeta);
        return this;
    }

    public ItemStack build() {
        return this;
    }
}
