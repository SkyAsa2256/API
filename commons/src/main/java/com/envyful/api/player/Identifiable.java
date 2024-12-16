package com.envyful.api.player;

import java.util.UUID;

/**
 *
 * Represents an object that has a unique identifier, and a (not necessarily unique) name
 *
 */
public interface Identifiable {

    /**
     *
     * Gets the unique identifier of the object
     *
     * @return The unique identifier
     */
    UUID getUniqueId();

    /**
     *
     * Gets the name of the object
     *
     * @return The name
     */
    String getName();

}
