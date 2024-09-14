package com.envyful.api.type.map;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class KeyedMap {

    private final Map<Key<?>, Object> map = new HashMap<>();

    public KeyedMap() {
    }

    public <T> void put(Key<T> key, T value) {
        this.map.put(key, value);
    }

    @Nullable
    public <T> T get(Key<T> key) {
        if (!this.map.containsKey(key)) {
            return null;
        }

        return (T) this.map.get(key);
    }

}
