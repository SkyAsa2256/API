package com.envyful.api.forge.player;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.attribute.PlayerAttribute;
import com.google.common.collect.Maps;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * Forge implementation of the {@link EnvyPlayer} interface
 *
 */
public class ForgeEnvyPlayer implements EnvyPlayer<ServerPlayerEntity> {

    protected final Map<Class<?>, PlayerAttribute<?>> attributes = Maps.newHashMap();

    private ServerPlayerEntity player;

    protected ForgeEnvyPlayer(ServerPlayerEntity player) {
        this.player = player;
    }

    @Override
    public UUID getUuid() {
        return this.player.getUUID();
    }

    @Override
    public String getName() {
        return this.player.getName().getString();
    }

    @Override
    public ServerPlayerEntity getParent() {
        return this.player;
    }

    public void setPlayer(ServerPlayerEntity player) {
        this.player = player;
    }

    @Override
    public void message(String message) {
        this.player.sendMessage(new StringTextComponent(message), Util.NIL_UUID);
    }

    @Override
    public void message(String... messages) {
        for (String message : messages) {
            this.message(message);
        }
    }

    @Override
    public void message(List<String> messages) {
        for (String message : messages) {
            this.message(message);
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
        ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(this.player.createCommandSourceStack(), command);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends PlayerAttribute<B>, B> A getAttribute(Class<B> plugin) {
        return (A) this.attributes.get(plugin);
    }
}
