package com.envyful.api.player.save;

import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.Attribute;
import com.google.common.collect.Maps;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public abstract class AbstractSaveManager<T> implements SaveManager<T> {

    protected final Map<Class<? extends Attribute<?, ?>>, AttributeData<?, ?>>
            registeredAttributes = Maps.newConcurrentMap();
    protected final Map<Object, Attribute<?, ?>> sharedAttributes
            = Maps.newConcurrentMap();

    protected final PlayerManager<?, ?> playerManager;

    protected AbstractSaveManager(PlayerManager<?, ?> playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void registerAttribute(
            Object manager, Class<? extends Attribute<?, ?>> attribute) {
        Supplier<Attribute<?, ?>> constructor =
                this.getAttributeConstructor(manager, attribute);
        this.registeredAttributes.put(attribute,
                new AttributeData<>(manager, constructor));
    }

    @Override
    public <B> B getManager(Attribute<?, ?> attribute) {
        return (B) this.registeredAttributes.get(attribute.getClass())
                .getManager();
    }

    private Supplier<Attribute<?, ?>> getAttributeConstructor(
            Object manager, Class<? extends Attribute<?, ?>> clazz) {
        try {
            Constructor<? extends Attribute<?, ?>> constructor =
                    clazz.getConstructor(manager.getClass(),
                            this.playerManager.getClass());

            return () -> {
                try {
                    return constructor.newInstance(manager, this.playerManager);
                } catch (InstantiationException |
                         IllegalAccessException |
                        IllegalArgumentException |
                         InvocationTargetException e) {
                    e.printStackTrace();
                }

                return null;
            };
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected <A, B> Attribute<A, B> getSharedAttribute(Object o) {
        return (Attribute<A, B>) this.sharedAttributes.get(o);
    }

    protected void addSharedAttribute(Object key, Attribute<?, ?> attribute) {
        this.sharedAttributes.put(key, attribute);
    }

    public static class AttributeData<A, B extends Attribute<?, A>> {

        private final A manager;
        private final Supplier<B> constructor;

        public AttributeData(A manager, Supplier<?> constructor) {
            this.manager = manager;
            this.constructor = (Supplier<B>) constructor;
        }

        public Object getManager() {
            return this.manager;
        }

        public Supplier<B> getConstructor() {
            return this.constructor;
        }
    }
}
