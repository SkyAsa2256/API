package com.envyful.api.forge.command.command;

import com.google.common.collect.Maps;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.Map;

/**
 *
 * Enum for identifying what type of executor a command has from the class of the sender
 *
 */
public enum ForgeSenderType {

    CONSOLE(ICommandSource.class),
    PLAYER(ServerPlayerEntity.class, PlayerEntity.class),

    ;

    private static final Map<Class<?>, ForgeSenderType> SENDERS = Maps.newHashMap();

    static {
        for (ForgeSenderType value : values()) {
            for (Class<?> clazz : value.clazz) {
                SENDERS.put(clazz, value);
            }
        }
    }

    private final Class<?>[] clazz;

    ForgeSenderType(Class<?>... clazz) {
        this.clazz = clazz;
    }

    public Class<?> getType() {
        return this.clazz[0];
    }

    public static ForgeSenderType get(Class<?> clazz) {
        return SENDERS.get(clazz);
    }
}
