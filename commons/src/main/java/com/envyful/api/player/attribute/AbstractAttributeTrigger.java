package com.envyful.api.player.attribute;

import com.envyful.api.player.attribute.data.AttributeData;
import com.envyful.api.type.BiAsyncFunction;
import com.envyful.api.type.map.KeyedMap;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractAttributeTrigger<T extends AttributeHolder> implements AttributeTrigger<T> {

    protected List<AttributeData<?, T>> attributes = new ArrayList<>();
    protected Set<Object> registeredBuses = new HashSet<>();
    protected Set<Class<?>> registeredEvents = new HashSet<>();

    @Override
    public void addAttribute(AttributeData<?, T> attribute) {
        this.attributes.add(attribute);
    }

    @Override
    public boolean registeredFor(Object eventBus, Class<?> event) {
        return this.registeredBuses.contains(eventBus) && this.registeredEvents.contains(event);
    }

    @Override
    public void addEvent(Object eventBus, Class<?> event) {
        this.registeredBuses.add(eventBus);
        this.registeredEvents.add(event);
    }

    @Override
    public boolean handle(Object event) {
        return this.registeredEvents.contains(event.getClass());
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
