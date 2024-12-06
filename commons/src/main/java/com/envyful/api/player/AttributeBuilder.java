package com.envyful.api.player;

import com.envyful.api.player.attribute.AttributeTrigger;
import com.envyful.api.player.attribute.adapter.AttributeAdapter;
import com.envyful.api.type.AsyncFunction;
import com.envyful.api.type.BiAsyncFunction;
import com.envyful.api.type.map.KeyedMap;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 *
 * A builder for creating {@link Attribute} types to be registered with the {@link PlayerManager}
 *
 * @param <A> The attribute type
 * @param <B> The id type
 * @param <C> The player type
 */
public class AttributeBuilder<A extends Attribute<B>, B, C> {

    protected Class<A> attributeClass;
    protected Function<B, A> constructor;
    protected BiAsyncFunction<EnvyPlayer<C>, KeyedMap, Object> idMapper;
    protected List<BiPredicate<EnvyPlayer<C>, KeyedMap>> predicates = new ArrayList<>();
    protected List<AttributeTrigger<C>> triggers = new ArrayList<>();
    protected Function<UUID, B> offlineIdMapper = null;
    protected Map<String, AttributeAdapter<A, B>> registeredAdapters = new HashMap<>();
    protected String overrideSaveMode = null;

    protected AttributeBuilder() {}

    public AttributeBuilder<A, B, C> attributeClass(Class<A> attributeClass) {
        this.attributeClass = attributeClass;
        return this;
    }

    public Class<A> attributeClass() {
        return this.attributeClass;
    }

    public AttributeBuilder<A, B, C> constructor(Function<B, A> constructor) {
        this.constructor = constructor;
        return this;
    }

    public AttributeBuilder<A, B, C> instantIdMapper(Function<EnvyPlayer<C>, Object> idMapper) {
        return this.idMapper(player -> CompletableFuture.completedFuture(idMapper.apply(player)));
    }

    public AttributeBuilder<A, B, C> idMapper(AsyncFunction<EnvyPlayer<C>, Object> idMapper) {
        this.idMapper = (cEnvyPlayer, keyedMap) -> idMapper.apply(cEnvyPlayer);
        return this;
    }

    public AttributeBuilder<A, B, C> idMapper(BiAsyncFunction<EnvyPlayer<C>, KeyedMap, Object> idMapper) {
        this.idMapper = idMapper;
        return this;
    }

    public AttributeBuilder<A, B, C> filter(Predicate<EnvyPlayer<C>> predicate) {
        this.predicates.add((cEnvyPlayer, keyedMap) -> predicate.test(cEnvyPlayer));
        return this;
    }

    public AttributeBuilder<A, B, C> filter(BiPredicate<EnvyPlayer<C>, KeyedMap> predicate) {
        this.predicates.add(predicate);
        return this;
    }

    @SafeVarargs
    public final AttributeBuilder<A, B, C> triggers(AttributeTrigger<C>... trigger) {
        this.triggers.addAll(List.of(trigger));
        return this;
    }

    public AttributeBuilder<A, B, C> offlineIdMapper(Function<UUID, B> offlineIdMapper) {
        this.offlineIdMapper = offlineIdMapper;
        return this;
    }

    public AttributeBuilder<A, B, C> registerAdapter(String id, AttributeAdapter<A, B> adapter) {
        this.registeredAdapters.put(id.toLowerCase(Locale.ROOT), adapter);
        return this;
    }

    public AttributeBuilder<A, B, C> overrideSaveMode(String overrideSaveMode) {
        this.overrideSaveMode = overrideSaveMode;
        return this;
    }

    public Function<UUID, B> offlineIdMapper() {
        return this.offlineIdMapper;
    }

    public void register(PlayerManager<?, C> playerManager) {
        playerManager.registerAttribute(this);
    }
}
