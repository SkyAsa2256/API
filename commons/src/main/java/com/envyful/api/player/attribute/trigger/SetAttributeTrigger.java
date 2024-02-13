package com.envyful.api.player.attribute.trigger;

import com.envyful.api.player.Attribute;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.attribute.AbstractAttributeTrigger;
import com.envyful.api.player.save.SaveManager;

import java.util.concurrent.CompletableFuture;

public class SetAttributeTrigger<T> extends AbstractAttributeTrigger<T> {

    @Override
    public void trigger(EnvyPlayer<T> player) {
        for (var data : this.attributes) {
            if (!this.shouldLoad(player, data)) {
                continue;
            }

            data.idMapper().apply(player.getParent())
                    .thenAccept(id -> loadAttribute(data.saveManager(), data.attributeClass(), id)
                            .thenAccept(player::setAttribute)
                            .exceptionally(throwable -> {
                                data.saveManager().getErrorHandler().accept(player, throwable);
                                return null;
                            }));
        }
    }

    @SuppressWarnings("unchecked")
    private <A extends Attribute<B, T>, B> CompletableFuture<A> loadAttribute(
            SaveManager<T> saveManager, Class<? extends A> attributeClass, Object id) {
        return saveManager.loadAttribute(attributeClass, (B) id);
    }
}
