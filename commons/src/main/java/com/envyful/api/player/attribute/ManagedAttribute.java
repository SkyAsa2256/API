package com.envyful.api.player.attribute;

import com.envyful.api.player.Attribute;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 *
 * Abstract implementation of the attribute class storing the manager
 * and id of the attribute
 *
 * @param <A> The attribute ID type
 * @param <B> The manager instance for the attribute
 */
public abstract class ManagedAttribute<A, B> implements Attribute<A>, Serializable {

    private static final long ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);

    protected final transient A id;
    protected final transient B manager;
    protected transient long lastSave = -1L;

    protected ManagedAttribute(A id, B manager) {
        this.id = id;
        this.manager = manager;
    }

    @Override
    public A getId() {
        return this.id;
    }

    @Override
    public boolean shouldSave() {
        if (this.lastSave == -1L) {
            return true;
        }

        return (System.currentTimeMillis() - this.lastSave) >= ONE_MINUTE;
    }
}
