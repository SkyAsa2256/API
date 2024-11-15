package com.envyful.api.player.attribute;

/**
 *
 * Abstract implementation of the attribute class storing the manager
 * and id of the attribute
 *
 * @param <A> The attribute ID type
 * @param <B> The manager instance for the attribute
 */
public abstract class SharedAttribute<A, B> extends ManagedAttribute<A, B> {

    protected SharedAttribute(A id, B manager) {
        super(id, manager);
    }
}
