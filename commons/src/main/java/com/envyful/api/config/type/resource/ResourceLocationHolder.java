package com.envyful.api.config.type.resource;

public class ResourceLocationHolder {

    private String location;

    public ResourceLocationHolder() {
        this.location = "";
    }

    public ResourceLocationHolder(String location) {
        this.location = location;
    }

    public String getLocation() {
        return this.location;
    }
}
