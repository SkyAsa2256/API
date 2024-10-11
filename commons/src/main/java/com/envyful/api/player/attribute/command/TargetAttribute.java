package com.envyful.api.player.attribute.command;

import com.envyful.api.player.Attribute;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetAttribute {

    Class<? extends Attribute<?>> value();

}
