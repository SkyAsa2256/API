package com.envyful.api.player.attribute;

public abstract class AbstractAttribute<IdType, Manager> implements Attribute<IdType, Manager> {

    protected final transient Manager manager;

    protected IdType id;

    protected AbstractAttribute(Manager manager) {
        this.manager = manager;
    }

    protected abstract void load();

    protected abstract void save();

}
