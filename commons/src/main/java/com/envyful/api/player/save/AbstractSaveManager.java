package com.envyful.api.player.save;

import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.Attribute;
import com.google.common.collect.Maps;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractSaveManager<T> implements SaveManager<T> {

    protected final Map<Class<? extends Attribute<?, ?>>, AttributeData> registeredAttributes = Maps.newConcurrentMap();
    protected final Map<Object, Attribute<?, ?>> sharedAttributes = Maps.newConcurrentMap();

    protected final PlayerManager<?, ?> playerManager;

    protected AbstractSaveManager(PlayerManager<?, ?> playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void registerAttribute(Object manager, Class<? extends Attribute<?, ?>> attribute) {
        Function<Object, Attribute<?, ?>> constructor = this.getAttributeConstructor(manager, attribute);
        this.registeredAttributes.put(attribute, new AttributeData(manager, constructor));
    }

    private Function<Object, Attribute<?, ?>> getAttributeConstructor(Object manager, Class<? extends Attribute<?, ?>> clazz) {
        try {
            Constructor<? extends Attribute<?, ?>> constructor = clazz.getConstructor(manager.getClass(), PlayerManager.class);

            return o -> {
                try {
                    return constructor.newInstance(o, this.playerManager);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                return null;
            };
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected Attribute<?, ?> getSharedAttribute(Object o) {
        return this.sharedAttributes.get(o);
    }

    protected void addSharedAttribute(Object key, Attribute<?, ?> attribute) {
        this.sharedAttributes.put(key, attribute);
    }

    public static class AttributeData {

        private final Object manager;
        private final Function<Object, Attribute<?, ?>> constructor;

        public AttributeData(Object manager, Function<Object, Attribute<?, ?>> constructor) {
            this.manager = manager;
            this.constructor = constructor;
        }

        public Object getManager() {
            return this.manager;
        }

        public Function<Object, Attribute<?, ?>> getConstructor() {
            return this.constructor;
        }
    }
}
