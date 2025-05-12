package com.envyful.api.config.type.resource;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class ResourceLocationHolderTypeSerializer implements TypeSerializer<ResourceLocationHolder> {

    private static final ResourceLocationHolderTypeSerializer INSTANCE = new ResourceLocationHolderTypeSerializer();

    public static ResourceLocationHolderTypeSerializer getInstance() {
        return INSTANCE;
    }

    @Override
    public ResourceLocationHolder deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.getString() != null) {
            return new ResourceLocationHolder(node.getString());
        }

        return new ResourceLocationHolder(node.node("location").getString());
    }

    @Override
    public void serialize(Type type, @Nullable ResourceLocationHolder obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }

        node.raw(obj.getLocation());
    }
}
