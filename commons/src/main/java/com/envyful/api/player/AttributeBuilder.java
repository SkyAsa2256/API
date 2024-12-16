package com.envyful.api.player;

import com.envyful.api.player.attribute.AttributeHolder;
import com.envyful.api.player.attribute.AttributeTrigger;
import com.envyful.api.player.attribute.adapter.AttributeAdapter;
import com.envyful.api.player.attribute.manager.AttributeManager;
import com.envyful.api.type.AsyncFunction;
import com.envyful.api.type.BiAsyncFunction;
import com.envyful.api.type.map.KeyedMap;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 *
 *
 * A builder for creating {@link Attribute} types to be registered with the {@link PlayerManager}
 *
 * @param <A> The attribute type
 * @param <C> The player type
 */
public class AttributeBuilder<A extends Attribute, C extends AttributeHolder> {

    protected Class<A> attributeClass;
    protected Function<UUID, A> constructor;
    protected BiAsyncFunction<C, KeyedMap, UUID> idMapper;
    protected List<BiPredicate<C, KeyedMap>> predicates = new ArrayList<>();
    protected List<AttributeTrigger<C>> triggers = new ArrayList<>();
    protected UnaryOperator<UUID> offlineIdMapper = UnaryOperator.identity();
    protected Map<String, AttributeAdapter<A>> registeredAdapters = new HashMap<>();
    protected String overrideSaveMode = null;
    protected boolean shared = false;

    protected AttributeBuilder() {}

    public AttributeBuilder<A, C> attributeClass(Class<A> attributeClass) {
        this.attributeClass = attributeClass;
        return this;
    }

    public Class<A> attributeClass() {
        return this.attributeClass;
    }

    public AttributeBuilder<A, C> constructor(Function<UUID, A> constructor) {
        this.constructor = constructor;
        return this;
    }

    public AttributeBuilder<A, C> instantIdMapper(Function<C, UUID> idMapper) {
        return this.idMapper(player -> CompletableFuture.completedFuture(idMapper.apply(player)));
    }

    public AttributeBuilder<A, C> idMapper(AsyncFunction<C, UUID> idMapper) {
        this.idMapper = (cEnvyPlayer, keyedMap) -> idMapper.apply(cEnvyPlayer);
        return this;
    }

    public AttributeBuilder<A, C> idMapper(BiAsyncFunction<C, KeyedMap, UUID> idMapper) {
        this.idMapper = idMapper;
        return this;
    }

    public AttributeBuilder<A, C> filter(Predicate<C> predicate) {
        this.predicates.add((cEnvyPlayer, keyedMap) -> predicate.test(cEnvyPlayer));
        return this;
    }

    public AttributeBuilder<A, C> filter(BiPredicate<C, KeyedMap> predicate) {
        this.predicates.add(predicate);
        return this;
    }

    @SafeVarargs
    public final AttributeBuilder<A, C> triggers(AttributeTrigger<C>... trigger) {
        this.triggers.addAll(List.of(trigger));
        return this;
    }

    public AttributeBuilder<A, C> offlineIdMapper(UnaryOperator<UUID> offlineIdMapper) {
        this.offlineIdMapper = offlineIdMapper;
        return this;
    }

    public AttributeBuilder<A, C> registerAdapter(String id, AttributeAdapter<A> adapter) {
        this.registeredAdapters.put(id.toLowerCase(Locale.ROOT), adapter);
        return this;
    }

    public AttributeBuilder<A, C> overrideSaveMode(String overrideSaveMode) {
        this.overrideSaveMode = overrideSaveMode;
        return this;
    }

    public AttributeBuilder<A, C> shared() {
        this.shared = true;
        return this;
    }

    public AttributeBuilder<A, C> notShared() {
        this.shared = false;
        return this;
    }

    public AttributeBuilder<A, C> setShared(boolean shared) {
        this.shared = shared;
        return this;
    }

    public void register(AttributeManager<C> manager) {
        manager.registerAttribute(this);
    }

    public String overrideSaveMode() {
        return this.overrideSaveMode;
    }

    public UnaryOperator<UUID> offlineIdMapper() {
        return this.offlineIdMapper;
    }

    public boolean isShared() {
        return this.shared;
    }

    public Function<UUID, A> constructor() {
        return this.constructor;
    }

    public BiAsyncFunction<C, KeyedMap, UUID> idMapper() {
        return this.idMapper;
    }

    public List<BiPredicate<C, KeyedMap>> predicates() {
        return this.predicates;
    }

    public List<AttributeTrigger<C>> triggers() {
        return this.triggers;
    }

    public Map<String, AttributeAdapter<A>> registeredAdapters() {
        return this.registeredAdapters;
    }
}
