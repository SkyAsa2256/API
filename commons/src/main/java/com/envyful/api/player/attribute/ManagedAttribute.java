package com.envyful.api.player.attribute;

import com.envyful.api.player.Attribute;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 *
 * Abstract implementation of the attribute class storing the manager
 * and id of the attribute
 *
 * @param <B> The manager instance for the attribute
 */
public abstract class ManagedAttribute<B> implements Attribute, Serializable {

    private static final long ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);

    protected final transient UUID id;
    protected final transient B manager;
    protected transient long lastSave = -1L;

    protected ManagedAttribute(UUID id, B manager) {
        this.id = id;
        this.manager = manager;
    }

    @Override
    public UUID getUniqueId() {
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
