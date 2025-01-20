package com.envyful.api.config;

import com.envyful.api.config.serializer.PatternSerializer;
import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.util.regex.Pattern;

/**
 *
 * A class used to register and store the serializers for different types
 *
 * @param <T> The type of the serializer
 */
public class ConfigTypeSerializer<T> {

    static {
        register(PatternSerializer.get(), Pattern.class);
    }

    private final Class<T> clazz;
    private final TypeSerializer<T> serializer;
    private final TypeToken<T> type;

    private ConfigTypeSerializer(TypeSerializer<T> serializer, Class<T> clazz, TypeToken<T> type) {
        this.serializer = serializer;
        this.clazz = clazz;
        this.type = type;
    }

    public Class<T> clazz() {
        return this.clazz;
    }

    public TypeSerializer<T> serializer() {
        return this.serializer;
    }

    public TypeToken<T> type() {
        return this.type;
    }

    public static <T> ConfigTypeSerializer<T> register(TypeSerializer<T> serializer, Class<T> clazz) {
        return register(serializer, clazz, TypeToken.get(clazz));
    }

    public static <T> ConfigTypeSerializer<T> register(TypeSerializer<T> serializer, Class<T> clazz, TypeToken<T> type) {
        var value = new ConfigTypeSerializer<>(serializer, clazz, type);
        ConfigTypeSerializerRegistry.register(value);
        return value;
    }
}
