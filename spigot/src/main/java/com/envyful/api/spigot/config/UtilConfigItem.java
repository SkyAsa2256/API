package com.envyful.api.spigot.config;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.spigot.item.ItemBuilder;
import com.envyful.api.spigot.player.SpigotEnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.envyful.api.type.UtilParse;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        return fromPermissibleItem(player, permissibleConfigItem, Lists.newArrayList(transformers));
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
        return fromConfigItem(configItem.asConfigItem(), Lists.newArrayList(transformers));
    }

    public static ItemStack fromConfigItem(ExtendedConfigItem configItem, List<Placeholder> transformers) {
        return fromConfigItem(configItem.asConfigItem(), transformers);
    }

    public static ItemStack fromConfigItem(ConfigItem configItem, Placeholder... transformers) {
        return fromConfigItem(configItem, Lists.newArrayList(transformers));
    }

    public static ItemStack fromConfigItem(ConfigItem configItem, List<Placeholder> placeholders) {
        if (!configItem.isEnabled()) {
            return null;
        }

        String name = configItem.getName();

        ItemBuilder itemBuilder = new ItemBuilder()
                .type(Material.matchMaterial(configItem.getType()))
                .amount(configItem.getAmount(placeholders));

        if (!placeholders.isEmpty()) {
            itemBuilder.lore(PlaceholderFactory.handlePlaceholders(configItem.getLore(), MiniMessage.miniMessage()::deserialize, placeholders));
            itemBuilder.itemFlags(PlaceholderFactory.handlePlaceholders(configItem.getFlags(), s -> ItemFlag.valueOf(s.toUpperCase(Locale.ROOT)), placeholders).toArray(new ItemFlag[0]));
        }

        itemBuilder.name(PlaceholderFactory.handlePlaceholders(Collections.singletonList(name), MiniMessage.miniMessage()::deserialize, placeholders).get(0));

        for (ConfigItem.EnchantData value : configItem.getEnchants().values()) {
            String enchantName = value.getEnchant();
            String level = value.getLevel();

            if (!placeholders.isEmpty()) {
                enchantName = PlaceholderFactory.handlePlaceholders(Collections.singletonList(enchantName), placeholders).get(0);
                level = PlaceholderFactory.handlePlaceholders(Collections.singletonList(level), placeholders).get(0);
            }

            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.fromString(value.getEnchant()));
            int parsedLevel = UtilParse.parseInteger(level).orElse(1);

            if (enchantment == null) {
                continue;
            }

            itemBuilder.enchant(enchantment, parsedLevel);
        }

        for (Map.Entry<String, ConfigItem.NBTValue> nbtData : configItem.getNbt().entrySet()) {
            itemBuilder.updateItemMeta(itemMeta -> {
                PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
                addValue(persistentDataContainer, nbtData.getKey(), nbtData.getValue());
            });
        }

        return itemBuilder.build();
    }

    public static void addValue(PersistentDataContainer container, String key, ConfigItem.NBTValue value) {
        NamespacedKey namespacedKey = NamespacedKey.fromString(key);
        if (value.getType().equalsIgnoreCase("nbt")) {
            PersistentDataContainer subContainer = container.getAdapterContext().newPersistentDataContainer();

            for (Map.Entry<String, ConfigItem.NBTValue> entry : value.getSubData().entrySet()) {
                addValue(subContainer, entry.getKey(), entry.getValue());
            }

            container.set(namespacedKey, PersistentDataType.TAG_CONTAINER, subContainer);
            return;
        }

        switch (value.getType()) {
            case "int":
            case "integer":
                container.set(namespacedKey, PersistentDataType.INTEGER, Integer.parseInt(value.getData()));
                break;
            case "long":
                container.set(namespacedKey, PersistentDataType.LONG, Long.parseLong(value.getData()));
                break;
            case "byte":
                container.set(namespacedKey, PersistentDataType.BYTE, Byte.parseByte(value.getData()));
                break;
            case "double":
                container.set(namespacedKey, PersistentDataType.DOUBLE, Double.parseDouble(value.getData()));
                break;
            case "float":
                container.set(namespacedKey, PersistentDataType.FLOAT, Float.parseFloat(value.getData()));
                break;
            case "short":
                container.set(namespacedKey, PersistentDataType.SHORT, Short.parseShort(value.getData()));
                break;
            default:
            case "string":
                container.set(namespacedKey, PersistentDataType.STRING, value.getData());
                break;
        }
    }
}
