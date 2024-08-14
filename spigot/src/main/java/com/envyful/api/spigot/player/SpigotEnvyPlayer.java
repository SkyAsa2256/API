package com.envyful.api.spigot.player;

import com.envyful.api.config.ConfigLocation;
import com.envyful.api.player.AbstractEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.save.SaveManager;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 *
 * Spigot implementation of the {@link EnvyPlayer} interface
 *
 */
public class SpigotEnvyPlayer extends AbstractEnvyPlayer<Player> {

    protected final UUID uuid;

    protected SpigotEnvyPlayer(SaveManager<Player> saveManager, UUID uuid) {
        super(saveManager);

        this.uuid = uuid;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.parent.getName();
    }

    @Override
    public void message(Object... messages) {
        for (Object message : messages) {
            if (message instanceof Component) {
                this.getParent().sendMessage((Component) message);
            } else if (message instanceof String) {
                //TODO: convert
            }
        }
    }

    @Override
    public void actionBar(String message, Placeholder... placeholders) {
        var converted = PlaceholderFactory.handlePlaceholders(message, placeholders);

        if (converted.isEmpty()) {
            return;
        }

        this.getParent().sendActionBar(MiniMessage.miniMessage().deserialize(converted.get(0)));
    }

    @Override
    public void actionBar(Object message) {
        if (message instanceof Component component) {
            this.getParent().sendActionBar(component);
        } else if (message instanceof String string) {
            this.actionBar(string, new Placeholder[0]);
        } else {
            throw new RuntimeException("Unsupported message type");
        }
    }

    @Override
    public void executeCommands(String... commands) {
        for (String command : commands) {
            this.executeCommand(command);
        }
    }

    @Override
    public void executeCommand(String command) {
        this.getParent().performCommand(command);
    }

    @Override
    public void teleport(ConfigLocation location) {
        //TODO:
    }
}
