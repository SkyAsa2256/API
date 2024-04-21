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
 * The classes specified in {@link TypeSerializers#value()} must be registered with {@link com.envyful.api.config.ConfigTypeSerializer#register(TypeSerializer, Class)}
 * otherwise they will throw exceptions
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeSerializers {

    Class<?>[] value();

}
