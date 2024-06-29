package com.envyful.api.player.attribute;

import com.envyful.api.player.Attribute;

import java.io.Serializable;

/**
 *
 * Abstract implementation of the attribute class storing the manager
 * and id of the attribute
 *
 * @param <A> The attribute ID type
 * @param <B> The manager instance for the attribute
 */
public abstract class ManagedAttribute<A, B>
        implements Attribute<A>, Serializable {

    protected final transient B manager;

    protected transient A id;

    protected ManagedAttribute(B manager) {
        this.manager = manager;
    }

    protected abstract void load();

    protected abstract void save();

}
