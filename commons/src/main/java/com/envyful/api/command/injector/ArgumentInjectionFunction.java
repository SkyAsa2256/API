package com.envyful.api.command.injector;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 *
 * A functional interface for injecting arguments into a command
 *
 * @param <A> The type of the argument being injected
 * @param <B> The type of the sender
 */
public interface ArgumentInjectionFunction<A, B> {

    A apply(B sender, List<Annotation> annotations, String[] args);

}
