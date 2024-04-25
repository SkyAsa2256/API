package com.envyful.api.config;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class ConfigTypeSerializerRegistry {

    private static final Map<Class<?>, ConfigTypeSerializer<?>> REGISTRY = Maps.newHashMap();

    public static void register(ConfigTypeSerializer<?> serializer) {
        REGISTRY.put(serializer.clazz(), serializer);
    }

    public static ConfigTypeSerializer<?> get(Class<?> id) {
        return REGISTRY.get(id);
    }

    public static List<ConfigTypeSerializer<?>> getAll() {
        return List.copyOf(REGISTRY.values());
    }
}
