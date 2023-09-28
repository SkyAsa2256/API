package com.envyful.api.forge.config;

import com.envyful.api.math.Vector;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class ConfigParticle {

    protected String particleType;
    protected transient IParticleData cachedType;
    protected int amount;
    protected Vector distance;
    protected float maxSpeed;

    public ConfigParticle() {
    }

    public ConfigParticle(Builder builder) {
        this.particleType = builder.particleType;
        this.amount = builder.amount;
        this.distance = builder.distance;
        this.maxSpeed = builder.maxSpeed;
    }

    public void spawnParticles(ServerWorld world, BlockPos pos) {
        world.sendParticles(this.getCachedType(),
                pos.getX(), pos.getY(), pos.getZ(),
                this.amount,
                this.distance.getX(), this.distance.getY(), this.distance.getZ(), this.maxSpeed);
    }

    private IParticleData getCachedType() {
        if (this.cachedType == null) {
            this.cachedType = (IParticleData) Registry.PARTICLE_TYPE.get(new ResourceLocation(this.particleType));
        }

        return this.cachedType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        protected String particleType;
        protected int amount;
        protected Vector distance;
        protected float maxSpeed;

        protected Builder() {
        }

        public Builder particleType(IParticleData particleType) {
            this.particleType = particleType.getType().getRegistryName().toString();
            return this;
        }

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder distance(Vector distance) {
            this.distance = distance;
            return this;
        }

        public Builder maxSpeed(float maxSpeed) {
            this.maxSpeed = maxSpeed;
            return this;
        }

        public ConfigParticle build() {
            return new ConfigParticle(this);
        }
    }
}
