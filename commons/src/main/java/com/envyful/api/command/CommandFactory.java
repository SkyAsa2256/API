package com.envyful.api.command;

import com.envyful.api.command.exception.CommandParseException;
import com.envyful.api.command.injector.ArgumentInjector;
import com.envyful.api.command.injector.TabCompleter;

import java.util.function.BiFunction;

/**
 *
 * An interface for registering, and unregistering,
 * commands and the injectors for handling those commands.
 *
 * @param <A> The server / command registrar for
 *          the platform specific implementation
 * @param <B> The sender type for the platform specific implementation
 */
public interface CommandFactory<A, B> {

    /**
     *
     * Registers the command with the command registrar
     * for that platform.
     *
     * @param registrar The command registrar for that platform
     * @param command The command being registered
     */
    void registerCommand(A registrar, PlatformCommand<B> command);

    /**
     *
     * Gets an instance of the command builder for the platform
     *
     * @return The command builder instance
     */
    PlatformCommand.Builder<B> commandBuilder();

    /**
     *
     * Parses a command using the defined {@link CommandParser}
     *
     * @param o The object being parsed
     * @return The parsed command
     * @throws CommandParseException Thrown if there is an error parsing the object
     */
    PlatformCommand<B> parseCommand(Object o) throws CommandParseException;

    /**
     *
     * Method for checking if the sender has the permission node
     *
     * @param sender The sender
     * @param permission The node being checked
     * @return True if they have access to that permission
     */
    boolean hasPermission(B sender, String permission);

    /**
     *
     * Default method for registering an
     * injector where multiple args defaults to false.
     * By default, uses the {@link CommandFactory#registerInjector(Class, boolean, BiFunction)} method with the
     * multipleArgs flag as false (as this is the most common use-case)
     *
     * @param parentClass The converted class to be registered
     * @param function The function converting
     *                 the sender and args to the parentClass
     */
    default <C> void registerInjector(Class<C> parentClass,
                                  BiFunction<B, String[], C> function
    ) {
        this.registerInjector(parentClass, false, function);
    }

    ArgumentInjector<?, B> getRegisteredInjector(Class<?> parentClass);

    /**
     *
     * Method for registering the injectors converting
     * from the args, and command sender, to the parentClass
     *
     * @param parentClass The converted class to be registered
     * @param multipleArgs if the command requires
     *                     multiple arguments to determine the converted data
     * @param function The function converting
     *                 the sender and args to the parentClass
     */
    <C> void registerInjector(Class<C> parentClass,
                          boolean multipleArgs,
                          BiFunction<B, String[], C> function);

    /**
     *
     * Unregisters all injectors with the converted class specified
     *
     * @param parentClass The class for all injectors to be removed
     */
    void unregisterInjector(Class<?> parentClass);

    /**
     *
     * Registers a tab completion method
     *
     * @param tabCompleter The tab completer
     */
    void registerCompleter(TabCompleter<?, ?> tabCompleter);

}
