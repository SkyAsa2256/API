package com.envyful.api.forge.command;

import com.envyful.api.command.sender.SenderType;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.ForgePlayerManager;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.level.ServerPlayer;

public class ForgeEnvyPlayerSenderType implements SenderType<CommandSource, ForgeEnvyPlayer> {

    private final ForgePlayerManager playerManager;

    public ForgeEnvyPlayerSenderType(ForgePlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public Class<?> getType() {
        return ForgeEnvyPlayer.class;
    }

    @Override
    public boolean isAccepted(CommandSource sender) {
        return sender instanceof ServerPlayer;
    }

    @Override
    public ForgeEnvyPlayer getInstance(CommandSource sender) {
        return this.playerManager.getPlayer(((ServerPlayer) sender).getUUID());
    }
}
