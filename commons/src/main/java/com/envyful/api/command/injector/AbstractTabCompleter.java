package com.envyful.api.command.injector;

public abstract class AbstractTabCompleter<A, B> implements TabCompleter<A, B> {

    private final Class<A> completedClass;
    private final Class<B> senderClass;

    protected AbstractTabCompleter(Class<A> completedClass, Class<B> senderClass) {
        this.completedClass = completedClass;
        this.senderClass = senderClass;
    }

    @Override
    public Class<B> getSenderClass() {
        return this.senderClass;
    }

    @Override
    public Class<A> getCompletedClass() {
        return this.completedClass;
    }
}
