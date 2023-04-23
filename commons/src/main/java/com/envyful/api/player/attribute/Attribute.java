package com.envyful.api.player.attribute;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public interface Attribute<IdType, Manager> {

    CompletableFuture<IdType> getId(UUID playerUuid);

    CompletableFuture<IdType> getId();

    default boolean isShared() {
        return false;
    }

    default void loadWithGenericId(Object object) throws ClassCastException {
        this.load((IdType) object);
    }

    void load(IdType id);

    default void saveWithGenericId(Object object) throws ClassCastException {
        this.load((IdType) object);
    }

    void save(IdType id);

}
