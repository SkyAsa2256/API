package com.envyful.api.player.attribute.adapter;

import com.envyful.api.player.Attribute;

/**
 *
 * An ease of use adapter for a self attribute that allows for easy porting of the old attribute
 * loading/saving system
 *
 * @param <A> The type of the attribute
 */
public interface SelfAttributeAdapter<A> extends AttributeAdapter<SelfAttributeAdapter<A>, A>, Attribute<A> {

    @Override
    default void save(SelfAttributeAdapter<A> attribute) {
        this.save();
    }

    void save();

    @Override
    default void load(SelfAttributeAdapter<A> attribute) {
        this.load();
    }

    void load();

    @Override
    default void delete(SelfAttributeAdapter<A> attribute) {

    }

    @Override
    default void deleteAll() {

    }

    @Override
    default void initialize() {

    }
}
