package com.envyful.api.type.map;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class KeyedMap {

    private final Map<String, Key<?>> map = new HashMap<>();

    public KeyedMap() {
    }

    public <T> void put(Key<T> key) {
        this.map.put(key.getKey(), key);
    }

    public <T> Key<T> get(String key) {
        return (Key<T>) this.map.get(key);
    }

    @Nullable
    public <T> T getNullable(String key) {
        var value = this.map.get(key);

        if (value == null) {
            return null;
        }

        return (T) value.getValue();
    }

}
