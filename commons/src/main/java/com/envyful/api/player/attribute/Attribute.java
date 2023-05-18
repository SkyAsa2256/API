package com.envyful.api.player.attribute;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;


/**
 *
 * An interface representing data stored about something, typically a player
 *
 * @param <A> The unique identifier type
 * @param <B> The manager type
 */
@SuppressWarnings({"unused", "unchecked"})
public interface Attribute<A, B> {

    CompletableFuture<A> getId(UUID playerUuid);

    CompletableFuture<A> getId();

    default boolean isShared() {
        return false;
    }

    default boolean shouldSave() {
        return true;
    }

    default void loadWithGenericId(Object object) throws ClassCastException {
        this.load((A) object);
    }

    void load(A id);

    default void saveWithGenericId(Object object) throws ClassCastException {
        this.save((A) object);
    }

    void save(A id);

}
