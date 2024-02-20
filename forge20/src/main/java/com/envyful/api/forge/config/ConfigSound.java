package com.envyful.api.forge.config;

import net.minecraft.core.Holder;
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
    private SoundSource source;

    public ConfigSound(String sound, float volume, float pitch, SoundSource source) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.source = source;
    }

    public ConfigSound(String sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.source = SoundSource.MASTER;
    }

    public ConfigSound() {
    }

    public void playSound(ServerPlayer... players) {
        if (this.cachedSound == null) {
            this.cachedSound = SoundEvent.createVariableRangeEvent(ResourceLocation.tryParse(this.sound));
        }

        if (this.cachedSound == null) {
            return;
        }

        for (ServerPlayer player : players) {
            player.connection.send(new ClientboundSoundPacket(Holder.direct(this.cachedSound), this.source, player.getX(), player.getY(), player.getZ(), 1.0f, 1.0f, 1));
        }
    }
}
