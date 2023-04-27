package com.envyful.api.forge.player;

import com.envyful.api.config.ConfigLocation;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.world.UtilWorld;
import com.envyful.api.player.AbstractEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.save.SaveManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.UUID;

/**
 *
 * Forge implementation of the {@link EnvyPlayer} interface
 *
 */
public class ForgeEnvyPlayer extends AbstractEnvyPlayer<ServerPlayerEntity> {

    protected ForgeEnvyPlayer(SaveManager<ServerPlayerEntity> saveManager, ServerPlayerEntity player) {
        super(saveManager);

        this.parent = player;
    }

    @Override
    public UUID getUuid() {
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
                this.getParent().sendMessage(UtilChatColour.colour((String) message), Util.NIL_UUID);
            } else if (message instanceof ITextComponent) {
                this.getParent().sendMessage((ITextComponent) message, Util.NIL_UUID);
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
    public void executeCommands(String... commands) {
        for (String command : commands) {
            this.executeCommand(command);
        }
    }

    @Override
    public void executeCommand(String command) {
        ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(this.parent.createCommandSourceStack(), command);
    }

    @Override
    public void teleport(ConfigLocation location) {
        this.getParent().teleportTo((ServerWorld) UtilWorld.findWorld(location.getWorldName()),
                location.getPosX(), location.getPosY(), location.getPosZ(), (float)location.getPitch(), (float)location.getYaw());
    }
}
