package com.envyful.api.command.annotate;

import com.envyful.api.command.PlatformCommandExecutor;
import com.envyful.api.command.injector.ArgumentInjector;
import com.envyful.api.command.sender.SenderType;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.platform.PlatformProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * Annotation based implementation of the {@link PlatformCommandExecutor} interface
 *
 * @param <C> The sender type
 */
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

        if (!this.senderType.isAccepted(sender)) {
            PlatformProxy.sendMessage(sender, List.of("&c&l(!) &cYou cannot execute this command!"));
            return;
        }

        values[0] = this.senderType.getInstance(sender);

        for (int i = 0; i < this.arguments.size(); i++) {
            var value = this.arguments.get(i).defaultValue;

            if (args.length > i) {
                value = args[i];
            }

            values[i + 1] = this.arguments.get(i).injector.instantiateClass(sender, this.arguments.get(i).annotations, value);

            if (values[i + 1] == null) {
                return;
            }
        }

        if (this.argsCapture) {
            if (this.arguments.size() < args.length) {
                values[values.length - 1] = Arrays.copyOfRange(args, this.arguments.size(), args.length);
            } else {
                values[values.length - 1] = new String[0];
            }
        }

        if (this.async) {
            try {
                this.method.invoke(this.instance,values);
            } catch (IllegalAccessException | InvocationTargetException e) {
                UtilLogger.getLogger().error("Error when executing command " + this.instance.getClass().getSimpleName() + " with method " + this.method.getName(), e);
            }
        } else {
            PlatformProxy.runSync(() -> {
                try {
                    this.method.invoke(this.instance,values);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    UtilLogger.getLogger().error("Error when executing command " + this.instance.getClass().getSimpleName() + " with method " + this.method.getName(), e);
                }
            });
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
        private final List<Annotation> annotations;

        public Argument(ArgumentInjector<?, A> injector, List<Annotation> annotations, String defaultValue) {
            this.injector = injector;
            this.defaultValue = defaultValue;
            this.annotations = annotations;
        }
    }

    public static class Builder<C> {

        protected SenderType<C, ?> senderType;
        protected boolean argsCapture = false;
        protected boolean async = false;
        protected List<Argument<C>> arguments = new ArrayList<>();
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
