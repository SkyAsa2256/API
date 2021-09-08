package com.envyful.api.forge.command.command.executor;

import com.envyful.api.command.injector.ArgumentInjector;
import com.envyful.api.forge.command.command.ForgeSenderType;
import com.envyful.api.forge.player.util.UtilPlayer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 *
 * A simple data transfer object for the command methods obtained through reflection.
 * Ensures no laggy reflection during user use-age as the methods, object instances, and required injectors are cached at
 * server start for future use.
 *
 */
public class CommandExecutor {

    private final String identifier;
    private final int senderPosition;
    private final ForgeSenderType sender;
    private final Object commandClass;
    private final Method executor;
    private final boolean executeAsync;
    private final int justArgsPos;
    private final int requiredArgs;
    private final String requiredPermission;
    private final ArgumentInjector<?, ICommandSender>[] arguments;

    /**
     *
     * Simple constructor taking all required parameters
     *
     * @param identifier The identifier string for the sub-command
     * @param sender The sender type for the sub-command
     * @param senderPosition The position in the parameters of the sender
     * @param commandClass An instance of the command class
     * @param executor The method instance
     * @param executeAsync If the command should be run asynchronously
     * @param requiredPermission The permission required to execute the command
     * @param arguments The injected argument types
     */
    public CommandExecutor(String identifier, ForgeSenderType sender, int senderPosition, Object commandClass, Method executor,
                           boolean executeAsync, int justArgsPos, String requiredPermission, ArgumentInjector<?, ICommandSender>[] arguments) {
        this.identifier = identifier;
        this.senderPosition = senderPosition;
        this.sender = sender;
        this.commandClass = commandClass;
        this.executor = executor;
        this.executeAsync = executeAsync;
        this.justArgsPos = justArgsPos;
        this.requiredPermission = requiredPermission;
        this.arguments = arguments;
        this.requiredArgs = this.calculateRequiredArgs();
    }

    /**
     *
     * Calculates the arguments required based on the injected arguments.
     * If there is a single multiple arg requirement then it will return -1.
     *
     * @return Number of args required for the sub command
     */
    private int calculateRequiredArgs() {
        if (this.justArgsPos != -1) {
            return -1;
        }

        for (ArgumentInjector<?, ICommandSender> argument : this.arguments) {
            if (argument != null && argument.doesRequireMultipleArgs()) {
                return -1;
            }
        }

        return this.arguments.length;
    }

    /**
     *
     * Gets the sub-commands identifying string
     *
     * @return The name of the sub command
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     *
     * Gets the sender type for the sub command
     *
     * @return The sender type
     */
    public ForgeSenderType getSender() {
        return this.sender;
    }

    /**
     *
     * Determines if the command can be executed asynchronously or not. Defaults as true
     *
     * @return If it should be run off thread
     */
    public boolean isExecutedAsync() {
        return this.executeAsync;
    }

    /**
     *
     * Gets the cached number of required arguments for this command
     *
     * @return The number of required args
     */
    public int getRequiredArgs() {
        return this.requiredArgs;
    }

    /**
     *
     * Determines if the command sender specified can execute this command (based on permissions)
     *
     * @param sender The entity attempting to run the command
     * @return If they can execute the command
     */
    public boolean canExecute(ICommandSender sender) {
        if (this.requiredPermission == null || this.requiredPermission.isEmpty()) {
            return true;
        }

        if (!(sender instanceof EntityPlayerMP)) {
            return true;
        }

        return UtilPlayer.hasPermission((EntityPlayerMP) sender, this.requiredPermission);
    }

    /**
     *
     * Attempts to execute the command with the specified sender, and arguments.
     *
     * Will return false if it fails to execute or an error occurs during runtime.
     *
     * @param sender The entity that executed the command
     * @param arguments The arguments that have been passed from the entity
     * @return If the command failed to run
     */
    public boolean execute(ICommandSender sender, String[] arguments) {
        Object[] args = new Object[this.arguments.length];
        int subtract = 0;

        for (int i = 0; i < this.arguments.length; i++) {
            if ((i - subtract) >= arguments.length) {
                break;
            }

            ArgumentInjector<?, ICommandSender> argument = this.arguments[i];

            if (argument == null) {
                args[i] = null;
                ++subtract;
                continue;
            }

            if (argument.doesRequireMultipleArgs()) {
                String[] remainingArgs = Arrays.copyOfRange(arguments, i, arguments.length);

                args[i] = argument.instantiateClass(sender, remainingArgs);

                if (args[i] == null) {
                    return false;
                }
            } else {
                args[i] = argument.instantiateClass(sender, arguments[i - subtract]);

                if (args[i] == null) {
                    return false;
                }
            }
        }

        if (this.sender.getType().equals(sender.getClass())) {
            args[this.senderPosition] = sender;
        } else {
            try {
                args[this.senderPosition] = this.sender.getType().cast(sender);
            } catch (ClassCastException e) {
                FMLCommonHandler.instance().getFMLLogger().info("You cannot use this command from this source (player only).");
                return false;
            }
        }

        if (this.justArgsPos != -1) {
            args[this.justArgsPos] = arguments;
        }

        return this.execute(args);
    }

    private boolean execute(Object... args) {
        try {
            this.executor.invoke(this.commandClass, args);
            return true;
        } catch (Exception e) {
            Throwable cause = e.getCause();
            cause.printStackTrace(new PrintWriter(new Writer() {
                @Override
                public void write(char[] cbuf, int off, int len) {
                    FMLCommonHandler.instance().getFMLLogger().error(new String(cbuf));
                }

                @Override
                public void flush() {}

                @Override
                public void close() {}
            }));
        }

        return false;
    }
}
