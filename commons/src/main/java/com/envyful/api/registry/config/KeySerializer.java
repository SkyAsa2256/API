package com.envyful.api.registry.config;

public interface KeySerializer<A> {

    String serialize(A key);

    A deserialize(String key);

    static KeySerializer<String> identity() {
        return new IdentityKeySerializer();
    }

}
