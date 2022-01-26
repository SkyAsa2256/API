package com.envyful.api.config.config.data;

import com.envyful.api.config.config.Config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Annotation for passing the config path to the {@link Config} interface so it can load the
 * values to the object
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigPath {

    /**
     *
     * The path of the config
     *
     * @return The file path to the config
     */
    String value() default "";

}
