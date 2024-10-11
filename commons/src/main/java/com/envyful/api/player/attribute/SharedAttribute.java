package com.envyful.api.player.attribute;

import com.envyful.api.database.Database;
import com.envyful.api.player.save.SaveManager;

import java.util.concurrent.TimeUnit;

/**
 *
 * Abstract implementation of the attribute class storing the manager
 * and id of the attribute
 *
 * @param <A> The attribute ID type
 * @param <B> The manager instance for the attribute
 */
public abstract class SharedAttribute<A, B> extends ManagedAttribute<A, B> {

    protected A id;

    protected transient long lastSave = -1L;
    protected transient Database database;

    protected SharedAttribute(B manager) {
        super(manager);
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
    public A getId() {
        return this.id;
    }

    @Override
    public void deleteAll(SaveManager<?> saveManager) {
        //TODO:
    }

    protected Database getDatabase() {
        return this.database;
    }

}
