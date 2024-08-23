package com.envyful.api.registry.impl;

import com.envyful.api.registry.Registry;
import com.envyful.api.registry.config.KeySerializer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public abstract class MapBasedRegistry<A, B> implements Registry<A, B> {

    private final KeySerializer<A> keySerializer;
    private final Map<A, B> backing;

    protected MapBasedRegistry(KeySerializer<A> keySerializer, Supplier<Map<A, B>> backingSupplier) {
        this.keySerializer = keySerializer;
        this.backing = backingSupplier.get();
    }

    @Override
    public KeySerializer<A> keySerializer() {
        return this.keySerializer;
    }

    @Nullable
    @Override
    public B get(A key) {
        return this.backing.get(key);
    }

    @Override
    public A getKey(B value) {
        for (var entry : this.backing.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }

        return null;
    }

    @Override
    public void register(A key, B value) {
        this.backing.put(key, value);
    }

    @Override
    public void unregister(A key) {
        this.backing.remove(key);
    }

    @Override
    public void clear() {
        this.backing.clear();
    }

    @Override
    public List<B> values() {
        return new ArrayList<>(this.backing.values());
    }

    @Override
    public Set<A> keys() {
        return this.backing.keySet();
    }
}
