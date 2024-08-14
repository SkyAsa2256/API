package com.envyful.api.forge.player;

import com.envyful.api.config.ConfigLocation;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.world.UtilWorld;
import com.envyful.api.player.AbstractEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.save.SaveManager;
import com.envyful.api.text.Placeholder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.UUID;

/**
 *
 * Forge implementation of the {@link EnvyPlayer} interface
 *
 */
public class ForgeEnvyPlayer extends AbstractEnvyPlayer<ServerPlayer> {

    protected ForgeEnvyPlayer(SaveManager<ServerPlayer> saveManager, ServerPlayer player) {
        super(saveManager);

        this.parent = player;
    }

    @Override
    public UUID getUniqueId() {
        return this.parent.getUUID();
    }

    @Override
    public String getName() {
        return this.parent.getName().getString();
    }

    @Override
    public void message(Object... messages) {
        for (Object message : messages) {
            if (message instanceof String) {
                this.getParent().sendSystemMessage(UtilChatColour.colour((String) message));
            } else if (message instanceof Component) {
                this.getParent().sendSystemMessage((Component) message);
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
    public void actionBar(String message, Placeholder... placeholders) {
        this.actionBar(UtilChatColour.colour(message, placeholders));
    }

    @Override
    public void actionBar(Object message) {
        if (message instanceof String string) {
            this.actionBar(string, new Placeholder[0]);
        } else if (message instanceof Component component) {
            this.getParent().sendSystemMessage(component, true);
        } else {
            throw new RuntimeException("Unsupported message type");
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
        ServerLifecycleHooks.getCurrentServer().getCommands().performPrefixedCommand(this.parent.createCommandSourceStack(), command);
    }

    @Override
    public void teleport(ConfigLocation location) {
        this.getParent().teleportTo((ServerLevel) UtilWorld.findWorld(location.getWorldName()),
                location.getPosX(), location.getPosY(), location.getPosZ(), (float)location.getPitch(), (float)location.getYaw());
    }
}
