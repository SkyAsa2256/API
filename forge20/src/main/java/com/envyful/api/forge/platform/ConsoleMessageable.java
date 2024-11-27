package com.envyful.api.forge.platform;

import com.envyful.api.platform.Messageable;
import com.envyful.api.platform.PlatformProxy;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 *
 * A {@link Messageable} implementation for the console
 *
 */
public class ConsoleMessageable implements Messageable<CommandSource> {

    private final CommandSource source;

    public ConsoleMessageable(CommandSource source) {
        this.source = source;
    }

    @Override
    public CommandSource getParent() {
        return this.source;
    }

    @Override
    public void message(Object... messages) {
        for (Object message : messages) {
            if (message instanceof String) {
                this.source.sendSystemMessage(PlatformProxy.parse((String) message));
            } else if (message instanceof Component) {
                this.source.sendSystemMessage((Component) message);
            } else if (message instanceof List) {
                for (Object subMessage : ((List) message)) {
                    this.message(subMessage);
                }
            } else {
                throw new RuntimeException("Unsupported message type");
            }
        }
    }
}
