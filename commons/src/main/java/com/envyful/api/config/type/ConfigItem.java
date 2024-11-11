package com.envyful.api.config.type;

import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.envyful.api.type.UtilParse;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.*;

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
    private String damage;
    private String name = " ";
    private List<String> flags = new ArrayList<>();
    private List<String> lore = new ArrayList<>();
    private Map<String, EnchantData> enchants = new HashMap<>();
    private Map<String, NBTValue> nbt = new HashMap<>();

    public ConfigItem() {}

    ConfigItem(boolean enabled, String type, String amount,
               String damage, String name, List<String> flags,
               List<String> lore, Map<String, EnchantData> enchants,
               Map<String, NBTValue> nbt) {
        this.enabled = enabled;
        this.type = type;
        this.amount = amount;
        this.damage = damage;
        this.name = name;
        this.flags = flags;
        this.lore = lore;
        this.enchants = enchants;
        this.nbt = nbt;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getType() {
        return this.type;
    }

    public int getAmount() {
        return UtilParse.parseInt(this.amount).orElse(0);
    }

    public int getAmount(List<Placeholder> placeholders) {
        List<Integer> integers = PlaceholderFactory.handlePlaceholders(
                Collections.singletonList(amount),
                s -> UtilParse.parseInt(s).orElse(0),
                placeholders);

        if (integers.isEmpty()) {
            return 0;
        }

        return integers.get(0);
    }

    public byte getDamage() {
        return (byte) UtilParse.parseInt(this.damage).orElse(0);
    }

    public byte getDamage(List<Placeholder> placeholders) {
        List<Integer> integers = PlaceholderFactory.handlePlaceholders(
                Collections.singletonList(damage),
                s -> UtilParse.parseInt(s).orElse(0),
                placeholders);

        if (integers.isEmpty()) {
            return 0;
        }

        return (byte) integers.get(0).intValue();
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

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ConfigItem configItem) {
        return new Builder()
                .type(configItem.type)
                .amount(configItem.getAmount())
                .lore(configItem.lore.toArray(new String[0]))
                .name(configItem.name)
                .flags(configItem.flags.toArray(new String[0]))
                .enchants(configItem.enchants.values().toArray(new EnchantData[0]));
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

    public static class Builder {

        private boolean enabled = true;
        private String type = "minecraft:stained_glass_pane";
        private String amount = "1";
        private String damage;
        private String name = " ";
        private List<String> flags = new ArrayList<>();
        private List<String> lore = new ArrayList<>();
        private Map<String, EnchantData> enchants = new HashMap<>();
        private Map<String, NBTValue> nbt = new HashMap<>();

        protected Builder() {}

        public Builder enabled() {
            this.enabled = true;
            return this;
        }

        public Builder disabled() {
            this.enabled = false;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder amount(int amount) {
            this.amount = String.valueOf(amount);
            return this;
        }

        public Builder damage(double damage) {
            this.damage = String.valueOf(damage);
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder flags(String... flags) {
            this.flags.addAll(Arrays.asList(flags));
            return this;
        }

        public Builder clearLore() {
            this.lore.clear();
            return this;
        }

        public Builder setLore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public Builder setLore(String... lore) {
            this.lore = new ArrayList<>();
            this.lore.addAll(List.of(lore));
            return this;
        }

        public Builder lore(String... lore) {
            this.lore.addAll(List.of(lore));
            return this;
        }

        public Builder enchants(EnchantData... enchants) {
            for (EnchantData enchant : enchants) {
                this.enchants.put("enchant-" + this.enchants.size(), enchant);
            }

            return this;
        }

        public Builder nbt(String key, NBTValue value) {
            this.nbt.put(key, value);
            return this;
        }

        public ConfigItem build() {
            return new ConfigItem(this.enabled, this.type,
                    this.amount, this.damage, this.name,
                    this.flags, this.lore, this.enchants,
                    this.nbt);
        }
    }
}
