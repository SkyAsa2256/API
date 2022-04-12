package com.envyful.api.config.type;

import com.envyful.api.gui.Transformer;
import com.envyful.api.type.UtilParse;
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
    private String amount = "1";
    private String damage = "14";
    private String name = " ";
    private List<String> flags = Lists.newArrayList();
    private List<String> lore = Lists.newArrayList();
    private Map<String, EnchantData> enchants = Maps.newHashMap();
    private Map<String, NBTValue> nbt = Maps.newHashMap();

    public ConfigItem() {}

    public ConfigItem(String type, int amount, byte damage, String name, List<String> lore, Map<String, EnchantData> enchants, Map<String, NBTValue> nbt) {
        this.type = type;
        this.amount = amount + "";
        this.damage = damage + "";
        this.name = name;
        this.lore = lore;
        this.enchants = enchants;
        this.nbt = nbt;
    }

    public ConfigItem(String type, int amount, byte damage, String name, List<String> lore, Map<String, NBTValue> nbt) {
        this(type, amount, damage, name, lore, Maps.newHashMap(), nbt);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getType() {
        return this.type;
    }

    public int getAmount() {
        return UtilParse.parseInteger(this.amount).orElse(0);
    }

    public int getAmount(List<Transformer> transformers) {
        String amount = this.amount;
        for (Transformer transformer : transformers) {
            amount = transformer.transformName(amount);
        }

        return UtilParse.parseInteger(amount).orElse(0);
    }

    public byte getDamage() {
        return (byte) (int) UtilParse.parseInteger(this.damage).orElse(0);
    }

    public byte getDamage(List<Transformer> transformers) {
        String damage = this.damage;
        for (Transformer transformer : transformers) {
            damage = transformer.transformName(damage);
        }

        return (byte) (int) UtilParse.parseInteger(damage).orElse(0);
    }

    public String getName() {
        return this.name;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public Map<String, EnchantData> getEnchants() {
        return enchants;
    }

    public List<String> getFlags() {
        return flags;
    }

    public Map<String, NBTValue> getNbt() {
        return this.nbt;
    }

    @ConfigSerializable
    public static final class NBTValue {

        private String type;
        private String data;
        private Map<String, NBTValue> subData;

        public NBTValue() {}

        public NBTValue(String type, String data) {
            this.type = type;
            this.data = data;
        }

        public NBTValue(String type, Map<String, NBTValue> subData) {
            this.type = type;
            this.subData = subData;
        }

        public String getType() {
            return this.type;
        }

        public String getData() {
            return this.data;
        }

        public Map<String, NBTValue> getSubData() {
            return subData;
        }
    }

    @ConfigSerializable
    public static final class EnchantData {

        private String enchant;
        private String level;

        public EnchantData() {}

        public EnchantData(String enchant, String level) {
            this.enchant = enchant;
            this.level = level;
        }

        public String getEnchant() {
            return this.enchant;
        }

        public String getLevel() {
            return this.level;
        }
    }
}
