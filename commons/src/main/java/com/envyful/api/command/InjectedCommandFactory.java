package com.envyful.api.command;

import com.envyful.api.command.exception.CommandParseException;
import com.envyful.api.command.injector.ArgumentInjector;
import com.envyful.api.command.injector.TabCompleter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public abstract class InjectedCommandFactory<A, B> implements CommandFactory<A, B> {

    protected final List<ArgumentInjector<?, B>> registeredInjectors = Lists.newArrayList();
    protected final Map<Class<?>, TabCompleter<?, ?>> registeredCompleters = Maps.newConcurrentMap();
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
    public void registerCompleter(TabCompleter<?, ?> tabCompleter) {
        this.registeredCompleters.put(tabCompleter.getClass(), tabCompleter);
    }
}
