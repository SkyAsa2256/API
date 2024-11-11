package com.envyful.api.player.save;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.player.Attribute;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public abstract class AbstractSaveManager<T> implements SaveManager<T> {

    protected final Map<Class<? extends Attribute<?>>, PlayerManager.AttributeData<?, ?, T>> registeredAttributes = new ConcurrentHashMap<>();
    protected final Map<Class<? extends Attribute<?>>, Map<Object, Attribute<?>>> sharedAttributes = new ConcurrentHashMap<>();

    protected final PlayerManager<?, T> playerManager;
    protected BiConsumer<EnvyPlayer<T>, Throwable> errorHandler = (player, throwable) -> UtilLogger.logger().ifPresent(logger -> logger.error("Error loading data for " + player.getUniqueId() + " " + player.getName(), throwable));


    protected AbstractSaveManager(PlayerManager<?, T> playerManager) {
        this(playerManager, null);
    }

    protected AbstractSaveManager(PlayerManager<?, T> playerManager, @Nullable BiConsumer<EnvyPlayer<T>, Throwable> errorHandler) {
        this.playerManager = playerManager;

        if (errorHandler != null) {
            this.errorHandler = errorHandler;
        }
    }

    @Override
    public BiConsumer<EnvyPlayer<T>, Throwable> getErrorHandler() {
        return this.errorHandler;
    }

    @Override
    public <A extends Attribute<B>, B> void registerAttribute(PlayerManager.AttributeData<A, B, T> attribute) {
        this.registeredAttributes.put(attribute.attributeClass(), attribute);
    }

    @SuppressWarnings("unchecked")
    protected <A> Attribute<A> getSharedAttribute(Class<? extends A> attributeClass, Object o) {
        return (Attribute<A>) this.sharedAttributes.computeIfAbsent((Class<? extends Attribute<?>>) attributeClass, ___ -> new HashMap<>()).get(o);
    }

    @SuppressWarnings("unchecked")
    protected void addSharedAttribute(Object key, Attribute<?> attribute) {
        this.sharedAttributes.computeIfAbsent((Class<? extends Attribute<?>>) attribute.getClass(), ___ -> new HashMap<>()).put(key, attribute);
    }
}
