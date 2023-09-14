package com.envyful.api.velocity.player.command;

import com.envyful.api.command.CommandFactory;
import com.envyful.api.command.CommandParser;
import com.envyful.api.command.InjectedCommandFactory;
import com.envyful.api.command.PlatformCommand;
import com.envyful.api.command.exception.CommandParseException;
import com.envyful.api.command.sender.SenderTypeFactory;
import com.envyful.api.velocity.player.VelocityPlayerManager;
import com.envyful.api.velocity.player.command.command.VelocityPlatformCommand;
import com.envyful.api.velocity.player.command.command.sender.ConsoleSenderType;
import com.envyful.api.velocity.player.command.command.sender.VelocityPlayerSenderType;
import com.envyful.api.velocity.player.command.completion.number.IntegerTabCompleter;
import com.envyful.api.velocity.player.command.completion.player.PlayerTabCompleter;
import com.envyful.api.velocity.player.command.injector.VelocityFunctionInjector;
import com.envyful.api.velocity.player.util.UtilPlayer;
import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 * Forge implementation of the {@link CommandFactory} interface
 *
 */
public class VelocityCommandFactory extends InjectedCommandFactory<CommandManager, CommandSource> {

    private ProxyServer proxyServer;

    public VelocityCommandFactory(ProxyServer proxyServer,
            Function<InjectedCommandFactory<CommandManager, CommandSource>, CommandParser<PlatformCommand<CommandSource>, CommandSource>> commandParser) {
        this(proxyServer, commandParser, null);
    }

    public VelocityCommandFactory(ProxyServer proxyServer,
            Function<InjectedCommandFactory<CommandManager, CommandSource>, ? extends CommandParser<? extends PlatformCommand<CommandSource>, CommandSource>> commandParser,
            @Nullable VelocityPlayerManager playerManager) {
        super(commandParser);

        this.proxyServer = proxyServer;

        SenderTypeFactory.register(new ConsoleSenderType(), new VelocityPlayerSenderType());

        if (playerManager != null) {
            SenderTypeFactory.register(new VelocityEnvyPlayerSenderType(playerManager));
        }

        this.registerInjector(Player.class, (sender, args) -> proxyServer.getPlayer(args[0]).orElse(null));
        this.registerCompleter(new IntegerTabCompleter());
        this.registerCompleter(new PlayerTabCompleter(proxyServer));
    }

    @Override
    public void registerCommand(CommandManager registrar, PlatformCommand<CommandSource> command) {
        if (!(command instanceof VelocityPlatformCommand)) {
            return;
        }

        ((VelocityPlatformCommand) command).proxy = this.proxyServer;

        BrigadierCommand brigadierCommand = new BrigadierCommand(LiteralArgumentBuilder.<CommandSource>literal(command.getName())
                .requires(commandSource -> true)
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("", StringArgumentType.greedyString())
                        .suggests((context, builder) -> this.buildSuggestions((VelocityPlatformCommand) command, context, builder))
                        .executes(context -> {
                            command.execute(context.getSource(), this.getArgs(context));
                            return 1;
                        }))
                .executes(context -> {
                    command.execute(context.getSource(), new String[0]);
                    return 1;
                }));

        registrar.register(brigadierCommand);

        for (String alias : command.getAliases()) {
            registrar.register(alias, brigadierCommand);
        }
    }

    @Override
    public PlatformCommand.Builder<CommandSource> commandBuilder() {
        return VelocityPlatformCommand.builder();
    }

    @Override
    public PlatformCommand<CommandSource> parseCommand(Object o) throws CommandParseException {
        return this.commandParser.parseCommand(o);
    }

    @Override
    public boolean hasPermission(CommandSource sender, String permission) {
        if (!(sender instanceof Player)) {
            return true;
        }

        return UtilPlayer.hasPermission(sender, permission);
    }

    /**
     * Returns a literal node that redirects its execution to
     * the given destination node.
     *
     * @param alias the command alias
     * @param destination the destination node
     * @return the built node
     */
    public static LiteralCommandNode<CommandManager> buildRedirect(
            final String alias, final LiteralCommandNode<CommandManager> destination) {
        // Redirects only work for nodes with children, but break the top argument-less command.
        // Manually adding the root command after setting the redirect doesn't fix it.
        // See https://github.com/Mojang/brigadier/issues/46). Manually clone the node instead.
        LiteralArgumentBuilder<CommandManager> builder = LiteralArgumentBuilder
                .<CommandManager>literal(alias.toLowerCase(Locale.ENGLISH))
                .requires(destination.getRequirement())
                .forward(
                        destination.getRedirect(), destination.getRedirectModifier(), destination.isFork())
                .executes(destination.getCommand());
        for (CommandNode<CommandManager> child : destination.getChildren()) {
            builder.then(child);
        }
        return builder.build();
    }

    private String[] getArgs(CommandContext<CommandSource> context) {
        String[] args = context.getInput().split(" ");
        if (context.getInput().endsWith(" ")) {
            args = Arrays.copyOf(args, args.length + 1);
            args[args.length - 1] = "";
        }
        return Arrays.copyOfRange(args, 1, args.length);
    }

    private CompletableFuture<Suggestions> buildSuggestions(VelocityPlatformCommand command, CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        String[] args = this.getArgs(context);
        return command.getTabCompletions(context.getSource(), args)
                .exceptionally(throwable -> Lists.newArrayList())
                .thenApply(completions -> {
                    int lastArgPos = context.getInput().lastIndexOf(' ') + 1;
                    String lastArg = context.getInput().endsWith(" ") ? "" : args[args.length - 1];

                    SuggestionsBuilder updatedBuilder = builder.createOffset(lastArgPos);

                    for (String completion : completions) {
                        if (!lastArg.isBlank() && !completion.toLowerCase(Locale.ROOT).contains(lastArg.toLowerCase(Locale.ROOT))) {
                            continue;
                        }

                        updatedBuilder.suggest(completion);
                    }

                    return updatedBuilder.build();
                });
    }

    @Override
    public <C> void registerInjector(Class<C> parentClass, boolean multipleArgs, BiFunction<CommandSource, String[], C> function) {
        this.registeredInjectors.add(new VelocityFunctionInjector<>(parentClass, multipleArgs, function));
    }
}
