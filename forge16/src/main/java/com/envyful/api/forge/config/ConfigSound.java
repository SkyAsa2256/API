package com.envyful.api.forge.config;

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

    public ConfigSound(String sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public ConfigSound() {
    }

    public void playSound(ServerPlayerEntity... players) {
        if (this.cachedSound == null) {
            this.cachedSound = new ResourceLocation(this.sound);
        }

        for (ServerPlayerEntity player : players) {
            player.connection.send(new SPlaySoundPacket(this.cachedSound, SoundCategory.MUSIC,
                    new Vector3d(player.getX(), player.getY(), player.getZ()), 1.0f, 1.0f));
        }
    }
}
