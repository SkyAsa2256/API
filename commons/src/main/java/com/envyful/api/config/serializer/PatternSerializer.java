package com.envyful.api.config.serializer;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.regex.Pattern;

public class PatternSerializer implements TypeSerializer<Pattern> {

    private static final PatternSerializer INSTANCE = new PatternSerializer();

    private PatternSerializer() {}

    public static PatternSerializer get() {
        return INSTANCE;
    }

    @Override
    public Pattern deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var value = node.getString();
        return Pattern.compile(value);
    }

    @Override
    public void serialize(Type type, @Nullable Pattern obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        node.set(obj.pattern());
    }
}
