package com.envyful.api.command.injector;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 *
 * A simple implementation of the {@link ArgumentInjector} interface that allows for easy creation of new injectors
 *
 * @param <A> The type of the class to be injected
 * @param <B> The type of the sender
 */
public class SimpleInjector<A, B> implements ArgumentInjector<A, B> {

    private final Class<A> injectedClass;
    private final ArgumentInjectionFunction<A, B> argumentInjector;

    /**
     *
     * Constructor for the simple injector
     *
     * @param injectedClass The class to be injected
     * @param argumentInjector The function to be used to inject the class
     */
    public SimpleInjector(Class<A> injectedClass, ArgumentInjectionFunction<A, B> argumentInjector) {
        this.injectedClass = injectedClass;
        this.argumentInjector = argumentInjector;
    }

    @Override
    public Class<A> getConvertedClass() {
        return this.injectedClass;
    }

    @Override
    public boolean doesRequireMultipleArgs() {
        return false;
    }

    @Override
    public A instantiateClass(B sender, List<Annotation> annotations, String... arguments) {
        return this.argumentInjector.apply(sender, annotations, arguments);
    }
}
