package com.envyful.api.registry.impl;

import com.envyful.api.registry.config.KeySerializer;
import com.envyful.api.registry.config.RegistryTypeSerializer;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.util.Map;
import java.util.function.Supplier;

public class StandardMapBasedRegistry<A, B> extends MapBasedRegistry<A, B> {

    public StandardMapBasedRegistry(KeySerializer<A> keySerializer, Supplier<Map<A, B>> backingSupplier) {
        super(keySerializer, backingSupplier);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TypeSerializer<B> getTypeSerializer() {
        return new RegistryTypeSerializer<>(this,
                (c, configurationNode) -> (B) ObjectMapper.factory().get(c.getClass()).load(configurationNode),
                b -> b, this.keySerializer());
    }
}
