package com.envyful.api.config;

import com.envyful.api.config.type.ConfigItem;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 *
 * A config representation of a toast.
 *
 */
@ConfigSerializable
public class ConfigToast {

    private String message;
    private ConfigItem item;

    @Comment("TASK, CHALLENGE, or GOAL")
    private String type;

    public ConfigToast() {
    }

    public ConfigToast(String message, ConfigItem item, String type) {
        this.message = message;
        this.item = item;
        this.type = type;
    }

    public String getMessage() {
        return this.message;
    }

    public ConfigItem getItem() {
        return this.item;
    }

    public String getType() {
        return this.type;
    }
}
