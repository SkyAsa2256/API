package com.envyful.api.spigot.config;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.spigot.item.ItemBuilder;
import com.envyful.api.spigot.player.SpigotEnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.envyful.api.type.UtilParse;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * Static utility for turning the {@link ConfigItem} into an {@link ItemStack} for use in GUIs
 *
 */
public class UtilConfigItem {

    private UtilConfigItem() {
        throw new UnsupportedOperationException("Static utility class");
    }

    public static ConfigItemBuilder builder() {
        return new ConfigItemBuilder();
    }

    public static void addExtendedConfigItem(Pane pane, SpigotEnvyPlayer player, ExtendedConfigItem configItem, Placeholder... transformers) {
        builder().extendedConfigItem(player, pane, configItem, transformers);
    }

    public static ItemStack fromPermissibleItem(Player player, ExtendedConfigItem permissibleConfigItem, Placeholder... transformers) {
        return fromPermissibleItem(player, permissibleConfigItem, List.of(transformers));
    }

    public static ItemStack fromPermissibleItem(Player player, ExtendedConfigItem permissibleConfigItem, List<Placeholder> transformers) {
        if (!permissibleConfigItem.isEnabled()) {
            return null;
        }

        if (!permissibleConfigItem.requiresPermission() || permissibleConfigItem.getPermission().isEmpty() || permissibleConfigItem.getPermission() == null ||
                permissibleConfigItem.getPermission().equalsIgnoreCase("none") ||
                player.hasPermission(permissibleConfigItem.getPermission())) {
            return fromConfigItem(permissibleConfigItem, transformers);
        }

        if (permissibleConfigItem.getElseItem() == null || !permissibleConfigItem.getElseItem().isEnabled()) {
            return null;
        }

        return fromConfigItem(permissibleConfigItem.getElseItem(), transformers);
    }

    public static ItemStack fromConfigItem(ExtendedConfigItem configItem, Placeholder... transformers) {
        return fromConfigItem(configItem.asConfigItem(), List.of(transformers));
    }

    public static ItemStack fromConfigItem(ExtendedConfigItem configItem, List<Placeholder> transformers) {
        return fromConfigItem(configItem.asConfigItem(), transformers);
    }

    public static ItemStack fromConfigItem(ConfigItem configItem, Placeholder... transformers) {
        return fromConfigItem(configItem, List.of(transformers));
    }

    public static ItemStack fromConfigItem(ConfigItem configItem, List<Placeholder> placeholders) {
        if (!configItem.isEnabled()) {
            return null;
        }

        String name = configItem.getName();

        ItemBuilder itemBuilder = new ItemBuilder(Material.valueOf(PlaceholderFactory.handlePlaceholders(configItem.getType(),placeholders).get(0)))
                .amount(configItem.getAmount(placeholders));

        if (configItem.getLore() != null && !configItem.getLore().isEmpty()) {
            itemBuilder.lore(PlaceholderFactory.handlePlaceholders(configItem.getLore(), input -> MiniMessage.miniMessage().deserialize(input).decoration(TextDecoration.ITALIC, false), placeholders));
        }

        if (configItem.getFlags() != null && !configItem.getFlags().isEmpty()) {
            itemBuilder.itemFlags(PlaceholderFactory.handlePlaceholders(configItem.getFlags(), s -> ItemFlag.valueOf(s.toUpperCase(Locale.ROOT)), placeholders).toArray(new ItemFlag[0]));
        }

        if (configItem.getName() != null && !configItem.getName().isEmpty()) {
            itemBuilder.name(PlaceholderFactory.handlePlaceholders(Collections.singletonList(name), input -> MiniMessage.miniMessage().deserialize(input).decoration(TextDecoration.ITALIC, false), placeholders).get(0));
        }

        for (ConfigItem.EnchantData value : configItem.getEnchants().values()) {
            String enchantName = value.getEnchant();
            String level = value.getLevel();

            if (!placeholders.isEmpty()) {
                enchantName = PlaceholderFactory.handlePlaceholders(Collections.singletonList(enchantName), placeholders).get(0);
                level = PlaceholderFactory.handlePlaceholders(Collections.singletonList(level), placeholders).get(0);
            }

            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.fromString(enchantName));
            int parsedLevel = UtilParse.parseInt(level).orElse(1);

            if (enchantment == null) {
                continue;
            }

            itemBuilder.enchant(enchantment, parsedLevel);
        }

        NBTItem nbtItem = new NBTItem(itemBuilder.build());
        var customModelData = -1;

        for (var nbtData : configItem.getNbt().entrySet()) {
            if (nbtData.getKey().equalsIgnoreCase("CustomModelData")) {
                customModelData = Integer.parseInt(nbtData.getValue().getData());
                continue;
            }

            addValue(nbtItem, nbtData.getKey(), nbtData.getValue());
        }

        var item = itemBuilder.build();
        var meta = item.getItemMeta();

        if (meta != null) {
            meta.setCustomModelData(customModelData);
            item.setItemMeta(meta);
        }

        return item;
    }

    public static void addValue(NBTCompound nbtItem, String key, ConfigItem.NBTValue value) {
        if (value.getType().equalsIgnoreCase("nbt")) {
            NBTCompound nbtCompound = nbtItem.addCompound(key);

            for (Map.Entry<String, ConfigItem.NBTValue> entry : value.getSubData().entrySet()) {
                addValue(nbtCompound, entry.getKey(), entry.getValue());
            }

            return;
        }

        switch (value.getType()) {
            case "int":
            case "integer":
                nbtItem.setInteger(key, Integer.parseInt(value.getData()));
                break;
            case "long":
                nbtItem.setLong(key, Long.parseLong(value.getData()));
                break;
            case "byte":
                nbtItem.setByte(key, Byte.parseByte(value.getData()));
                break;
            case "double":
                nbtItem.setDouble(key, Double.parseDouble(value.getData()));
                break;
            case "float":
                nbtItem.setFloat(key, Float.parseFloat(value.getData()));
                break;
            case "short":
                nbtItem.setShort(key, Short.parseShort(value.getData()));
                break;
            default:
            case "string":
                nbtItem.setString(key, value.getData());
                break;
        }
    }
}
