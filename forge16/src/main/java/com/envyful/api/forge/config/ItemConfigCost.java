package com.envyful.api.forge.config;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.text.Placeholder;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

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
        PlatformProxy.sendMessage(player, List.of(this.successMessage), placeholders);
    }

    @Override
    public String getFailureMessage() {
        return this.failureMessage;
    }
}
