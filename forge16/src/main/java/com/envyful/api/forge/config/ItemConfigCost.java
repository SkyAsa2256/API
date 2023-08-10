package com.envyful.api.forge.config;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;

@ConfigSerializable
public class ItemConfigCost implements ConfigCost {

    private ConfigItem configItem;
    private String failureMessage;
    private String successMessage;

    public ItemConfigCost(ConfigItem configItem, String failureMessage, String successMessage) {
        this.configItem = configItem;
        this.failureMessage = failureMessage;
        this.successMessage = successMessage;
    }

    @Override
    public boolean has(ServerPlayerEntity player) {
        return player.inventory.contains(UtilConfigItem.fromConfigItem(this.configItem));
    }

    @Override
    public void take(ServerPlayerEntity player, Placeholder... placeholders) {
        player.inventory.removeItem(UtilConfigItem.fromConfigItem(this.configItem));

        for (String handlePlaceholder : PlaceholderFactory.handlePlaceholders(Collections.singletonList(this.successMessage), placeholders)) {
            player.sendMessage(UtilChatColour.colour(handlePlaceholder), Util.NIL_UUID);
        }
    }

    @Override
    public String getFailureMessage() {
        return this.failureMessage;
    }
}
