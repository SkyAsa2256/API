package com.envyful.api.velocity.player.command.command;

import com.envyful.api.command.PlatformCommand;
import com.envyful.api.command.PlatformCommandExecutor;
import com.envyful.api.command.tab.TabHandler;
import com.google.common.collect.Lists;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * A platform command for the Velocity platform
 *
 */
public class VelocityPlatformCommand extends PlatformCommand<CommandSource> {

    public ProxyServer proxy;

    protected VelocityPlatformCommand(Builder builder) {
        super(builder);
    }

    @Override
    protected void sendSystemMessage(CommandSource sender, List<String> message) {
        for (String s : message) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(s));
        }
    }

    @Override
    protected List<String> getOnlinePlayerNames() {
        return Lists.newArrayList(this.proxy.getAllPlayers().stream().map(Player::getUsername).collect(Collectors.toList()));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends PlatformCommand.Builder<CommandSource> {

        private Builder() {
            super();
        }

        @Override
        public Builder name(String name) {
            return (Builder) super.name(name);
        }

        @Override
        public Builder descriptionProvider(BiFunction<CommandSource, List<String>, List<String>> descriptionProvider) {
            return (Builder) super.descriptionProvider(descriptionProvider);
        }

        @Override
        public Builder permissionCheck(BiPredicate<CommandSource, List<String>> permissionCheck) {
            return (Builder) super.permissionCheck(permissionCheck);
        }

        @Override
        public Builder noPermissionProvider(Function<CommandSource, List<String>> noPermissionProvider) {
            return (Builder) super.noPermissionProvider(noPermissionProvider);
        }

        @Override
        public Builder aliases(List<String> aliases) {
            return (Builder) super.aliases(aliases);
        }

        @Override
        public Builder executor(PlatformCommandExecutor executor) {
            return (Builder) super.executor(executor);
        }

        @Override
        public Builder tabHandler(TabHandler<CommandSource> tabHandler) {
            return (Builder) super.tabHandler(tabHandler);
        }

        @Override
        public Builder subCommands(List<PlatformCommand<CommandSource>> subCommands) {
            return (Builder) super.subCommands(subCommands);
        }

        @Override
        public PlatformCommand<CommandSource> build() {
            return this.build(builder -> new VelocityPlatformCommand((Builder) builder));
        }
    }
}
