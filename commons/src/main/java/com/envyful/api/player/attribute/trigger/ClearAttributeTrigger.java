package com.envyful.api.player.attribute.trigger;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.attribute.AbstractAttributeTrigger;

public class ClearAttributeTrigger<T> extends AbstractAttributeTrigger<T> {

    @Override
    public void trigger(EnvyPlayer<T> player) {
        for (var data : this.attributes) {
            player.removeAttribute(data.attributeClass());
        }
    }
}
