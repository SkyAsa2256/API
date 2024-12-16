package com.envyful.api.command;

import com.envyful.api.command.exception.CommandParseException;
import com.envyful.api.command.injector.ArgumentInjectionFunction;
import com.envyful.api.command.injector.ArgumentInjector;
import com.envyful.api.command.injector.SimpleInjector;
import com.envyful.api.command.injector.TabCompleter;
import com.envyful.api.player.Attribute;
import com.envyful.api.player.attribute.OfflineAttribute;
import com.envyful.api.player.attribute.command.TargetAttribute;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 *
 * A command factory that allows for the registration of argument injectors and tab completers
 *
 * @param <A> The platform specific command dispatcher
 * @param <B> The platform specific sender type
 */
public abstract class InjectedCommandFactory<A, B> implements CommandFactory<A, B> {

    protected final List<ArgumentInjector<?, B>> registeredInjectors = new ArrayList<>();
    protected final Map<Class<?>, TabCompleter<?>> registeredCompleters = new ConcurrentHashMap<>();
    protected final CommandParser<? extends PlatformCommand<B>, B> commandParser;

    protected InjectedCommandFactory(Function<InjectedCommandFactory<A, B>, ? extends CommandParser<? extends PlatformCommand<B>, B>> commandParser) {
        this.commandParser = commandParser.apply(this);

        this.registerInjector(int.class, (ICommandSource, args) -> {
            try {
                return Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                return null;
            }
        });

        this.registerInjector(String.class, (ICommandSource, args) -> args[0]);

        this.registerInjector(double.class, ((ICommandSource, args) -> {
            try {
                return Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                return null;
            }
        }));

        this.registerInjector(long.class, ((ICommandSource, args) -> {
            try {
                return Long.parseLong(args[0]);
            } catch (NumberFormatException e) {
                return null;
            }
        }));

        this.registerInjector(boolean.class, ((ICommandSource, args) -> {
            if (args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("yes") ||
                args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("1")) {
                return true;
            }

            if (args[0].equalsIgnoreCase("false") || args[0].equalsIgnoreCase("no") ||
                args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("0")) {
                return false;
            }

            return null;
        }));

        this.registerInjector(OfflineAttribute.class, (sender, annotations, args) -> {
            var targetAttribute = this.getTargetAttribute(annotations);

            if (targetAttribute == null) {
                throw new CommandParseException("No TargetAttribute with paired OfflineAttribute injection found");
            }

            return offlineAttributeBouncer(targetAttribute.value(), args[0]);
        });
    }

    private TargetAttribute getTargetAttribute(List<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof TargetAttribute) {
                return (TargetAttribute) annotation;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private <A extends Attribute> OfflineAttribute<A> offlineAttributeBouncer(Class<?> clazz, String name) {
        return OfflineAttribute.fromName((Class<A>) clazz, name);
    }

    @Override
    public PlatformCommand<B> parseCommand(Object o) throws CommandParseException {
        return this.commandParser.parseCommand(o);
    }

    @Override
    public ArgumentInjector<?, B> getRegisteredInjector(Class<?> parentClass) {
        for (ArgumentInjector<?, B> registeredInjector : this.registeredInjectors) {
            if (registeredInjector.getConvertedClass().equals(parentClass)) {
                return registeredInjector;
            }
        }

        return null;
    }

    @Override
    public void unregisterInjector(Class<?> parentClass) {
        this.registeredInjectors.removeIf(next -> Objects.equals(parentClass, next.getConvertedClass()));
    }

    @Override
    public void registerCompleter(TabCompleter<?> tabCompleter) {
        this.registeredCompleters.put(tabCompleter.getClass(), tabCompleter);
    }

    @Override
    public <C> void registerInjector(Class<C> parentClass, ArgumentInjectionFunction<C, B> function) {
        this.registeredInjectors.add(new SimpleInjector<>(parentClass, function));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> TabCompleter<T> getRegisteredCompleter(Class<?> tabCompleterClass) {
        return (TabCompleter<T>) this.registeredCompleters.get(tabCompleterClass);
    }
}
