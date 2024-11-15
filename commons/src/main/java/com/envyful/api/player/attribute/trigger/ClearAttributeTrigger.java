package com.envyful.api.player.attribute.trigger;

import com.envyful.api.player.Attribute;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.attribute.AbstractAttributeTrigger;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * An attribute trigger that clears the attribute from the player
 *
 * @param <T> The type of the player
 */
public class ClearAttributeTrigger<T> extends AbstractAttributeTrigger<T> {

    @Override
    public void trigger(EnvyPlayer<T> player) {
        List<Attribute<?>> attributes = new ArrayList<>();

        for (var data : this.attributes) {
            attributes.add(player.removeAttribute(data.attributeClass()));
        }

        for (var attribute : attributes) {
            attribute.onPlayerQuit();
        }
    }
}
