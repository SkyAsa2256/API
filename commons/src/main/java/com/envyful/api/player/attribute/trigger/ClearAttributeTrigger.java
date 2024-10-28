package com.envyful.api.player.attribute.trigger;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.attribute.AbstractAttributeTrigger;

/**
 *
 * An attribute trigger that clears the attribute from the player
 *
 * @param <T> The type of the player
 */
public class ClearAttributeTrigger<T> extends AbstractAttributeTrigger<T> {

    @Override
    public void trigger(EnvyPlayer<T> player) {
        for (var data : this.attributes) {
            player.removeAttribute(data.attributeClass());
        }
    }
}
