package com.envyful.api.discord.command.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Annotation for setting the required role ID for a given discord command
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permissible {

    /**
     *
     * The long ID of the required Discord @{@link net.dv8tion.jda.api.entities.Role}
     *
     * @return The discord role
     */
    long value() default -1L;

}
