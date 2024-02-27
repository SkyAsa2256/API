package com.envyful.api.command;

/**
 *
 * An interface that represents a command executor for a specific platform
 *
 * @param <C> The sender type for the platform specific implementation
 */
public interface PlatformCommandExecutor<C> {

    /**
     *
     * The method for executing the command
     *
     * @param sender The sender of the command
     * @param args The arguments for the command
     */
    void execute(C sender, String[] args);

}
