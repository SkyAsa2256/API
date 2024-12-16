package com.envyful.api.player.attribute.trigger;

import com.envyful.api.player.attribute.AttributeHolder;
import com.envyful.api.player.attribute.AttributeTrigger;
import com.envyful.api.player.attribute.data.AttributeData;

import java.util.List;

public class ComposedAttributeTrigger<T extends AttributeHolder> implements AttributeTrigger<T> {

    private final List<AttributeTrigger<T>> triggers;

    public ComposedAttributeTrigger(List<AttributeTrigger<T>> triggers) {
        this.triggers = triggers;
    }

    @Override
    public void addAttribute(AttributeData<?, T> attribute) {
        for (var trigger : this.triggers) {
            trigger.addAttribute(attribute);
        }
    }

    @Override
    public void trigger(T holder) {
        for (var trigger : this.triggers) {
            trigger.trigger(holder);
        }
    }
}
