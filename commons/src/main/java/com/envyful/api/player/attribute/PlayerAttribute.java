package com.envyful.api.player.attribute;

/**
 *
 * An interface designed for storing specific data for each mod / plugin about a player.
 *
 * All implementations should stick to only keeping functions visible that operate on this object (i.e. no public getters
 * or setters) to follow SOLID rules.
 *
 * @param <A> The manager class
 */
public interface PlayerAttribute<A> {

    /**
     *
     * Implementation for loading the data for the player attribute
     *
     */
    void load();

    /**
     *
     * Implementation for saving the data from the player attribute
     *
     */
    void save();

}
