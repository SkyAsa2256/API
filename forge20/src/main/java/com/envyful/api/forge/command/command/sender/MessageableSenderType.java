package com.envyful.api.forge.command.command.sender;

import com.envyful.api.command.sender.SenderType;
import com.envyful.api.forge.platform.ConsoleMessageable;
import com.envyful.api.platform.Messageable;
import net.minecraft.commands.CommandSource;

public class MessageableSenderType implements SenderType<CommandSource, Messageable> {
    
    @Override
    public Class<?> getType() {
        return Messageable.class;
    }

    @Override
    public boolean isAccepted(CommandSource sender) {
        return true;
    }

    @Override
    public Messageable getInstance(CommandSource sender) {
        return new ConsoleMessageable(sender);
    }
}
