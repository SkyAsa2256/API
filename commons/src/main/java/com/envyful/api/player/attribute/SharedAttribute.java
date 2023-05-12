package com.envyful.api.player.attribute;

import com.envyful.api.player.PlayerManager;

/**
 *
 * Abstract implementation of the attribute class storing the manager
 * and id of the attribute
 *
 * @param <A> The attribute ID type
 * @param <B> The manager instance for the attribute
 */
public abstract class SharedAttribute<A, B> extends AbstractAttribute<A, B> {

    protected final transient PlayerManager<?, ?> playerManager;

    protected A id;

    protected SharedAttribute(B manager, PlayerManager<?, ?> playerManager) {
        super(manager);

        this.playerManager = playerManager;
    }

    @Override
    public boolean isShared() {
        return true;
    }

    @Override
    public void load(A id) {
        this.id = id;

        this.load();
    }

    @Override
    public void save(A id) {
        this.id = id;

        if (!this.shouldSave()) {
            return;
        }

        this.save();
    }
}
