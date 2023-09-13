package com.envyful.api.command.annotate;

import com.envyful.api.command.PlatformCommandExecutor;
import com.envyful.api.command.injector.ArgumentInjector;
import com.envyful.api.command.sender.SenderType;
import com.envyful.api.concurrency.UtilLogger;
import com.google.common.collect.Lists;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class AnnotationPlatformCommandExecutor<C> implements PlatformCommandExecutor<C> {

    protected final SenderType<C, ?> senderType;
    protected final boolean argsCapture;
    protected final boolean async;
    protected final List<Argument<C>> arguments;
    protected final Object instance;
    protected final Method method;

    protected AnnotationPlatformCommandExecutor(Builder<C> builder) {
        this.senderType = builder.senderType;
        this.argsCapture = builder.argsCapture;
        this.async = builder.async;
        this.arguments = builder.arguments;
        this.instance = builder.instance;
        this.method = builder.method;
    }

    @Override
    public void execute(C sender, String[] args) {
        Object[] values = new Object[this.getArgumentsSize()];

        values[0] = this.senderType.getInstance(sender);

        for (int i = 1; i < (this.arguments.size() + 1); i++) {
            var value = this.arguments.get(i - 1).defaultValue;

            if (args.length > i) {
                value = args[i];
            }

            values[i] = this.arguments.get(i - 1).injector.instantiateClass(sender, value);

            if (values[i] == null) {
                return;
            }
        }

        if (this.argsCapture) {
            values[values.length - 1] = Arrays.copyOfRange(args, this.arguments.size(), args.length);
        }

        if (this.async) {
            try {
                this.method.invoke(this.instance,values);
            } catch (IllegalAccessException | InvocationTargetException e) {
                UtilLogger.logger().ifPresent(logger -> logger.error("Error when executing command " + this.instance.getClass().getSimpleName() + " with method " + this.method.getName(), e));
            }
        } else {
            //TODO: run sync
        }
    }

    private int getArgumentsSize() {
        int size = this.arguments.size() + 1;

        if (this.argsCapture) {
            ++size;
        }

        return size;
    }

    public static <C> Builder<C> builder(SenderType<C, ?> senderType) {
        return new Builder<C>().senderType(senderType);
    }

    public static class Argument<A> {

        private final ArgumentInjector<?, A> injector;
        private final String defaultValue;

        public Argument(ArgumentInjector<?, A> injector, String defaultValue) {
            this.injector = injector;
            this.defaultValue = defaultValue;
        }
    }

    public static class Builder<C> {

        protected SenderType<C, ?> senderType;
        protected boolean argsCapture = false;
        protected boolean async = false;
        protected List<Argument<C>> arguments = Lists.newArrayList();
        protected Object instance;
        protected Method method;

        private Builder() {}

        public Builder<C> senderType(SenderType<C, ?> senderType) {
            this.senderType = senderType;
            return this;
        }

        public Builder<C> argsCapture() {
            this.argsCapture = true;
            return this;
        }

        public Builder<C> argsCapture(boolean argsCapture) {
            this.argsCapture = argsCapture;
            return this;
        }

        public Builder<C> async() {
            this.async = true;
            return this;
        }

        public Builder<C> async(boolean async) {
            this.async = async;
            return this;
        }

        public Builder<C> arguments(List<Argument<C>> arguments) {
            this.arguments.addAll(arguments);
            return this;
        }

        public Builder<C> instance(Object instance) {
            this.instance = instance;
            return this;
        }

        public Builder<C> method(Method method) {
            this.method = method;
            return this;
        }

        public AnnotationPlatformCommandExecutor<C> build() {
            return new AnnotationPlatformCommandExecutor<>(this);
        }
    }
}
