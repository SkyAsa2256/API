package com.envyful.api.registry.impl;

import com.envyful.api.registry.config.KeySerializer;
import com.envyful.api.registry.config.RegistryTypeSerializer;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.util.Map;
import java.util.function.Supplier;

public class ClassValueMapBasedRegistry<A, B, C extends Class<B>> extends MapBasedRegistry<A, C> {

    public ClassValueMapBasedRegistry(KeySerializer<A> keySerializer, Supplier<Map<A, C>> backingSupplier) {
        super(keySerializer, backingSupplier);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TypeSerializer<B> getTypeSerializer() {
        return new RegistryTypeSerializer<>(this,
                (c, configurationNode) -> ObjectMapper.factory().get(c).load(configurationNode),
                b -> (C) b.getClass(), this.keySerializer());
    }
}
