package com.envyful.api.spigot.config;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class ConfigSound {

    private Sound sound;
    private float volume;
    private float pitch;
    private SoundCategory source;

    public ConfigSound(Sound sound, float volume, float pitch, SoundCategory source) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.source = source;
    }

    public ConfigSound(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.source = SoundCategory.MASTER;
    }

    public ConfigSound() {
    }

    public void playSound(Player... players) {
        for (var player : players) {
            player.playSound(player.getLocation(), this.sound, this.source, this.volume, this.pitch);
        }
    }
}
