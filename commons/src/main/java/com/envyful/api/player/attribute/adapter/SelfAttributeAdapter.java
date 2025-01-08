package com.envyful.api.player.attribute.adapter;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.player.Attribute;

import java.util.concurrent.CompletableFuture;

/**
 *
 * An ease of use adapter for a self attribute that allows for easy porting of the old attribute
 * loading/saving system
 *
 */
public interface SelfAttributeAdapter extends AttributeAdapter<SelfAttributeAdapter>, Attribute {

    @Override
    default CompletableFuture<Void> save(SelfAttributeAdapter attribute) {
        return UtilConcurrency.runAsync(this::save);
    }

    void save();

    @Override
    default void load(SelfAttributeAdapter attribute) {
        this.load();
    }

    void load();

    @Override
    default CompletableFuture<Void> delete(SelfAttributeAdapter attribute) {
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
