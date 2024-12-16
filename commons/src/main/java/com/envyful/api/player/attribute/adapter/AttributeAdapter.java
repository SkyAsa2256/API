package com.envyful.api.player.attribute.adapter;

import com.envyful.api.player.Attribute;

import java.util.concurrent.CompletableFuture;

/**
 *
 * Interface for handling the serialization of attributes
 *
 * @param <A> The attribute type
 */
public interface AttributeAdapter<A extends Attribute> {

    /**
     *
     * Saves the attribute
     *
     * @param attribute The attribute being saved
     */
    CompletableFuture<Void> save(A attribute);

    /**
     *
     * Loads the attribute
     *
     * @param attribute The attribute being loaded
     */
    void load(A attribute);

    /**
     *
     * Deletes the attribute
     *
     * @param attribute The attribute being deleted
     */
    CompletableFuture<Void> delete(A attribute);

    /**
     *
     * Deletes all attributes
     *
     */
    CompletableFuture<Void> deleteAll();

    /**
     *
     * Called on start up to initialize the adapter.
     * <br>
     * This is where you would create tables, etc
     *
     */
    void initialize();

}
