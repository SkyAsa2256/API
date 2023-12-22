package com.envyful.api.spigot.player;

import com.envyful.api.config.ConfigLocation;
import com.envyful.api.player.AbstractEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.save.SaveManager;
import net.kyori.adventure.text.Component;
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
    public UUID getUuid() {
        return this.uuid;
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
