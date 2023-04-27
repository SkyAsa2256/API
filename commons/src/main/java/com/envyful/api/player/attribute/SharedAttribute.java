package com.envyful.api.player.attribute;

/**
 *
 * Abstract implementation of the attribute class storing the manager
 * and id of the attribute
 *
 * @param <A> The attribute ID type
 * @param <B> The manager instance for the attribute
 */
public abstract class SharedAttribute<A, B> extends AbstractAttribute<A, B> {

    protected A id;

    protected SharedAttribute(B manager) {
        super(manager);
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

        this.save();
    }
}
