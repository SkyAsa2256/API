package com.envyful.api.forge.command;

import com.envyful.api.command.CommandFactory;
import com.envyful.api.command.CommandParser;
import com.envyful.api.command.InjectedCommandFactory;
import com.envyful.api.command.PlatformCommand;
import com.envyful.api.command.exception.CommandParseException;
import com.envyful.api.command.sender.SenderTypeFactory;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.forge.command.command.ForgePlatformCommand;
import com.envyful.api.forge.command.command.sender.ConsoleSenderType;
import com.envyful.api.forge.command.command.sender.ForgePlayerSenderType;
import com.envyful.api.forge.command.command.sender.MessageableSenderType;
import com.envyful.api.forge.command.completion.number.IntegerTabCompleter;
import com.envyful.api.forge.command.completion.player.PlayerTabCompleter;
import com.envyful.api.forge.command.injector.ForgeFunctionInjector;
import com.envyful.api.forge.command.parser.ForgeAnnotationCommandParser;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.platform.PlatformProxy;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
public class ForgeCommandFactory extends InjectedCommandFactory<CommandDispatcher<CommandSourceStack>, CommandSource> {

    public ForgeCommandFactory(ForgePlayerManager playerManager) {
        this(ForgeAnnotationCommandParser::new, playerManager);
    }

    public ForgeCommandFactory() {
        this(ForgeAnnotationCommandParser::new);
    }

    public ForgeCommandFactory(
            Function<InjectedCommandFactory<CommandDispatcher<CommandSourceStack>, CommandSource>, ? extends CommandParser<? extends PlatformCommand<CommandSource>, CommandSource>> commandParser) {
        this(commandParser, null);
    }

    public ForgeCommandFactory(
            Function<InjectedCommandFactory<CommandDispatcher<CommandSourceStack>, CommandSource>, ? extends CommandParser<? extends PlatformCommand<CommandSource>, CommandSource>> commandParser,
            @Nullable ForgePlayerManager playerManager) {
        super(commandParser);

        SenderTypeFactory.register(new ConsoleSenderType(), new ForgePlayerSenderType(), new MessageableSenderType(playerManager));

        if (playerManager != null) {
            SenderTypeFactory.register(new ForgeEnvyPlayerSenderType(playerManager));
            this.registerInjector(ForgeEnvyPlayer.class, (sender, args) -> {
                var player = playerManager.getOnlinePlayer(args[0]);

                if (player == null) {
                    sender.sendSystemMessage(PlatformProxy.parse("&c&l(!) &cCannot find player with name " + args[0]));
                }

                return player;
            });
        }

        this.registerInjector(ServerPlayer.class, (sender, args) -> ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(args[0]));
        this.registerCompleter(new IntegerTabCompleter());
        this.registerCompleter(new PlayerTabCompleter());
    }

    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> registrar, PlatformCommand<CommandSource> command) {
        if (!(command instanceof ForgePlatformCommand)) {
            return;
        }

        LiteralCommandNode<CommandSourceStack> args = registrar.register(
                Commands.literal(command.getName())
                        .requires(commandSource -> true)
                        .then(Commands.argument("", StringArgumentType.greedyString())
                                .suggests((context, builder) -> buildSuggestions((ForgePlatformCommand) command, context, builder))
                                .executes(context -> {
                                    command.execute(context.getSource().source, this.getArgs(context));
                                    return 1;
                                }))
                        .executes(context -> {
                            command.execute(context.getSource().source, new String[0]);
                            return 1;
                        })
        );

        for (String alias : command.getAliases()) {
            registrar.getRoot().addChild(buildRedirect(alias, args));
        }
    }

    @Override
    public PlatformCommand.Builder<CommandSource> commandBuilder() {
        return ForgePlatformCommand.builder();
    }

    @Override
    public PlatformCommand<CommandSource> parseCommand(Object o) throws CommandParseException {
        return this.commandParser.parseCommand(o);
    }

    /**
     * Returns a literal node that redirects its execution to
     * the given destination node.
     *
     * @param alias the command alias
     * @param destination the destination node
     * @return the built node
     */
    public static LiteralCommandNode<CommandSourceStack> buildRedirect(
            final String alias, final LiteralCommandNode<CommandSourceStack> destination) {
        // Redirects only work for nodes with children, but break the top argument-less command.
        // Manually adding the root command after setting the redirect doesn't fix it.
        // See https://github.com/Mojang/brigadier/issues/46). Manually clone the node instead.
        LiteralArgumentBuilder<CommandSourceStack> builder = LiteralArgumentBuilder
                .<CommandSourceStack>literal(alias.toLowerCase(Locale.ENGLISH))
                .requires(destination.getRequirement())
                .forward(
                        destination.getRedirect(), destination.getRedirectModifier(), destination.isFork())
                .executes(destination.getCommand());
        for (CommandNode<CommandSourceStack> child : destination.getChildren()) {
            builder.then(child);
        }
        return builder.build();
    }

    private String[] getArgs(CommandContext<CommandSourceStack> context) {
        String[] args = context.getInput().split(" ");
        if (context.getInput().endsWith(" ")) {
            args = Arrays.copyOf(args, args.length + 1);
            args[args.length - 1] = "";
        }
        return Arrays.copyOfRange(args, 1, args.length);
    }

    private CompletableFuture<Suggestions> buildSuggestions(ForgePlatformCommand command, CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        String[] args = this.getArgs(context);
        return command.getTabCompletions(context.getSource().source, args)
                .exceptionally(throwable -> {
                    UtilLogger.getLogger().error("Error when tab completing command", throwable);
                    return new ArrayList<>();
                })
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
        this.registeredInjectors.add(new ForgeFunctionInjector<>(parentClass, multipleArgs, function));
    }
}
