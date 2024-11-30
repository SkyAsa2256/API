package com.envyful.api.player.attribute.trigger;

import com.envyful.api.player.Attribute;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.attribute.AbstractAttributeTrigger;
import com.envyful.api.player.save.SaveManager;

/**
 *
 * An instance of a trigger that will save the attribute data
 *
 * @param <T> The type of the player
 */
public class SaveAttributeTrigger<T> extends AbstractAttributeTrigger<T> {

    @Override
    public void trigger(EnvyPlayer<T> player) {
        for (var data : this.attributes) {
            if (!player.hasAttribute(data.attributeClass())) {
                continue;
            }

            var attribute = player.getAttributeNow(data.attributeClass());
            this.saveAttribute(data.saveManager(), attribute, attribute.getId());
        }
    }

    @SuppressWarnings("unchecked")
    private <A extends Attribute<B>, B> void saveAttribute(
            SaveManager<T> saveManager, A attribute, Object id) {
        saveManager.saveData((B) id, attribute);
    }
}
