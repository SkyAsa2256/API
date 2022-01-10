package com.envyful.api.player.save.impl;

import com.envyful.api.database.Database;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.attribute.PlayerAttribute;
import com.envyful.api.player.save.SaveManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class SimpleSQLSaveManager<T> implements SaveManager<T> {

    private final Map<Class<? extends PlayerAttribute<?>>, AttributeData> loadedAttributes = Maps.newConcurrentMap();
    private final Database database;

    public SimpleSQLSaveManager(Database database) {this.database = database;}

    @Override
    public List<PlayerAttribute<?>> loadData(EnvyPlayer<T> player) {
        if (this.loadedAttributes.isEmpty()) {
            return Collections.emptyList();
        }

        List<PlayerAttribute<?>> attributes = Lists.newArrayList();

        for (Map.Entry<Class<? extends PlayerAttribute<?>>, AttributeData> entry : this.loadedAttributes.entrySet()) {
            AttributeData value = entry.getValue();
            PlayerAttribute<?> attribute = value.getConstructor().apply(player, value.getManager());

            attribute.preLoad();
            attribute.load();
            attribute.postLoad();
            attributes.add(attribute);
        }

        return attributes;
    }

    @Override
    public void saveData(EnvyPlayer<T> player, PlayerAttribute<?> attribute) {
        AttributeData attributeData = this.loadedAttributes.get(attribute.getClass());

        if (attributeData == null) {
            attribute.save();
            return;
        }

        attribute.preSave();
        attribute.save();
        attribute.postSave();
    }

    @Override
    public void registerAttribute(Object manager, Class<? extends PlayerAttribute<?>> attribute) {
        BiFunction<EnvyPlayer<?>, Object, PlayerAttribute<?>> constructor = this.getConstructor(manager, attribute);

        this.loadedAttributes.put(attribute, new AttributeData(manager, constructor));
    }

    private BiFunction<EnvyPlayer<?>, Object, PlayerAttribute<?>> getConstructor(Object manager, Class<? extends PlayerAttribute<?>> clazz) {
        try {
            Constructor<? extends PlayerAttribute<?>> constructor = clazz.getConstructor(
                    EnvyPlayer.class,
                    manager.getClass()
            );

            return (envyPlayer, o) -> {
                try {
                    return constructor.newInstance(envyPlayer, o);
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

    public static class AttributeData {

        private final Object manager;
        private final BiFunction<EnvyPlayer<?>, Object, PlayerAttribute<?>> constructor;

        public AttributeData(Object manager, BiFunction<EnvyPlayer<?>, Object, PlayerAttribute<?>> constructor) {
            this.manager = manager;
            this.constructor = constructor;
        }

        public Object getManager() {
            return this.manager;
        }

        public BiFunction<EnvyPlayer<?>, Object, PlayerAttribute<?>> getConstructor() {
            return this.constructor;
        }
    }
}
