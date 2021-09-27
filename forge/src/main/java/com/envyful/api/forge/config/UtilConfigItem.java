package com.envyful.api.forge.config;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.items.ItemBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class UtilConfigItem {

    public static ItemStack fromConfigItem(ConfigItem configItem) {
        ItemBuilder itemBuilder = new ItemBuilder()
                .type(Item.getByNameOrId(configItem.getType()))
                .amount(configItem.getAmount())
                .name(UtilChatColour.translateColourCodes('&', configItem.getName()))
                .damage(configItem.getDamage());

        for (String s : configItem.getLore()) {
            itemBuilder.addLore(UtilChatColour.translateColourCodes('&', s));
        }

        return itemBuilder.build();
    }

}
