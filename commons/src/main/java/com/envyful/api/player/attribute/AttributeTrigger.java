package com.envyful.api.player.attribute;

import com.envyful.api.player.attribute.data.AttributeData;

public interface AttributeTrigger<T extends AttributeHolder> {

    void addAttribute(AttributeData<?, T> attribute);

    void trigger(T holder);

}
