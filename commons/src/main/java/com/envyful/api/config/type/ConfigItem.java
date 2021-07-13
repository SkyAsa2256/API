package com.envyful.api.config.type;

import com.google.common.collect.Lists;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

/**
 *
 * A serializable object that can be used to represent an Item in a config
 *
 */
@ConfigSerializable
public class ConfigItem {

    private String type = "minecraft:stained_glass_pane";
    private int amount = 1;
    private byte damage = 14;
    private String name = " ";
    private List<String> lore = Lists.newArrayList();

    public ConfigItem() {}

    public String getType() {
        return this.type;
    }

    public int getAmount() {
        return this.amount;
    }

    public byte getDamage() {
        return this.damage;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getLore() {
        return this.lore;
    }
}
