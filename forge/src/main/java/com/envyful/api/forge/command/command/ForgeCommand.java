package com.envyful.api.forge.command.command;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.command.command.executor.CommandExecutor;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.player.util.UtilPlayer;
import com.google.common.collect.Lists;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        if (this.basePermission.isEmpty() || !(sender instanceof EntityPlayerMP)) {
            return true;
        }

        return UtilPlayer.hasPermission((EntityPlayerMP) sender, this.basePermission);
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

        if (args.length > 0) {
            for (ForgeCommand subCommand : this.subCommands) {
                if (this.fitsCommand(args[0], subCommand)) {
                    subCommand.executeSync(server, sender, Arrays.copyOfRange(args, 1, args.length));
                    return;
                }
            }
        }

        for (CommandExecutor executor : this.executors) {
            if (executor.getIdentifier().isEmpty()) {
                if (this.attemptRun(executor, sender, args)) {
                    return;
                }
            }

            if (args.length == 0) {
                continue;
            }

            if (!executor.getIdentifier().equalsIgnoreCase(args[0]) || (executor.getIdentifier().isEmpty() && !args[0].isEmpty())) {
                continue;
            }

            if (this.attemptRun(executor, sender, args)) {
                return;
            }
        }

        sender.sendMessage(new TextComponentString(this.getUsage(sender)));
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

        if (executor.getRequiredArgs() == (args.length + 1)) {
            if (!executor.isExecutedAsync()) {
                UtilForgeConcurrency.runSync(() -> {
                    if (executor.execute(sender, args)) {
                        return;
                    }

                    sender.sendMessage(new TextComponentString(this.getUsage(sender)));
                });
                return true;
            }

            return executor.execute(sender, args);
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

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (this.subCommands.size() == 0) {
            if (args.length == 0) {
                return this.getAllPlayers();
            } else {
                return this.getPlayers(sender, args[0]);
            }
        }

        if (args.length == 0) {
            return this.getAccessibleSubCommands(sender);
        }

        if (args.length == 1) {
            return this.getMatching(args[0], this.getAccessibleSubCommands(sender));
        }

        for (ForgeCommand subCommand : this.subCommands) {
            if (args[0].equalsIgnoreCase(subCommand.getName()) || subCommand.getAliases().contains(args[0])) {
                return subCommand.getTabCompletions(server, sender, args, pos);
            }
        }

        return Collections.emptyList();
    }

    protected List<String> getPlayers(ICommandSender sender, String name) {
        if (name.isEmpty()) {
            return this.getAllPlayers();
        }

        return this.getMatching(name, this.getAllPlayers());
    }

    protected List<String> getAllPlayers() {
        List<EntityPlayerMP> players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
        return players.stream().map(EntityPlayerMP::getName).collect(Collectors.toList());
    }

    protected List<String> getAccessibleSubCommands(ICommandSender sender) {
        List<String> subCommands = Lists.newArrayList();

        for (ForgeCommand subCommand : this.subCommands) {
            if (!(sender instanceof EntityPlayerMP)
                    || subCommand.checkPermission(FMLCommonHandler.instance().getMinecraftServerInstance(), sender)) {
                subCommands.add(subCommand.name);
                subCommands.addAll(subCommand.aliases);
            }
        }

        return subCommands;
    }

    protected List<String> getMatching(String arg, List<String> potential) {
        List<String> args = Lists.newArrayList();

        for (String s : potential) {
            if (s.toLowerCase().startsWith(arg.toLowerCase())) {
                args.add(s);
            }
        }

        return args;
    }
}
