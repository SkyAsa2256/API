package com.envyful.api.command.injector;

import java.lang.annotation.Annotation;
import java.util.List;

public class SimpleInjector<A, B> implements ArgumentInjector<A, B> {

    private final Class<A> injectedClass;
    private final ArgumentInjectionFunction<A, B> argumentInjector;

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
