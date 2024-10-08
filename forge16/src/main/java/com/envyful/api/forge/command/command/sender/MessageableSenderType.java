package com.envyful.api.forge.command.command.sender;

import com.envyful.api.command.sender.SenderType;
import com.envyful.api.forge.platform.ConsoleMessageable;
import com.envyful.api.platform.Messageable;
import net.minecraft.command.ICommandSource;

public class MessageableSenderType implements SenderType<ICommandSource, Messageable> {
    
    @Override
    public Class<?> getType() {
        return Messageable.class;
    }

    @Override
    public boolean isAccepted(ICommandSource sender) {
        return true;
    }

    @Override
    public Messageable getInstance(ICommandSource sender) {
        return new ConsoleMessageable(sender);
    }
}
