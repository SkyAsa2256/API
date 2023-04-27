package com.envyful.api.velocity.player;

import com.envyful.api.config.ConfigLocation;
import com.envyful.api.player.AbstractEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.save.SaveManager;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

import java.util.UUID;

/**
 *
 * Velocity implementation of the {@link EnvyPlayer} interface
 *
 */
public class VelocityEnvyPlayer extends AbstractEnvyPlayer<Player> {

    private final ProxyServer proxy;
    private final UUID uuid;

    protected VelocityEnvyPlayer(SaveManager<Player> saveManager, ProxyServer proxy, UUID uuid) {
        super(saveManager);

        this.proxy = proxy;
        this.uuid = uuid;
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.parent.getUsername();
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
        this.proxy.getCommandManager().executeAsync(this.getParent(), command);
    }

    @Override
    public void teleport(ConfigLocation location) {
        throw new UnsupportedOperationException("Cannot teleport players on the proxy!");
    }
}
