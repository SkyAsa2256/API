package com.envyful.api.player.attribute;

import com.envyful.api.player.attribute.data.AttributeData;
import com.envyful.api.type.BiAsyncFunction;
import com.envyful.api.type.map.KeyedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractAttributeTrigger<T extends AttributeHolder> implements AttributeTrigger<T> {

    protected List<AttributeData<?, T>> attributes = new ArrayList<>();

    @Override
    public void addAttribute(AttributeData<?, T> attribute) {
        this.attributes.add(attribute);
    }

    protected boolean shouldLoad(T player, AttributeData<?, T> attributeData, KeyedMap map) {
        for (var predicate : attributeData.predicates()) {
            if (!predicate.test(player, map)) {
                return false;
            }
        }

        return true;
    }

    protected BiAsyncFunction<T, KeyedMap, UUID> getIdMapper(T holder, AttributeData<?, T> attributeData) {
        if (attributeData.idMapper() == null && holder != null) {
            return (a, b) -> CompletableFuture.completedFuture(holder.getUniqueId());
        }

        return attributeData.idMapper();
    }
}
