package com.envyful.api.player.attribute;

import com.envyful.api.player.EnvyPlayer;

import java.util.UUID;

/**
 *
 * An interface designed for storing specific
 * data for each mod / plugin about a player.
 *
 *
 * @param <A> The manager type
 * @param <B> The API's player type
 * @param <C> The platform's player type
 */
public abstract class PlayerAttribute<A, B extends EnvyPlayer<C>, C>
        extends ManagedAttribute<UUID, A> {

    protected transient B parent;

    protected PlayerAttribute(UUID id, A manager) {
        super(id, manager);
    }

    public void setParent(B parent) {
        this.parent = parent;
    }
}
