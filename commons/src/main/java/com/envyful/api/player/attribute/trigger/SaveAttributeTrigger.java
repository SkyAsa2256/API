package com.envyful.api.player.attribute.trigger;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.concurrency.UtilLogger;
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
        UtilConcurrency.runAsync(() -> {
            for (var data : this.attributes) {
                if (!holder.hasAttribute(data.attributeClass())) {
                    UtilLogger.getLogger().error("Player " + holder.getName() + " does not have attribute " + data.attributeClass().getSimpleName());
                    continue;
                }

                var attribute = holder.getAttributeNow(data.attributeClass());
                data.manager().saveAttribute(attribute);
            }
        });
    }
}
