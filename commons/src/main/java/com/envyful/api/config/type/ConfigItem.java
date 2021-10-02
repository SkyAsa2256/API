package com.envyful.api.config.type;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Map;

/**
 *
 * A serializable object that can be used to represent an Item in a config
 *
 */
@ConfigSerializable
public class ConfigItem {

    private boolean enabled = true;
    private String type = "minecraft:stained_glass_pane";
    private int amount = 1;
    private byte damage = 14;
    private String name = " ";
    private List<String> lore = Lists.newArrayList();
    private Map<String, NBTValue> nbt = Maps.newHashMap();

    public ConfigItem() {}

    public ConfigItem(String type, int amount, byte damage, String name, List<String> lore, Map<String, NBTValue> nbt) {
        this.type = type;
        this.amount = amount;
        this.damage = damage;
        this.name = name;
        this.lore = lore;
        this.nbt = nbt;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

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

    public Map<String, NBTValue> getNbt() {
        return this.nbt;
    }

    @ConfigSerializable
    public static final class NBTValue {

        private String type;
        private String data;

        public NBTValue() {}

        public NBTValue(String type, String data) {
            this.type = type;
            this.data = data;
        }

        public String getType() {
            return this.type;
        }

        public String getData() {
            return this.data;
        }
    }
}
