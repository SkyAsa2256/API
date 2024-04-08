package com.envyful.api.config.data;

import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * An annotation to specify the type serializers for a config object
 * <br>
 * The {@link #serializer()} and {@link #clazz()} arrays must be the same length and
 * the index of the serializer must correspond to the index of the class
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeSerializers {

    Class<? extends TypeSerializer>[] serializer();

    Class<?>[] clazz();

}
