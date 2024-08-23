package com.envyful.api.registry.config;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.registry.Registry;
import com.envyful.api.type.ExceptionThrowingBiFunction;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.function.Function;

public class RegistryTypeSerializer<A, B, C> implements TypeSerializer<C> {

    private final Registry<A, B> registry;
    private final ExceptionThrowingBiFunction<B, ConfigurationNode, C, SerializationException> converter;
    private final Function<C, B> inverter;
    private final KeySerializer<A> keySerializer;

    public RegistryTypeSerializer(Registry<A, B> registry, ExceptionThrowingBiFunction<B, ConfigurationNode, C, SerializationException> converter, Function<C, B> inverter, KeySerializer<A> keySerializer) {
        this.registry = registry;
        this.converter = converter;
        this.inverter = inverter;
        this.keySerializer = keySerializer;
    }


    @Override
    public C deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.raw() == null) {
            return null;
        }

        var id = node.node("id").getString();
        var expectedType = this.registry.get(this.keySerializer.deserialize(id));

        if (expectedType == null) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Invalid id provided: {}", id));
            return null;
        }

        return this.converter.get(expectedType, node);
    }

    @Override
    public void serialize(Type type, @Nullable C obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }

        var id = registry.getKey(inverter.apply(obj));
        node.node("id").set(id);
        this.save(obj, node);
    }

    @SuppressWarnings("unchecked")
    private void save(C value, ConfigurationNode target) throws SerializationException {
        Class<C> clazz = (Class<C>) value.getClass();
        ObjectMapper.factory().get(clazz).save(value, target);
    }
}
