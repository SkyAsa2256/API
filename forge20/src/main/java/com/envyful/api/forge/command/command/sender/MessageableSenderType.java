package com.envyful.api.forge.command.command.sender;

import com.envyful.api.command.sender.SenderType;
import com.envyful.api.forge.platform.ConsoleMessageable;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.platform.Messageable;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.level.ServerPlayer;

public class MessageableSenderType implements SenderType<CommandSource, Messageable<?>> {

    private final ForgePlayerManager playerManager;

    public MessageableSenderType(ForgePlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public Class<?> getType() {
        return Messageable.class;
    }

    @Override
    public boolean isAccepted(CommandSource sender) {
        return true;
    }

    @Override
    public Messageable<?> getInstance(CommandSource sender) {
        if (this.playerManager != null && sender instanceof ServerPlayer player) {
            return this.playerManager.getPlayer(player);
        }

        return new ConsoleMessageable(sender);
    }
}
