package com.envyful.api.registry.config;

public class IdentityKeySerializer implements KeySerializer<String> {

    public String serialize(String key) {
        return key;
    }

    public String deserialize(String key) {
        return key;
    }

}
