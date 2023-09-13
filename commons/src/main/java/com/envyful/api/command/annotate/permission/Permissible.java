package com.envyful.api.command.annotate.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Annotation for specifying the permission required for
 * the whole command, or the specific sub-command given by a method
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Permissible {

    String value() default "";

}
