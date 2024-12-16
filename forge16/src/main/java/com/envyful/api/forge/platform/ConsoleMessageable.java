package com.envyful.api.forge.platform;

import com.envyful.api.platform.Messageable;
import com.envyful.api.platform.PlatformProxy;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;

import java.util.List;
import java.util.UUID;

/**
 *
 * A {@link Messageable} implementation for the console
 *
 */
public class ConsoleMessageable implements Messageable<ICommandSource> {

    private final ICommandSource source;

    public ConsoleMessageable(ICommandSource source) {
        this.source = source;
    }

    @Override
    public ICommandSource getParent() {
        return this.source;
    }

    @Override
    public void message(Object... messages) {
        for (Object message : messages) {
            if (message instanceof String) {
                this.source.sendMessage(PlatformProxy.parse((String) message), Util.NIL_UUID);
            } else if (message instanceof ITextComponent) {
                this.source.sendMessage((ITextComponent) message, Util.NIL_UUID);
            } else if (message instanceof List) {
                for (Object subMessage : ((List) message)) {
                    this.message(subMessage);
                }
            } else {
                throw new RuntimeException("Unsupported message type");
            }
        }
    }

    @Override
    public UUID getUniqueId() {
        return Util.NIL_UUID;
    }

    @Override
    public String getName() {
        return "Console";
    }
}
