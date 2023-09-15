package com.envyful.api.forge.config;

import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.server.UtilForgeServer;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.google.common.collect.Lists;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class ConfigReward {

    protected String displayName = "Example Display Name";
    protected ExtendedConfigItem displayItem;
    protected List<String> commands;
    protected List<String> messages;

    @Deprecated
    public ConfigReward(List<String> commands, List<String> messages) {
        this.commands = commands;
        this.messages = messages;
    }

    protected ConfigReward(Builder builder) {
        this.displayName = builder.displayName;
        this.displayItem = builder.displayItem;
        this.commands = builder.commands;
        this.messages = builder.messages;
    }

    public ConfigReward() {
    }

    public ExtendedConfigItem getDisplayItem() {
        return this.displayItem;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void execute(ServerPlayer player, Placeholder... placeholders) {
        if (this.commands != null && !this.commands.isEmpty()) {
            for (String command : PlaceholderFactory.handlePlaceholders(this.commands, placeholders)) {
                UtilForgeServer.executeCommand(command.replace("%player%", player.getName().getString()));
            }
        }

        if (this.messages != null && !this.messages.isEmpty()) {
            for (String message : PlaceholderFactory.handlePlaceholders(this.messages, placeholders)) {
                player.sendSystemMessage(UtilChatColour.colour(message));
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        protected String displayName = "Example Display Name";
        protected ExtendedConfigItem displayItem;
        protected List<String> commands = Lists.newArrayList();
        protected List<String> messages = Lists.newArrayList();

        protected Builder() {}

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder displayItem(ExtendedConfigItem displayItem) {
            this.displayItem = displayItem;
            return this;
        }

        public Builder commands(List<String> commands) {
            this.commands.addAll(commands);
            return this;
        }

        public Builder messages(List<String> messages) {
            this.messages.addAll(messages);
            return this;
        }

        public ConfigReward build() {
            return new ConfigReward(this);
        }
    }
}
