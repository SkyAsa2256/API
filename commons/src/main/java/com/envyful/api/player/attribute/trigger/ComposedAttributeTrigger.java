package com.envyful.api.player.attribute.trigger;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.AttributeTrigger;

import java.util.List;

public class ComposedAttributeTrigger<T> implements AttributeTrigger<T> {

    private final List<AttributeTrigger<T>> triggers;

    public ComposedAttributeTrigger(List<AttributeTrigger<T>> triggers) {
        this.triggers = triggers;
    }

    @Override
    public void addAttribute(PlayerManager.AttributeData<?, ?, T> attribute) {
        for (var trigger : this.triggers) {
            trigger.addAttribute(attribute);
        }
    }

    @Override
    public void trigger(EnvyPlayer<T> player) {
        for (var trigger : this.triggers) {
            trigger.trigger(player);
        }
    }
}
