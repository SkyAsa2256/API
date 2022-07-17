package com.envyful.api.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class ConfigLocation {

    private String worldName;
    private double posX;
    private double posY;
    private double posZ;
    private float pitch;
    private float yaw;

    public ConfigLocation(String worldName, double posX, double posY, double posZ, float pitch, float yaw) {
        this.worldName = worldName;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public ConfigLocation(String worldName, double posX, double posY, double posZ) {
        this(worldName, posX, posY, posZ, 0.0F, 0.0F);
    }

    public ConfigLocation() {
    }

    public String getWorldName() {
        return this.worldName;
    }

    public double getPosX() {
        return this.posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public double getPosZ() {
        return this.posZ;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }
}
