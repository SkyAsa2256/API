package com.envyful.api.spigot.config;

import com.envyful.api.config.type.ConfigItem;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class DisplayableConfigReward extends ConfigReward {

    protected ConfigItem displayItem;

    public DisplayableConfigReward(List<String> commands, List<String> messages, ConfigItem displayItem) {
        super(commands, messages);

        this.displayItem = displayItem;
    }

    public DisplayableConfigReward() {
    }

    public ConfigItem getDisplayableItem() {
        return this.displayItem;
    }
}
