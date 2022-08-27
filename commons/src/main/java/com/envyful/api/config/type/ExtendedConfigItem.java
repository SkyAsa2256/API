package com.envyful.api.config.type;

import com.envyful.api.type.Pair;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Map;

@ConfigSerializable
public class ExtendedConfigItem extends ConfigItem {

    private Map<String, Pair<Integer, Integer>> positions = Maps.newHashMap();
    private boolean requiresPermission;
    private String permission;
    private ConfigItem elseItem;
    private boolean closeOnClick;
    private List<String> commandsExecuted;

    public ExtendedConfigItem() {
        super();
    }

    public ExtendedConfigItem(String type, int amount, byte damage, String name, List<String> lore, int xPos,
                              int yPos, Map<String, NBTValue> nbt) {
        super(type, amount, damage, name, lore, nbt);

        this.closeOnClick = false;
        this.requiresPermission = false;
        this.positions.put("first", Pair.of(xPos, yPos));
        this.commandsExecuted = Lists.newArrayList();
    }
    public ExtendedConfigItem(String type, int amount, String name, List<String> lore, int xPos,
                              int yPos, Map<String, NBTValue> nbt) {
        super(type, amount, (byte) 0, name, lore, nbt);

        this.closeOnClick = false;
        this.requiresPermission = false;
        this.positions.put("first", Pair.of(xPos, yPos));
        this.commandsExecuted = Lists.newArrayList();
    }

    public ExtendedConfigItem(boolean enabled, String type, String amount, String name, List<String> flags,
                              List<String> lore, Map<String, EnchantData> enchants, Map<String, NBTValue> nbt,
                              Map<String, Pair<Integer, Integer>> positions, boolean requiresPermission,
                              String permission, ConfigItem elseItem, boolean closeOnClick,
                              List<String> commandsExecuted) {
        super(enabled, type, amount, name, flags, lore, enchants, nbt);

        this.positions = positions;
        this.requiresPermission = requiresPermission;
        this.permission = permission;
        this.elseItem = elseItem;
        this.closeOnClick = closeOnClick;
        this.commandsExecuted = commandsExecuted;
    }

    public List<Pair<Integer, Integer>> getPositions() {
        return Lists.newArrayList(this.positions.values());
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private boolean enabled = true;
        private String type = "minecraft:stained_glass_pane";
        private String amount = "1";
        private String name = " ";
        private List<String> flags = Lists.newArrayList();
        private List<String> lore = Lists.newArrayList();
        private Map<String, EnchantData> enchants = Maps.newHashMap();
        private Map<String, NBTValue> nbt = Maps.newHashMap();
        private Map<String, Pair<Integer, Integer>> positions = Maps.newHashMap();
        private boolean requiresPermission = false;
        private String permission;
        private ConfigItem elseItem;
        private boolean closeOnClick = false;
        private List<String> commandsExecuted = Lists.newArrayList();

        protected Builder() {}

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
            this.flags.addAll(Lists.newArrayList(flags));
            return this;
        }

        public Builder lore(String... lore) {
            this.lore.addAll(Lists.newArrayList(lore));
            return this;
        }

        public Builder enchants(EnchantData... enchantData) {
            for (EnchantData enchantDatum : enchantData) {
                this.enchants.put(this.enchants.size() + "", enchantDatum);
            }

            return this;
        }

        public Builder nbt(NBTValue... nbtData) {
            for (NBTValue nbtDatum : nbtData) {
                this.nbt.put(this.nbt.size() + "", nbtDatum);
            }

            return this;
        }

        @SafeVarargs
        public final Builder positions(Pair<Integer, Integer>... positions) {
            for (Pair<Integer, Integer> position : positions) {
                this.positions.put(this.positions.size() + "", position);
            }

            return this;
        }

        public Builder noPermission() {
            this.requiresPermission = false;
            return this;
        }

        public Builder requiresPermission(String permission, ConfigItem elseItem) {
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
            this.commandsExecuted.addAll(Lists.newArrayList(commands));
            return this;
        }

        public ExtendedConfigItem build() {
            return new ExtendedConfigItem(this.enabled, this.type, this.amount, this.name, this.flags, this.lore, this.enchants,
                    this.nbt, this.positions, this.requiresPermission, this.permission, this.elseItem, this.closeOnClick,
                    this.commandsExecuted);
        }
    }
}
