package com.envyful.api.player.attribute;

import com.envyful.api.player.PlayerManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
    protected long lastSave = -1L;

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

        this.lastSave = System.currentTimeMillis();

        this.save();
    }

    @Override
    public boolean shouldSave() {
        if (this.lastSave == -1L) {
            return true;
        }

        return super.shouldSave() && (System.currentTimeMillis() - this.lastSave) >= TimeUnit.MINUTES.toMillis(1);
    }

    @Override
    public CompletableFuture<A> getId() {
        return CompletableFuture.completedFuture(this.id);
    }
}
