package com.envyful.api.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 *
 * A config representation of a location.
 *
 */
@ConfigSerializable
public class ConfigLocation {

    private String worldName;
    private double posX;
    private double posY;
    private double posZ;
    private double pitch;
    private double yaw;

    protected ConfigLocation(Builder builder) {
        this.worldName = builder.worldName;
        this.posX = builder.posX;
        this.posY = builder.posY;
        this.posZ = builder.posZ;
        this.pitch = builder.pitch;
        this.yaw = builder.yaw;
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

    public double getPitch() {
        return this.pitch;
    }

    public double getYaw() {
        return this.yaw;
    }

    /**
     *
     * Creates a new {@link Builder} instance.
     *
     * @return The new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     *
     * A builder for the {@link ConfigLocation} class.
     *
     */
    public static class Builder {

        private String worldName;
        private double posX;
        private double posY;
        private double posZ;
        private double pitch;
        private double yaw;

        protected Builder() {
        }

        public Builder worldName(String worldName) {
            this.worldName = worldName;
            return this;
        }

        public Builder posX(double posX) {
            this.posX = posX;
            return this;
        }

        public Builder posY(double posY) {
            this.posY = posY;
            return this;
        }

        public Builder posZ(double posZ) {
            this.posZ = posZ;
            return this;
        }

        public Builder pitch(double pitch) {
            this.pitch = pitch;
            return this;
        }

        public Builder yaw(double yaw) {
            this.yaw = yaw;
            return this;
        }

        public ConfigLocation build() {
            return new ConfigLocation(this);
        }

    }
}
