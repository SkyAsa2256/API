package com.envyful.api.player.save.impl;

import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.Attribute;
import com.envyful.api.player.save.AbstractSaveManager;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EmptySaveManager<T> extends AbstractSaveManager<T> {

    public EmptySaveManager(PlayerManager<?, ?> playerManager) {
        super(playerManager);
    }

    @Override
    public CompletableFuture<List<Attribute<?, ?>>> loadData(UUID uuid) {
        if (this.registeredAttributes.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        List<Attribute<?, ?>> attributes = Lists.newArrayList();
        List<CompletableFuture<Attribute<?, ?>>> loadTasks = Lists.newArrayList();

        for (Map.Entry<Class<? extends Attribute<?, ?>>, AttributeData<?, ?>> entry : this.registeredAttributes.entrySet()) {
            AttributeData<?, ?> value = entry.getValue();
            Attribute<?, ?> attribute = value.getConstructor().apply(uuid);

            loadTasks.add(attribute.getId(uuid).thenApply(o -> {
                if (o == null) {
                    return null;
                }

                if (attribute.isShared()) {
                    Attribute<?, ?> sharedAttribute = this.getSharedAttribute(o);

                    if (sharedAttribute == null) {
                        sharedAttribute = attribute;
                        attribute.loadWithGenericId(o);
                        this.addSharedAttribute(entry.getValue().getManager(), sharedAttribute);
                    }

                    return sharedAttribute;
                } else {
                    attribute.loadWithGenericId(o);
                    return attribute;
                }
            }).whenComplete((loaded, throwable) -> {
                if (loaded != null) {
                    attributes.add(loaded);
                }
            }));
        }

        return CompletableFuture.allOf(loadTasks.toArray(new CompletableFuture[0])).thenApply(unused -> attributes);
    }

    @Override
    public void saveData(UUID uuid, Attribute<?, ?> attribute) {
        attribute.getId(uuid).whenComplete((o, throwable) -> attribute.saveWithGenericId(o));
    }
}
