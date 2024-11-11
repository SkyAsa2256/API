package com.envyful.api.player.attribute;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.type.BiAsyncFunction;
import com.envyful.api.type.map.KeyedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractAttributeTrigger<T> implements AttributeTrigger<T> {

    protected List<PlayerManager.AttributeData<?, ?, T>> attributes = new ArrayList<>();

    @Override
    public void addAttribute(PlayerManager.AttributeData<?, ?, T> attribute) {
        this.attributes.add(attribute);
    }

    protected boolean shouldLoad(EnvyPlayer<T> player, PlayerManager.AttributeData<?, ?, T> attributeData, KeyedMap map) {
        for (var predicate : attributeData.predicates()) {
            if (!predicate.test(player, map)) {
                return false;
            }
        }

        return true;
    }

    protected BiAsyncFunction<EnvyPlayer<T>, KeyedMap, Object> getIdMapper(EnvyPlayer<T> player, PlayerManager.AttributeData<?, ?, T> attributeData) {
        if (attributeData.idMapper() == null) {
            return (a, b) -> CompletableFuture.completedFuture(player.getUniqueId());
        }
        return attributeData.idMapper();
    }
}
