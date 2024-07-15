package com.envyful.api.forge.config;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.forge.items.ItemFlag;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.envyful.api.type.Pair;
import com.envyful.api.type.UtilParse;
import com.google.common.collect.Lists;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

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

    public static void addExtendedConfigItem(Pane pane, ForgeEnvyPlayer player, ExtendedConfigItem configItem, Placeholder... transformers) {
        builder().extendedConfigItem(player, pane, configItem, transformers);
    }

    public static ItemStack fromPermissibleItem(ServerPlayer player, ExtendedConfigItem permissibleConfigItem, Placeholder... transformers) {
        return fromPermissibleItem(player, permissibleConfigItem, Lists.newArrayList(transformers));
    }

    public static ItemStack fromPermissibleItem(ServerPlayer player, ExtendedConfigItem permissibleConfigItem, List<Placeholder> transformers) {
        if (!permissibleConfigItem.isEnabled()) {
            return null;
        }

        if (hasPermission(player, permissibleConfigItem)) {
            return fromConfigItem(permissibleConfigItem, transformers);
        }

        if (permissibleConfigItem.getElseItem() == null || !permissibleConfigItem.getElseItem().isEnabled()) {
            return null;
        }

        return fromConfigItem(permissibleConfigItem.getElseItem(), transformers);
    }

    public static boolean hasPermission(ServerPlayer player, ExtendedConfigItem permissibleConfigItem) {
        return !permissibleConfigItem.requiresPermission() ||
                permissibleConfigItem.getPermission().isEmpty() ||
                permissibleConfigItem.getPermission() == null ||
                permissibleConfigItem.getPermission().equalsIgnoreCase("none") ||
                UtilPlayer.hasPermission(player, permissibleConfigItem.getPermission());
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
                .type(fromNameOrId(PlaceholderFactory.handlePlaceholders(configItem.getType(), placeholders).get(0)))
                .amount(configItem.getAmount(placeholders));

        itemBuilder.lore(PlaceholderFactory.handlePlaceholders(configItem.getLore(), UtilChatColour::colour, placeholders));
        itemBuilder.itemFlags(PlaceholderFactory.handlePlaceholders(configItem.getFlags(), s -> ItemFlag.valueOf(s.toUpperCase(Locale.ROOT)), placeholders));

        if (!name.isBlank() && !name.isEmpty()) {
            itemBuilder.name(PlaceholderFactory.handlePlaceholders(Collections.singletonList(name), UtilChatColour::colour, placeholders).get(0));
        }

        for (ConfigItem.EnchantData value : configItem.getEnchants().values()) {
            String enchantName = value.getEnchant();
            String level = value.getLevel();

            if (!placeholders.isEmpty()) {
                enchantName = PlaceholderFactory.handlePlaceholders(Collections.singletonList(enchantName), placeholders).get(0);
                level = PlaceholderFactory.handlePlaceholders(Collections.singletonList(level), placeholders).get(0);
            }

            Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(new ResourceLocation(enchantName.toLowerCase()));
            int parsedLevel = UtilParse.parseInt(level).orElse(1);

            if (enchantment == null) {
                continue;
            }

            itemBuilder.enchant(enchantment, parsedLevel);
        }

        for (Map.Entry<String, ConfigItem.NBTValue> nbtData : configItem.getNbt().entrySet()) {
            Pair<String, Tag> parsed = parseNBT(nbtData, placeholders);

            if (parsed != null) {
                itemBuilder.nbt(parsed.getX(), parsed.getY());
            }
        }

        return itemBuilder.build();
    }

    public static Pair<String, Tag> parseNBT(Map.Entry<String, ConfigItem.NBTValue> nbtEntry, List<Placeholder> placeholders) {
        if (nbtEntry.getValue().getType().equalsIgnoreCase("nbt")) {
            CompoundTag compound = new CompoundTag();

            for (Map.Entry<String, ConfigItem.NBTValue> entry : nbtEntry.getValue().getSubData().entrySet()) {
                Pair<String, Tag> parsed = parseNBT(entry, placeholders);

                if (parsed != null) {
                    compound.put(parsed.getX(), parsed.getY());
                }
            }

            return Pair.of(nbtEntry.getKey(), compound);
        }

        if (nbtEntry.getValue().getType().equalsIgnoreCase("list")) {
            ListTag list = new ListTag();

            for (Map.Entry<String, ConfigItem.NBTValue> nbtValue : nbtEntry.getValue().getSubData().entrySet()) {
                Pair<String, Tag> parsed = parseNBT(nbtValue, placeholders);

                if (parsed != null) {
                    CompoundTag compound = new CompoundTag();
                    compound.put(parsed.getX(), parsed.getY());
                    list.add(compound);
                }
            }

            return Pair.of(nbtEntry.getKey(), list);
        }

        return Pair.of(nbtEntry.getKey(), parseBasic(nbtEntry.getValue(), placeholders));
    }

    public static Tag parseBasic(ConfigItem.NBTValue value, List<Placeholder> placeholders) {
        String data = value.getData();

        if (!placeholders.isEmpty()) {
            data = PlaceholderFactory.handlePlaceholders(Collections.singletonList(data), placeholders).get(0);
        }

        Tag base;

        switch (value.getType().toLowerCase()) {
            case "int":
            case "integer":
                base = IntTag.valueOf(Integer.parseInt(data));
                break;
            case "long":
                base = LongTag.valueOf(Long.parseLong(data));
                break;
            case "byte":
                base = ByteTag.valueOf(Byte.parseByte(data));
                break;
            case "double":
                base = DoubleTag.valueOf(Double.parseDouble(data));
                break;
            case "float":
                base = FloatTag.valueOf(Float.parseFloat(data));
                break;
            case "short":
                base = ShortTag.valueOf(Short.parseShort(data));
                break;
            default:
            case "string":
                base = StringTag.valueOf(data);
                break;
        }

        return base;
    }

    public static Item fromNameOrId(String data) {
        try {
            Item item = BuiltInRegistries.ITEM.getOptional(new ResourceLocation(data)).orElse(null);

            if (item != null) {
                return item;
            }

            int integer = UtilParse.parseInt(data).orElse(-1);

            if (integer == -1) {
                UtilLogger.logger().ifPresent(logger -> logger.error("Invalid item type provided: " + data));
                return null;
            }

            return Item.byId(integer);
        } catch (ResourceLocationException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Invalid item type provided: " + data));
            return null;
        }
    }

}
