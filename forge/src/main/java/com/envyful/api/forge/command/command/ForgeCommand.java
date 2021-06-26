package com.envyful.api.forge.command.command;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.command.command.executor.CommandExecutor;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

/**
 *
 * The forge command implementation for registering to the server. Handles checking all sub commands
 * and running all commands from the forge implementation.
 *
 */
@MethodsReturnNonnullByDefault
public class ForgeCommand extends CommandBase {

    private static final ITextComponent NO_PERMISSION = new TextComponentString("§c§l(!) §cNo permission!");

    private final ForgeCommandFactory commandFactory;
    private final String name;
    private final String description;
    private final String basePermission;
    private final List<String> aliases;
    private final List<CommandExecutor> executors;
    private final List<ForgeCommand> subCommands;

    public ForgeCommand(ForgeCommandFactory commandFactory, String name, String description, String basePermission,
                        List<String> aliases, List<CommandExecutor> executors, List<ForgeCommand> subCommands) {
        this.commandFactory = commandFactory;
        this.name = name;
        this.description = description;
        this.basePermission = basePermission;
        this.aliases = aliases;
        this.executors = executors;
        this.subCommands = subCommands;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), this.basePermission);
    }

    @Override
    @ParametersAreNonnullByDefault
    public String getUsage(ICommandSender sender) {
        return this.description;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        UtilConcurrency.runAsync(() -> this.executeSync(server, sender, args));
    }

    public void executeSync(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!this.checkPermission(server, sender)) {
            sender.sendMessage(NO_PERMISSION);
            return;
        }

        for (ForgeCommand subCommand : this.subCommands) {
            if (this.fitsCommand(args[0], subCommand)) {
                subCommand.executeSync(server, sender, Arrays.copyOfRange(args, 1, args.length));
                return;
            }
        }

        for (CommandExecutor executor : this.executors) {
            if (executor.getIdentifier().isEmpty() && args.length == 0) {
                this.attemptRun(executor, sender, args);
                return;
            }

            if (!executor.getIdentifier().equalsIgnoreCase(args[0]) || (executor.getIdentifier().isEmpty() && !args[0].isEmpty())) {
                continue;
            }

            if (this.attemptRun(executor, sender, args)) {
                return;
            }
        }

        this.getUsage(sender);
    }

    private boolean attemptRun(CommandExecutor executor, ICommandSender sender, String[] args) {
        if (!executor.canExecute(sender)) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }

        if (executor.getRequiredArgs() == -1 || executor.getRequiredArgs() == 0) {
            if (!executor.isExecutedAsync()) {
                UtilForgeConcurrency.runSync(() -> executor.execute(sender, args));
                return true;
            }

            return executor.execute(sender, args);
        }

        String[] newArgs = Arrays.copyOfRange(args, 0, args.length - 1);

        if (executor.getRequiredArgs() == newArgs.length) {
            if (!executor.isExecutedAsync()) {
                UtilForgeConcurrency.runSync(() -> executor.execute(sender, newArgs));
                return true;
            }

            return executor.execute(sender, newArgs);
        }

        return false;
    }

    private boolean fitsCommand(String arg, ForgeCommand subCommand) {
        if (subCommand.getName().equalsIgnoreCase(arg)) {
            return true;
        }

        for (String alias : subCommand.getAliases()) {
            if (alias.equalsIgnoreCase(arg)) {
                return true;
            }
        }

        return false;
    }
}
