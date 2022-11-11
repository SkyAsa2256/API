package com.envyful.api.forge.config;

import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class ConfigSound {

    private String sound;
    private transient SoundEvent cachedSound = null;
    private float volume;
    private float pitch;

    public ConfigSound(String sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public ConfigSound() {
    }

    public void playSound(ServerPlayer... players) {
        if (this.cachedSound == null) {
            this.cachedSound = new SoundEvent(new ResourceLocation(this.sound));
        }

        if (this.cachedSound == null) {
            return;
        }

        for (ServerPlayer player : players) {
            player.connection.send(new ClientboundSoundPacket(this.cachedSound, SoundSource.MUSIC, player.getX(), player.getY(), player.getZ(), 1.0f, 1.0f, 1));
        }
    }
}
