package com.envyful.api.player.attribute;

import com.envyful.api.player.attribute.data.AttributeData;

public interface AttributeTrigger<T extends AttributeHolder> {

    boolean registeredFor(Object eventBus, Class<?> event);

    boolean handle(Object event);

    void addEvent(Object eventBus, Class<?> event);

    void addAttribute(AttributeData<?, T> attribute);

    void trigger(T holder);

}
