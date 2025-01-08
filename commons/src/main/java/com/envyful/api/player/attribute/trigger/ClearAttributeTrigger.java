package com.envyful.api.player.attribute.trigger;

import com.envyful.api.player.Attribute;
import com.envyful.api.player.attribute.AbstractAttributeTrigger;
import com.envyful.api.player.attribute.AttributeHolder;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * An attribute trigger that clears the attribute from the player
 *
 * @param <T> The type of the player
 */
public class ClearAttributeTrigger<T extends AttributeHolder> extends AbstractAttributeTrigger<T> {

    @Override
    public void trigger(AttributeHolder holder) {
        List<Attribute> attributes = new ArrayList<>();

        for (var data : this.attributes) {
            attributes.add(holder.removeAttribute(data.attributeClass()));
        }

        for (var attribute : attributes) {
            attribute.onPlayerQuit();
        }
    }
}
