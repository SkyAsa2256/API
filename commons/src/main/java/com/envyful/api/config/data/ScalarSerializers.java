package com.envyful.api.config.data;

import org.spongepowered.configurate.serialize.ScalarSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 *
 * An annotation to specify the scalar serializers for a config object
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScalarSerializers {

    Class<? extends ScalarSerializer<?>>[] value();

}
