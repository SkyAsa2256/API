package com.envyful.api.forge.command.command.sender;

import com.envyful.api.command.sender.SenderType;
import com.envyful.api.forge.platform.ConsoleMessageable;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.platform.Messageable;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;

public class MessageableSenderType implements SenderType<ICommandSource, Messageable<?>> {

    private final ForgePlayerManager playerManager;

    public MessageableSenderType(ForgePlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public Class<?> getType() {
        return Messageable.class;
    }

    @Override
    public boolean isAccepted(ICommandSource sender) {
        return true;
    }

    @Override
    public Messageable<?> getInstance(ICommandSource sender) {
        if (this.playerManager != null && sender instanceof ServerPlayerEntity) {
            return this.playerManager.getPlayer((ServerPlayerEntity) sender);
        }

        return new ConsoleMessageable(sender);
    }
}
