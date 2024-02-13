package com.envyful.api.player;

import com.envyful.api.player.save.SaveManager;

import java.util.concurrent.CompletableFuture;


/**
 *
 * An interface representing data stored about something, typically a player
 *
 * @param <A> The unique identifier type
 * @param <B> The platform player type
 */
@SuppressWarnings({"unused", "unchecked"})
public interface Attribute<A, B> {

    CompletableFuture<A> getId();

    default boolean shouldSave() {
        return true;
    }

    void load(A id);

    default void saveWithGenericId(Object object) throws ClassCastException {
        this.save((A) object);
    }

    void save(A id);

    void deleteAll(SaveManager<?> saveManager);

    static <A extends Attribute<B, C>, B, C> AttributeBuilder<A, B, C> builder(Class<A> attributeClass) {
        return new AttributeBuilder<A, B, C>().attributeClass(attributeClass);
    }

}
