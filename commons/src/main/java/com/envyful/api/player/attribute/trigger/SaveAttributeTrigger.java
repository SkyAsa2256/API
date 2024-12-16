package com.envyful.api.player.attribute.trigger;

import com.envyful.api.player.attribute.AbstractAttributeTrigger;
import com.envyful.api.player.attribute.AttributeHolder;

/**
 *
 * An instance of a trigger that will save the attribute data
 *
 * @param <T> The type of the player
 */
public class SaveAttributeTrigger<T extends AttributeHolder> extends AbstractAttributeTrigger<T> {

    @Override
    public void trigger(AttributeHolder holder) {
        for (var data : this.attributes) {
            if (!holder.hasAttribute(data.attributeClass())) {
                continue;
            }

            var attribute = holder.getAttributeNow(data.attributeClass());
            data.saveManager().saveData(attribute);
        }
    }
}
