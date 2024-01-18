package com.envyful.api.player.save;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.Attribute;
import com.google.common.collect.Maps;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public abstract class AbstractSaveManager<T> implements SaveManager<T> {

    protected final Map<Class<? extends Attribute<?>>, AttributeData<?, ?>>
            registeredAttributes = Maps.newConcurrentMap();
    protected final Map<Class<? extends Attribute<?>>, Map<Object, Attribute<?>>> sharedAttributes
            = Maps.newConcurrentMap();

    protected final PlayerManager<?, ?> playerManager;
    protected BiConsumer<EnvyPlayer<?>, Throwable> errorHandler = (player, throwable) -> UtilLogger.logger().ifPresent(logger -> logger.error("Error loading data for " + player.getUniqueId() + " " + player.getName(), throwable));

    protected AbstractSaveManager(PlayerManager<?, ?> playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void setErrorHandler(BiConsumer<EnvyPlayer<?>, Throwable> errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public BiConsumer<EnvyPlayer<?>, Throwable> getErrorHandler() {
        return this.errorHandler;
    }

    @Override
    public void registerAttribute(Class<? extends Attribute<?>> attribute) {
        Supplier<Attribute<?>> constructor =
                this.getAttributeConstructor(attribute);

        if (constructor == null) {
            throw new IllegalArgumentException("No valid constructor found for " + attribute.getName());
        }

        this.registeredAttributes.put(attribute, new AttributeData<>(constructor));
    }

    private Supplier<Attribute<?>> getAttributeConstructor(Class<? extends Attribute<?>> clazz) {
        try {
            Constructor<? extends Attribute<?>> constructor = clazz.getConstructor(this.playerManager.getClass());

            return () -> {
                try {
                    return constructor.newInstance(this.playerManager);
                } catch (InstantiationException |
                         IllegalAccessException |
                        IllegalArgumentException |
                         InvocationTargetException e) {
                    UtilLogger.logger().ifPresent(logger -> logger.error("Failed to obtain player manager constructor for attribute " + clazz.getName(), e));
                }

                return null;
            };
        } catch (NoSuchMethodException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Failed to obtain player manager constructor for attribute " + clazz.getName(), e));
        }

        return null;
    }

    protected <A> Attribute<A> getSharedAttribute(Class<? extends Attribute<?>> attributeClass, Object o) {
        return (Attribute<A>) this.sharedAttributes.computeIfAbsent(attributeClass, ___ -> Maps.newHashMap()).get(o);
    }

    protected void addSharedAttribute(Object key, Attribute<?> attribute) {
        this.sharedAttributes.computeIfAbsent((Class<? extends Attribute<?>>) attribute.getClass(), ___ -> Maps.newHashMap()).put(key, attribute);
    }

    public static class AttributeData<A, B extends Attribute<A>> {

        private final Supplier<B> constructor;

        public AttributeData(Supplier<?> constructor) {
            this.constructor = (Supplier<B>) constructor;
        }

        public Supplier<B> getConstructor() {
            return this.constructor;
        }
    }
}
