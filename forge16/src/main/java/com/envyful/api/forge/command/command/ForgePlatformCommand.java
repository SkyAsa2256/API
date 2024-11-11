package com.envyful.api.forge.command.command;

import com.envyful.api.command.PlatformCommand;
import com.envyful.api.command.PlatformCommandExecutor;
import com.envyful.api.command.tab.TabHandler;
import com.envyful.api.forge.chat.UtilChatColour;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class ForgePlatformCommand extends PlatformCommand<ICommandSource> {

    protected ForgePlatformCommand(Builder builder) {
        super(builder);
    }

    @Override
    protected void sendSystemMessage(ICommandSource sender, List<String> message) {
        for (var encodedMessage : UtilChatColour.colour(message)) {
            sender.sendMessage(encodedMessage, Util.NIL_UUID);
        }
    }

    @Override
    protected List<String> getOnlinePlayerNames() {
        return List.of(ServerLifecycleHooks.getCurrentServer().getPlayerNames());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends PlatformCommand.Builder<ICommandSource> {

        private Builder() {
            super();
        }

        @Override
        public Builder name(String name) {
            return (Builder) super.name(name);
        }

        @Override
        public Builder descriptionProvider(BiFunction<ICommandSource, List<String>, List<String>> descriptionProvider) {
            return (Builder) super.descriptionProvider(descriptionProvider);
        }

        @Override
        public Builder permissionCheck(BiPredicate<ICommandSource, List<String>> permissionCheck) {
            return (Builder) super.permissionCheck(permissionCheck);
        }

        @Override
        public Builder noPermissionProvider(Function<ICommandSource, List<String>> noPermissionProvider) {
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
        public Builder tabHandler(TabHandler<ICommandSource> tabHandler) {
            return (Builder) super.tabHandler(tabHandler);
        }

        @Override
        public Builder subCommands(List<PlatformCommand<ICommandSource>> subCommands) {
            return (Builder) super.subCommands(subCommands);
        }

        @Override
        public PlatformCommand<ICommandSource> build() {
            return this.build(builder -> new ForgePlatformCommand((Builder) builder));
        }
    }
}
