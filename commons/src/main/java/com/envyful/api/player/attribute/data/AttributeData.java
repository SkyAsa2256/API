package com.envyful.api.player.attribute.data;

import com.envyful.api.player.Attribute;
import com.envyful.api.player.attribute.AttributeHolder;
import com.envyful.api.player.attribute.AttributeTrigger;
import com.envyful.api.player.attribute.adapter.AttributeAdapter;
import com.envyful.api.player.save.SaveManager;
import com.envyful.api.type.BiAsyncFunction;
import com.envyful.api.type.map.KeyedMap;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A simple data transfer object for the attribute data
 *
 * @param <A> The attribute type
 * @param <B> The type
 */
public class AttributeData<A extends Attribute, B extends AttributeHolder> {

    private final Class<A> attributeClass;
    private final boolean shared;
    private final Function<UUID, A> constructor;
    private final BiAsyncFunction<B, KeyedMap, UUID> idMapper;
    private final UnaryOperator<UUID> offlineIdMapper;
    private final List<BiPredicate<B, KeyedMap>> predicates;
    private final List<AttributeTrigger<B>> triggers;
    private final SaveManager<B> saveManager;
    private final Map<String, AttributeAdapter<A>> adapters;

    public AttributeData(Class<A> attributeClass, boolean shared, Function<UUID, A> constructor, BiAsyncFunction<B, KeyedMap, UUID> idMapper,
                         List<BiPredicate<B, KeyedMap>> predicates, List<AttributeTrigger<B>> triggers,
                         UnaryOperator<UUID> offlineIdMapper,
                         SaveManager<B> saveManager, Map<String, AttributeAdapter<A>> adapters) {
        this.attributeClass = attributeClass;
        this.shared = shared;
        this.constructor = constructor;
        this.idMapper = idMapper;
        this.predicates = List.copyOf(predicates);
        this.triggers = List.copyOf(triggers);
        this.offlineIdMapper = offlineIdMapper;
        this.saveManager = saveManager;
        this.adapters = Map.copyOf(adapters);
    }

    public Class<A> attributeClass() {
        return this.attributeClass;
    }

    public boolean shared() {
        return this.shared;
    }

    public Function<UUID, A> constructor() {
        return this.constructor;
    }

    public BiAsyncFunction<B, KeyedMap, UUID> idMapper() {
        return this.idMapper;
    }

    public List<BiPredicate<B, KeyedMap>> predicates() {
        return this.predicates;
    }

    public List<AttributeTrigger<B>> triggers() {
        return this.triggers;
    }

    public SaveManager<B> saveManager() {
        return this.saveManager;
    }

    public UnaryOperator<UUID> offlineIdMapper() {
        return this.offlineIdMapper;
    }

    public Map<String, AttributeAdapter<A>> adapters() {
        return this.adapters;
    }
}
