package com.envyful.api.spigot.config;

import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class ConfigReward {

    protected String displayName = "Example Display Name";
    protected List<String> commands;
    protected List<String> messages;

    public ConfigReward(List<String> commands, List<String> messages) {
        this.commands = commands;
        this.messages = messages;
    }

    public ConfigReward() {
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void execute(Player player, Placeholder... placeholders) {
        if (this.commands != null && !this.commands.isEmpty()) {
            for (String command : PlaceholderFactory.handlePlaceholders(this.commands, placeholders)) {
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("%player%", player.getName()));
            }
        }

        if (this.messages != null && !this.messages.isEmpty()) {
            for (String message : PlaceholderFactory.handlePlaceholders(this.messages, placeholders)) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(message).decoration(TextDecoration.ITALIC, false));
            }
        }
    }
}
