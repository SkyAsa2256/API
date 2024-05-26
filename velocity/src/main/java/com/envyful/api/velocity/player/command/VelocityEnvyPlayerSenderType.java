package com.envyful.api.velocity.player.command;

import com.envyful.api.command.sender.SenderType;
import com.envyful.api.velocity.player.VelocityEnvyPlayer;
import com.envyful.api.velocity.player.VelocityPlayerManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

/**
 *
 * Velocity implementation of the {@link SenderType} interface for the {@link VelocityEnvyPlayer} class
 *
 */
public class VelocityEnvyPlayerSenderType implements SenderType<CommandSource, VelocityEnvyPlayer> {

    private final VelocityPlayerManager playerManager;

    public VelocityEnvyPlayerSenderType(VelocityPlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public Class<?> getType() {
        return VelocityEnvyPlayer.class;
    }

    @Override
    public boolean isAccepted(CommandSource sender) {
        return sender instanceof Player;
    }

    @Override
    public VelocityEnvyPlayer getInstance(CommandSource sender) {
        return this.playerManager.getPlayer(((Player) sender).getUniqueId());
    }
}
