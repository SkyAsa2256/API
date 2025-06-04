package com.envyful.api.config.serializer;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class CommentedConfigurationNodeSerializer implements TypeSerializer<CommentedConfigurationNode> {
    @Override
    public CommentedConfigurationNode deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return ((CommentedConfigurationNode) node);
    }

    @Override
    public void serialize(Type type, @Nullable CommentedConfigurationNode obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }

        node.set(obj);
    }
}
