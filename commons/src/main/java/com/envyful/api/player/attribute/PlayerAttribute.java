package com.envyful.api.player.attribute;

import com.envyful.api.database.Database;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.save.SaveManager;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    protected transient Database database;
    protected transient B parent;

    protected PlayerAttribute(A manager) {
        super(manager);
    }

    public void setParent(B parent) {
        this.parent = parent;
    }

    @Override
    public CompletableFuture<UUID> getId() {
        return CompletableFuture.completedFuture(this.id);
    }

    public UUID getUuid() {
        return this.id;
    }

    @Override
    public void save(UUID id) {
        this.id = id;

        if (!this.shouldSave()) {
            return;
        }

        this.save();
    }

    @Override
    public void load(UUID id) {
        this.id = id;

        this.load();
    }

    @Override
    public void deleteAll(SaveManager<?> saveManager) {
        //TODO:
    }

    protected Database getDatabase() {
        return this.database;
    }
}
