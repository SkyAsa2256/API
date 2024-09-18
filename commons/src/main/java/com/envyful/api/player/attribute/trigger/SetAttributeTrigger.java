package com.envyful.api.player.attribute.trigger;

import com.envyful.api.player.Attribute;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.attribute.AbstractAttributeTrigger;
import com.envyful.api.player.save.SaveManager;
import com.envyful.api.type.map.KeyedMap;

import java.util.concurrent.CompletableFuture;

public class SetAttributeTrigger<T> extends AbstractAttributeTrigger<T> {

    @Override
    public void trigger(EnvyPlayer<T> player) {
        for (var data : this.attributes) {
            var map = new KeyedMap();

            if (!this.shouldLoad(player, data, map)) {
                continue;
            }

            setAttribute(player, data.attributeClass(),
                    this.getIdMapper(player, data).apply(player, map)
                            .thenCompose(id -> {
                                if (id == null) {
                                    return null;
                                }

                                return loadAttribute(data.saveManager(), data.attributeClass(), id);
                            })
                            .exceptionally(throwable -> {
                                data.saveManager().getErrorHandler().accept(player, throwable);
                                return null;
                            }));
        }
    }

    @SuppressWarnings("unchecked")
    private <A extends Attribute<B>, B, C extends EnvyPlayer<T>> void setAttribute(C player, Class<?> attributeClass, CompletableFuture<? extends Attribute> attribute) {
        player.setAttribute((Class<A>) attributeClass, (CompletableFuture<A>) attribute);
    }

    @SuppressWarnings("unchecked")
    private <A extends Attribute<B>, B> CompletableFuture<A> loadAttribute(
            SaveManager<T> saveManager, Class<? extends A> attributeClass, Object id) {
        return saveManager.loadAttribute(attributeClass, (B) id);
    }
}
