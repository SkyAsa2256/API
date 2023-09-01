package com.envyful.api.player.attribute.data;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.Attribute;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
    private final Class<? extends Attribute<?>> attributeClass;
    private final Constructor<? extends Attribute<?>> constructor;

    /**
     *
     * Passing the manager and attribute class allows for the object to get the necessary classes, and constructors
     * using this information for later instantiation
     *
     * @param attributeClass The class of the attribute being stored
     */
    public PlayerAttributeData(
            PlayerManager<?, ?> playerManager,
            Class<? extends Attribute<?>> attributeClass
    ) {
        this.playerManager = playerManager;
        this.attributeClass = attributeClass;
        this.constructor = this.getConstructor();
    }

    public Class<? extends Attribute<?>> getAttributeClass() {
        return this.attributeClass;
    }

    private Constructor<? extends Attribute<?>> getConstructor() {
        try {
            return attributeClass.getConstructor(
                   this.playerManager.getClass()
            );
        } catch (NoSuchMethodException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("No valid constructor found for " + attributeClass.getSimpleName() + ".", e));
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
    public Attribute<?> getInstance() {
        try {
            return this.constructor.newInstance(this.playerManager);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("No valid constructor found for " + attributeClass.getSimpleName() + ".", e));
        }

        return null;
    }
}
