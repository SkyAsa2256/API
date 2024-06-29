package com.envyful.api.player.attribute.trigger;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.player.Attribute;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.attribute.AbstractAttributeTrigger;
import com.envyful.api.player.save.SaveManager;

public class SaveAttributeTrigger<T> extends AbstractAttributeTrigger<T> {

    @Override
    public void trigger(EnvyPlayer<T> player) {
        for (var data : this.attributes) {
            this.getIdMapper(player, data).apply(player)
                    .thenAcceptAsync(id -> this.saveAttribute(data.saveManager(), player.getAttributeNow(data.attributeClass()), id), UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE);
        }
    }

    @SuppressWarnings("unchecked")
    private <A extends Attribute<B>, B> void saveAttribute(
            SaveManager<T> saveManager, A attribute, Object id) {
        saveManager.saveData((B) id, attribute);
    }
}
