package com.envyful.api.forge.command;

import com.envyful.api.command.CommandFactory;
import com.envyful.api.command.CommandParser;
import com.envyful.api.command.InjectedCommandFactory;
import com.envyful.api.command.PlatformCommand;
import com.envyful.api.command.exception.CommandParseException;
import com.envyful.api.command.sender.SenderTypeFactory;
import com.envyful.api.forge.command.command.ForgePlatformCommand;
import com.envyful.api.forge.command.command.sender.ConsoleSenderType;
import com.envyful.api.forge.command.command.sender.ForgePlayerSenderType;
import com.envyful.api.forge.command.completion.number.IntegerTabCompleter;
import com.envyful.api.forge.command.completion.player.PlayerTabCompleter;
import com.envyful.api.forge.command.injector.ForgeFunctionInjector;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.forge.player.util.UtilPlayer;
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
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;

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
public class ForgeCommandFactory extends InjectedCommandFactory<CommandDispatcher<CommandSourceStack>, CommandSource> {

    public ForgeCommandFactory(
            Function<InjectedCommandFactory<CommandDispatcher<CommandSourceStack>, CommandSource>, CommandParser<PlatformCommand<CommandSource>, CommandSource>> commandParser) {
        this(commandParser, null);
    }

    public ForgeCommandFactory(
            Function<InjectedCommandFactory<CommandDispatcher<CommandSourceStack>, CommandSource>, ? extends CommandParser<? extends PlatformCommand<CommandSource>, CommandSource>> commandParser,
            @Nullable ForgePlayerManager playerManager) {
        super(commandParser);

        SenderTypeFactory.register(new ConsoleSenderType(), new ForgePlayerSenderType());

        if (playerManager != null) {
            SenderTypeFactory.register(new ForgeEnvyPlayerSenderType(playerManager));
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

    @Override
    public boolean hasPermission(CommandSource sender, String permission) {
        if (!(sender instanceof Player)) {
            return true;
        }

        return UtilPlayer.hasPermission((ServerPlayer) sender, permission);
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
        return Arrays.copyOfRange(args, 1, args.length);
    }

    private CompletableFuture<Suggestions> buildSuggestions(ForgePlatformCommand command, CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        String[] args = this.getArgs(context);
        return command.getTabCompletions(context.getSource().source, args).thenApply(completions -> builder.build());

//        List<String> tabCompletions;
//        String[] initialArgs = context.getInput().split(" ");
//        List<String> args = Lists.newArrayList(Arrays.copyOfRange(initialArgs, 1, initialArgs.length));
//        int spaces = 0;
//        Matcher matcher = SPACE_PATTERN.matcher(context.getInput());
//
//        while (matcher.find()) {
//            spaces++;
//        }
//
//        while (spaces > args.size()) {
//            args.add(" ");
//            spaces--;
//        }
//
//        tabCompletions = command.getTabCompletions(context.getSource().getServer(),
//                context.getSource().getEntity(),
//                args.toArray(new String[0]),
//                new BlockPos((int) context.getSource().getPosition().x, (int) context.getSource().getPosition().y, (int) context.getSource().getPosition().z));
//
//        if (args.size() > 0 && !args.get(args.size() - 1).trim().isEmpty()) {
//            builder = builder.createOffset(context.getInput().length() - args.get(args.size() - 1).length());
//        } else {
//            builder = builder.createOffset(context.getInput().length());
//        }
//
//        for (String tabCompletion : tabCompletions) {
//            if (args.isEmpty()) {
//                builder.suggest(tabCompletion);
//                continue;
//            }
//
//            String currentWord = args.get(args.size() - 1);
//
//            if (currentWord.isEmpty() || currentWord.equals(" ")) {
//                builder.suggest(tabCompletion);
//                continue;
//            }
//
//            if (!tabCompletion.toLowerCase().startsWith(currentWord.toLowerCase())) {
//                continue;
//            }
//
//            builder.suggest(tabCompletion);
//        }
//
//        return builder.buildFuture();
    }

    @Override
    public <C> void registerInjector(Class<C> parentClass, boolean multipleArgs, BiFunction<CommandSource, String[], C> function) {
        this.registeredInjectors.add(new ForgeFunctionInjector<>(parentClass, multipleArgs, function));
    }
}
