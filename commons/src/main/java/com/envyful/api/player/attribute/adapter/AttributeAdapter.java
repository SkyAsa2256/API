package com.envyful.api.player.attribute.adapter;

import com.envyful.api.player.Attribute;

/**
 *
 * Interface for handling the serialization of attributes
 *
 * @param <A> The attribute type
 * @param <B> The player type
 */
public interface AttributeAdapter<A extends Attribute<B>, B> {

    /**
     *
     * Saves the attribute
     *
     * @param attribute The attribute being saved
     */
    void save(A attribute);

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
    void delete(A attribute);

    /**
     *
     * Deletes all attributes
     *
     */
    void deleteAll();

    /**
     *
     * Called on start up to initialize the adapter.
     * <br>
     * This is where you would create tables, etc
     *
     */
    void initialize();

}
