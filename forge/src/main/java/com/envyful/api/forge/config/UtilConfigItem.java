package com.envyful.api.forge.config;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.PermissibleConfigItem;
import com.envyful.api.config.type.PositionableConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;

import java.util.Map;
import java.util.function.BiConsumer;

public class UtilConfigItem {

    public static void addPermissibleConfigItem(Pane pane, EntityPlayerMP player, PermissibleConfigItem configItem) {
        addPermissibleConfigItem(pane, player, configItem, null);
    }

    public static void addPermissibleConfigItem(Pane pane, EntityPlayerMP player, PermissibleConfigItem configItem,
                                                BiConsumer<EnvyPlayer<?>, Displayable.ClickType> clickHandler) {
        ItemStack itemStack = fromPermissibleItem(player, configItem);

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

    public static void addConfigItem(Pane pane, PositionableConfigItem configItem,
                                     BiConsumer<EnvyPlayer<?>, Displayable.ClickType> clickHandler) {
        if (clickHandler == null) {
            pane.set(configItem.getXPos(), configItem.getYPos(), GuiFactory.displayable(fromConfigItem(configItem)));
        } else {
            pane.set(configItem.getXPos(), configItem.getYPos(), GuiFactory.displayableBuilder(fromConfigItem(configItem))
                    .clickHandler(clickHandler).build());
        }
    }

    public static ItemStack fromPermissibleItem(EntityPlayerMP player, PermissibleConfigItem permissibleConfigItem) {
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
        if (!configItem.isEnabled()) {
            return null;
        }

        ItemBuilder itemBuilder = new ItemBuilder()
                .type(Item.getByNameOrId(configItem.getType()))
                .amount(configItem.getAmount())
                .name(UtilChatColour.translateColourCodes('&', configItem.getName()))
                .damage(configItem.getDamage());

        for (String s : configItem.getLore()) {
            itemBuilder.addLore(UtilChatColour.translateColourCodes('&', s));
        }

        for (Map.Entry<String, ConfigItem.NBTValue> nbtData : configItem.getNbt().entrySet()) {
            NBTBase base = null;
            switch (nbtData.getValue().getType().toLowerCase()) {
                case "int" : case "integer" :
                    base = new NBTTagInt(Integer.parseInt(nbtData.getValue().getData()));
                    break;
                case "long" :
                    base = new NBTTagLong(Long.parseLong(nbtData.getValue().getData()));
                    break;
                case "byte" :
                    base = new NBTTagByte(Byte.parseByte(nbtData.getValue().getData()));
                    break;
                case "double" :
                    base = new NBTTagDouble(Double.parseDouble(nbtData.getValue().getData()));
                    break;
                case "float" :
                    base = new NBTTagFloat(Float.parseFloat(nbtData.getValue().getData()));
                    break;
                case "short" :
                    base = new NBTTagShort(Short.parseShort(nbtData.getValue().getData()));
                    break;
                default : case "string" :
                    base = new NBTTagString(nbtData.getValue().getData());
                    break;
            }

            itemBuilder.nbt(nbtData.getKey(), base);
        }

        return itemBuilder.build();
    }

}
