package com.envyful.api.forge.config;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.PermissibleConfigItem;
import com.envyful.api.config.type.PositionableConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.api.gui.Transformer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class UtilConfigItem {

    public static void addPermissibleConfigItem(Pane pane, EntityPlayerMP player, List<Transformer> transformers, PermissibleConfigItem configItem) {
        addPermissibleConfigItem(pane, player, configItem, transformers,null);
    }

    public static void addPermissibleConfigItem(Pane pane, EntityPlayerMP player, PermissibleConfigItem configItem) {
        addPermissibleConfigItem(pane, player, configItem, null);
    }

    public static void addPermissibleConfigItem(Pane pane, EntityPlayerMP player, PermissibleConfigItem configItem,
                                                BiConsumer<EnvyPlayer<?>, Displayable.ClickType> clickHandler) {
        addPermissibleConfigItem(pane, player, configItem, Collections.emptyList(), clickHandler);
    }

    public static void addPermissibleConfigItem(Pane pane, EntityPlayerMP player, PermissibleConfigItem configItem,
                                                List<Transformer> transformers,
                                                BiConsumer<EnvyPlayer<?>, Displayable.ClickType> clickHandler) {
        ItemStack itemStack = fromPermissibleItem(player, configItem, transformers);

        if (itemStack == null) {
            return;
        }

        if (clickHandler == null) {
            pane.set(configItem.getXPos(), configItem.getYPos(), GuiFactory.displayable(itemStack));
        } else {
            pane.set(configItem.getXPos(), configItem.getYPos(), GuiFactory.displayableBuilder(itemStack)
                    .clickHandler(clickHandler).build());
        }
    }

    public static void addConfigItem(Pane pane, PositionableConfigItem configItem) {
        addConfigItem(pane, configItem, null);
    }

    public static void addConfigItem(Pane pane, List<Transformer> transformers, PositionableConfigItem configItem) {
        addConfigItem(pane, configItem, transformers,null);
    }

    public static void addConfigItem(Pane pane, PositionableConfigItem configItem,
                                     BiConsumer<EnvyPlayer<?>, Displayable.ClickType> clickHandler) {
        addConfigItem(pane, configItem, Collections.emptyList(), clickHandler);
    }

    public static void addConfigItem(Pane pane, PositionableConfigItem configItem, List<Transformer> transformers,
                                     BiConsumer<EnvyPlayer<?>, Displayable.ClickType> clickHandler) {
        if (!configItem.isEnabled()) {
            return;
        }

        if (clickHandler == null) {
            pane.set(configItem.getXPos(), configItem.getYPos(), GuiFactory.displayable(fromConfigItem(
                    configItem,
                    transformers
            )));
        } else {
            pane.set(configItem.getXPos(), configItem.getYPos(), GuiFactory.displayableBuilder(fromConfigItem(
                    configItem,
                    transformers
            )).clickHandler(clickHandler).build());
        }
    }

    public static ItemStack fromPermissibleItem(EntityPlayerMP player, PermissibleConfigItem permissibleConfigItem) {
        return fromPermissibleItem(player, permissibleConfigItem, Collections.emptyList());
    }

    public static ItemStack fromPermissibleItem(EntityPlayerMP player, PermissibleConfigItem permissibleConfigItem, List<Transformer> transformers) {
        if (!permissibleConfigItem.isEnabled()) {
            return null;
        }

        if (permissibleConfigItem.getPermission().isEmpty() || UtilPlayer.hasPermission(player,
                                                                                        permissibleConfigItem.getPermission())) {
            return fromConfigItem(permissibleConfigItem);
        }

        if (permissibleConfigItem.getElseItem() == null || !permissibleConfigItem.getElseItem().isEnabled()) {
            return null;
        }

        return fromConfigItem(permissibleConfigItem.getElseItem());
    }

    public static ItemStack fromConfigItem(ConfigItem configItem) {
        return fromConfigItem(configItem, Collections.emptyList());
    }

    public static ItemStack fromConfigItem(ConfigItem configItem, List<Transformer> transformers) {
        if (!configItem.isEnabled()) {
            return null;
        }

        String name = configItem.getName();

        ItemBuilder itemBuilder = new ItemBuilder()
                .type(Item.getByNameOrId(configItem.getType()))
                .amount(configItem.getAmount())
                .damage(configItem.getDamage());

        List<String> lore = configItem.getLore();

        if (!transformers.isEmpty()) {
            for (Transformer transformer : transformers) {
                lore = transformer.transformLore(lore);
                name = transformer.transformName(name);
            }
        }

        for (String s : lore) {
            itemBuilder.addLore(UtilChatColour.translateColourCodes('&', s));
        }

        itemBuilder.name(UtilChatColour.translateColourCodes('&', name));

        for (Map.Entry<String, ConfigItem.NBTValue> nbtData : configItem.getNbt().entrySet()) {
            String data = nbtData.getValue().getData();

            if (!transformers.isEmpty()) {
                for (Transformer transformer : transformers) {
                    data = transformer.transformName(data);
                }
            }

            NBTBase base = null;
            switch (nbtData.getValue().getType().toLowerCase()) {
                case "int" : case "integer" :
                    base = new NBTTagInt(Integer.parseInt(data));
                    break;
                case "long" :
                    base = new NBTTagLong(Long.parseLong(data));
                    break;
                case "byte" :
                    base = new NBTTagByte(Byte.parseByte(data));
                    break;
                case "double" :
                    base = new NBTTagDouble(Double.parseDouble(data));
                    break;
                case "float" :
                    base = new NBTTagFloat(Float.parseFloat(data));
                    break;
                case "short" :
                    base = new NBTTagShort(Short.parseShort(data));
                    break;
                default : case "string" :
                    base = new NBTTagString(data);
                    break;
            }

            itemBuilder.nbt(nbtData.getKey(), base);
        }

        return itemBuilder.build();
    }

}
