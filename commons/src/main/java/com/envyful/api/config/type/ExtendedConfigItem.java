package com.envyful.api.config.type;

import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.envyful.api.type.Pair;
import com.envyful.api.type.UtilParse;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.*;

@ConfigSerializable
public class ExtendedConfigItem {

    private boolean enabled = true;
    private String type = "minecraft:stained_glass_pane";
    private String amount = "1";
    private String damage;
    private String name = " ";
    private List<String> flags = new ArrayList<>();
    private List<String> lore = new ArrayList<>();
    private Map<String, ConfigItem.EnchantData> enchants = new HashMap<>();
    private Map<String, ConfigItem.NBTValue> nbt = new HashMap<>();
    private Map<String, Pair<Integer, Integer>> positions = new HashMap<>();
    private boolean requiresPermission;
    private String permission;
    private ConfigItem elseItem;
    private boolean closeOnClick;
    private List<String> commandsExecuted;

    public ExtendedConfigItem() {
    }

    private ExtendedConfigItem(boolean enabled, String type,
                               String amount, String name, List<String> flags,
                               List<String> lore,
                               Map<String, ConfigItem.EnchantData> enchants,
                               Map<String, ConfigItem.NBTValue> nbt,
                               Map<String, Pair<Integer, Integer>> positions,
                               boolean requiresPermission,
                               String permission, ConfigItem elseItem,
                               boolean closeOnClick,
                               List<String> commandsExecuted) {
        this.enabled = enabled;
        this.type = type;
        this.amount = String.valueOf(amount);
        this.name = name;
        this.flags = flags;
        this.lore = lore;
        this.enchants = enchants;
        this.nbt = nbt;
        this.positions = positions;
        this.requiresPermission = requiresPermission;
        this.permission = permission;
        this.elseItem = elseItem;
        this.closeOnClick = closeOnClick;
        this.commandsExecuted = commandsExecuted;
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

    public Map<String, ConfigItem.EnchantData> getEnchants() {
        return enchants;
    }

    public List<String> getFlags() {
        return flags;
    }

    public Map<String, ConfigItem.NBTValue> getNbt() {
        return this.nbt;
    }

    public List<Pair<Integer, Integer>> getPositions() {
        return List.copyOf(this.positions.values());
    }

    public boolean requiresPermission() {
        return this.requiresPermission;
    }

    public String getPermission() {
        return this.permission;
    }

    public ConfigItem getElseItem() {
        return this.elseItem;
    }

    public boolean shouldCloseOnClick() {
        return this.closeOnClick;
    }

    public List<String> getCommandsExecuted() {
        return this.commandsExecuted;
    }

    public ConfigItem asConfigItem() {
        return new ConfigItem(enabled, type,
                amount, damage, name,
                flags, lore, enchants, nbt);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder copy(ExtendedConfigItem configItem) {
        var builder = builder()
                .enable(configItem.enabled)
                .type(configItem.type)
                .amount(configItem.amount)
                .name(configItem.name)
                .flags(configItem.flags.toArray(new String[0]))
                .lore(configItem.lore.toArray(new String[0]))
                .enchants(configItem.enchants.values().toArray(new ConfigItem.EnchantData[0]))
                .nbt(configItem.nbt)
                .positions(configItem.positions.values().toArray(new Pair[0]))
                .executeCommands(configItem.commandsExecuted.toArray(new String[0]));

        if (configItem.requiresPermission) {
            builder.requiresPermission(configItem.permission, configItem.elseItem);
        }

        if (builder.closeOnClick) {
            builder.closeOnClick();
        } else {
            builder.remainOpenOnClick();
        }

        return builder;
    }

    public static class Builder {

        private boolean enabled = true;
        private String type = "minecraft:stained_glass_pane";
        private String amount = "1";
        private String name = " ";
        private boolean requiresPermission = false;
        private String permission;
        private ConfigItem elseItem;
        private boolean closeOnClick = false;

        private final Map<String, ConfigItem.EnchantData> enchants = new HashMap<>();
        private final List<String> commandsExecuted = new ArrayList<>();
        private final List<String> flags = new ArrayList<>();
        private final List<String> lore = new ArrayList<>();
        private final Map<String, ConfigItem.NBTValue> nbt = new HashMap<>();
        private final Map<String, Pair<Integer, Integer>> positions = new HashMap<>();

        protected Builder() {
        }

        public Builder enable(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder enable() {
            this.enabled = true;
            return this;
        }

        public Builder disable() {
            this.enabled = false;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder amount(int amount) {
            return this.amount(amount + "");
        }

        public Builder amount(String amount) {
            this.amount = amount;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder flags(String... flags) {
            this.flags.addAll(List.of(flags));
            return this;
        }

        public Builder lore(String... lore) {
            this.lore.addAll(List.of(lore));
            return this;
        }

        public Builder enchants(ConfigItem.EnchantData... enchantData) {
            for (ConfigItem.EnchantData enchantDatum : enchantData) {
                this.enchants.put(this.enchants.size() + "", enchantDatum);
            }

            return this;
        }

        public Builder nbt(String key, ConfigItem.NBTValue value) {
            this.nbt.put(key, value);
            return this;
        }

        public Builder nbt(Map<String, ConfigItem.NBTValue> nbt) {
            this.nbt.putAll(nbt);
            return this;
        }

        @Deprecated
        @SafeVarargs
        public final Builder positions(Pair<Integer, Integer>... positions) {
            for (Pair<Integer, Integer> position : positions) {
                this.positions.put(this.positions.size() + "", position);
            }

            return this;
        }

        public Builder positions(int x, int y) {
            this.positions.put(this.positions.size() + "", Pair.of(x, y));
            return this;
        }

        public Builder noPermission() {
            this.requiresPermission = false;
            return this;
        }

        public Builder requiresPermission(
                String permission,
                ConfigItem elseItem) {
            this.requiresPermission = true;
            this.permission = permission;
            this.elseItem = elseItem;
            return this;
        }

        public Builder remainOpenOnClick() {
            this.closeOnClick = false;
            return this;
        }

        public Builder closeOnClick() {
            this.closeOnClick = true;
            return this;
        }

        public Builder executeCommands(String... commands) {
            this.commandsExecuted.addAll(List.of(commands));
            return this;
        }

        public ExtendedConfigItem build() {
            return new ExtendedConfigItem(
                    this.enabled, this.type, this.amount,
                    this.name, this.flags, this.lore, this.enchants,
                    this.nbt, this.positions, this.requiresPermission,
                    this.permission, this.elseItem, this.closeOnClick,
                    this.commandsExecuted);
        }
    }
}
