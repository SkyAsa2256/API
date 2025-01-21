package com.envyful.api.player.attribute.trigger;

import com.envyful.api.player.Attribute;
import com.envyful.api.player.attribute.AbstractAttributeTrigger;
import com.envyful.api.player.attribute.AttributeHolder;
import com.envyful.api.type.map.KeyedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SetAttributeTrigger<T extends AttributeHolder> extends AbstractAttributeTrigger<T> {

    @Override
    public void trigger(T player) {
        List<CompletableFuture<?>> attributeFutures = new ArrayList<>();

        for (var data : this.attributes) {
            var map = new KeyedMap();

            if (!this.shouldLoad(player, data, map)) {
                continue;
            }

            var future = this.getIdMapper(player, data).apply(player, map)
                    .thenCompose(id -> {
                        if (id == null) {
                            return null;
                        }

                        return data.manager().loadAttribute(data.attributeClass(), id);
                    });

            setAttribute(player, data.attributeClass(), future);
            attributeFutures.add(future);
        }

        CompletableFuture.allOf(attributeFutures.toArray(new CompletableFuture[0])).thenRun(() -> {
            for (var attribute : player.getAttributes()) {
                attribute.onAttributesLoaded();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private <A extends Attribute> void setAttribute(T player, Class<?> attributeClass, CompletableFuture<? extends Attribute> attribute) {
        player.setAttribute((Class<A>) attributeClass, (CompletableFuture<A>) attribute);
    }
}
