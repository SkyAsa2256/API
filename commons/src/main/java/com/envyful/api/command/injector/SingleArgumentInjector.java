package com.envyful.api.command.injector;

/**
 *
 * An abstract implementation of the ArgumentInjector interface taking
 * a converted class
 *
 *
 * @param <A> The converted class type
 * @param <B> The sender (module implementation) class
 */
public abstract class SingleArgumentInjector<A, B> implements ArgumentInjector<A, B> {

    protected final Class<A> convertedClass;

    protected SingleArgumentInjector(Class<A> convertedClass) {
        this.convertedClass = convertedClass;
    }

    @Override
    public boolean doesRequireMultipleArgs() {
        return false;
    }

    @Override
    public Class<A> getConvertedClass() {
        return this.convertedClass;
    }
}
