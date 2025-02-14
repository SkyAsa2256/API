package com.envyful.api.forge.config;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class ConfigSound {

    private String sound;
    private transient ResourceLocation cachedSound = null;
    private float volume;
    private float pitch;
    private SoundCategory source;

    public ConfigSound(String sound, float volume, float pitch, SoundCategory source) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.source = source;
    }

    public ConfigSound(String sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.source = SoundCategory.MASTER;
    }

    public ConfigSound() {
    }

    public void playSound(ForgeEnvyPlayer... players) {
        if (this.cachedSound == null) {
            this.cachedSound = ResourceLocation.tryParse(this.sound);
        }

        if (this.cachedSound == null) {
            return;
        }

        for (var player : players) {
            player.getParent().connection.send(new SPlaySoundPacket(this.cachedSound, this.source,
                    new Vector3d(player.getParent().getX(), player.getParent().getY(), player.getParent().getZ()), 1.0f, 1.0f));
        }
    }

    public void playSound(ServerPlayerEntity... players) {
        if (this.cachedSound == null) {
            this.cachedSound = ResourceLocation.tryParse(this.sound);
        }

        if (this.cachedSound == null) {
            return;
        }

        for (ServerPlayerEntity player : players) {
            player.connection.send(new SPlaySoundPacket(this.cachedSound, this.source,
                    new Vector3d(player.getX(), player.getY(), player.getZ()), 1.0f, 1.0f));
        }
    }
}
