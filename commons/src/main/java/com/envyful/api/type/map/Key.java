package com.envyful.api.type.map;

import java.util.Objects;

public class Key<T> {

    private final String key;
    private final Class<T> valueType;

    public Key(String key, Class<T> valueType) {
        this.key = key;
        this.valueType = valueType;
    }

    public String getKey() {
        return this.key;
    }

    public Class<T> getValueType() {
        return this.valueType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key<?> key1 = (Key<?>) o;
        return Objects.equals(key, key1.key) && Objects.equals(valueType, key1.valueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, valueType);
    }
}
