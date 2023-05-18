package com.envyful.api.player.attribute;

import java.io.Serializable;

/**
 *
 * Abstract implementation of the attribute class storing the manager
 * and id of the attribute
 *
 * @param <A> The attribute ID type
 * @param <B> The manager instance for the attribute
 */
public abstract class AbstractAttribute<A, B>
        implements Attribute<A, B>, Serializable {

    protected final transient B manager;

    protected transient A id;

    protected AbstractAttribute(B manager) {
        this.manager = manager;
    }

    protected abstract void load();

    protected abstract void save();

}
