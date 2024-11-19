package com.envyful.api.player.attribute.adapter;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.player.Attribute;

import java.util.concurrent.CompletableFuture;

/**
 *
 * An ease of use adapter for a self attribute that allows for easy porting of the old attribute
 * loading/saving system
 *
 * @param <A> The type of the attribute
 */
public interface SelfAttributeAdapter<A> extends AttributeAdapter<SelfAttributeAdapter<A>, A>, Attribute<A> {

    @Override
    default CompletableFuture<Void> save(SelfAttributeAdapter<A> attribute) {
        return UtilConcurrency.runAsync(this::save);
    }

    void save();

    @Override
    default void load(SelfAttributeAdapter<A> attribute) {
        this.load();
    }

    void load();

    @Override
    default CompletableFuture<Void> delete(SelfAttributeAdapter<A> attribute) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    default CompletableFuture<Void> deleteAll() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    default void initialize() {

    }
}
