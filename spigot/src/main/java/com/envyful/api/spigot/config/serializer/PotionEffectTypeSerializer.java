package com.envyful.api.spigot.config.serializer;

import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class PotionEffectTypeSerializer implements TypeSerializer<PotionEffect> {

    public PotionEffectTypeSerializer() {
    }

    @Override
    public PotionEffect deserialize(Type type, ConfigurationNode node) {
        return new PotionEffect(
                PotionEffectType.getByKey(NamespacedKey.fromString(node.node("type").getString())),
                node.node("duration").getInt(),
                node.node("amplifier").getInt(),
                node.node("ambient").getBoolean(),
                node.node("particles").getBoolean(),
                node.node("icon").getBoolean()
        );
    }

    @Override
    public void serialize(Type type, @Nullable PotionEffect obj, ConfigurationNode node) throws SerializationException {
        node.node("type").set(obj.getType().getKey().toString());
        node.node("duration").set(obj.getDuration());
        node.node("amplifier").set(obj.getAmplifier());
        node.node("ambient").set(obj.isAmbient());
        node.node("particles").set(obj.hasParticles());
        node.node("icon").set(obj.hasIcon());
    }
}
