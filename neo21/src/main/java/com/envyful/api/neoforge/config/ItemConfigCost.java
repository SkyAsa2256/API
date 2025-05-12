package com.envyful.api.neoforge.config;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.text.Placeholder;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class ItemConfigCost implements ConfigCost {

    private ConfigItem configItem;
    private String failureMessage;

    public ItemConfigCost() {
    }

    public ItemConfigCost(ConfigItem configItem) {
        this.configItem = configItem;
    }

    @Override
    public boolean has(ServerPlayer player) {
        return player.getInventory().contains(UtilConfigItem.fromConfigItem(this.configItem));
    }

    @Override
    public void take(ServerPlayer player, Placeholder... placeholders) {
        player.getInventory().removeItem(UtilConfigItem.fromConfigItem(this.configItem));
    }

    @Override
    public String getFailureMessage() {
        return this.failureMessage;
    }
}
