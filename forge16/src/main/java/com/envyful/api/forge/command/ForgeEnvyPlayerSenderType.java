package com.envyful.api.forge.command;

import com.envyful.api.command.sender.SenderType;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.ForgePlayerManager;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;

public class ForgeEnvyPlayerSenderType implements SenderType<ICommandSource, ForgeEnvyPlayer> {

    private final ForgePlayerManager playerManager;

    public ForgeEnvyPlayerSenderType(ForgePlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public Class<?> getType() {
        return ForgeEnvyPlayer.class;
    }

    @Override
    public boolean isAccepted(ICommandSource sender) {
        return sender instanceof ServerPlayerEntity;
    }

    @Override
    public ForgeEnvyPlayer getInstance(ICommandSource sender) {
        return this.playerManager.getPlayer(((ServerPlayerEntity) sender).getUUID());
    }
}
