package com.envyful.api.player.attribute.data;

import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.Attribute;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 *
 * A simple object used for efficiently instantiating the {@link Attribute} classes using reflection.
 *
 * The constructor of the {@link Attribute} is stored for future efficiency and thus allowing for easy attribute
 * instantiation
 *
 */
public class PlayerAttributeData {

    private final PlayerManager<?, ?> playerManager;
    private final Object manager;
    private final Class<?> managerClass;
    private final Class<? extends Attribute<?, ?>> attributeClass;
    private final Constructor<? extends Attribute<?, ?>> constructor;

    /**
     *
     * Passing the manager and attribute class allows for the object to get the necessary classes, and constructors
     * using this information for later instantiation
     *
     * @param manager The manager object
     * @param attributeClass The class of the attribute being stored
     */
    public PlayerAttributeData(Object manager, PlayerManager<?, ?> playerManager, Class<? extends Attribute<?, ?>> attributeClass) {
        this.manager = manager;
        this.playerManager = playerManager;
        this.managerClass = this.manager.getClass();
        this.attributeClass = attributeClass;
        this.constructor = this.getConstructor();
    }

    public Class<? extends Attribute<?, ?>> getAttributeClass() {
        return this.attributeClass;
    }

    private Constructor<? extends Attribute<?, ?>> getConstructor() {
        try {
            return attributeClass.getConstructor(this.managerClass, PlayerManager.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * Creates a new instance of the {@link Attribute}
     * {@link Attribute#load} not called here
     *
     * @return The new attribute
     */
    public Attribute<?, ?> getInstance() {
        try {
            return this.constructor.newInstance(this.manager, this.playerManager);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * Adds the instance to the given map to ensure encapsulation is followed.
     *
     * @param map The map being added to
     * @param instance The instance being added to the map using the manager class as a key
     */
    public void addToMap(Map<Class<?>, Attribute<?, ?>> map, Attribute<?, ?> instance) {
        map.put(this.managerClass, instance);
    }
}
