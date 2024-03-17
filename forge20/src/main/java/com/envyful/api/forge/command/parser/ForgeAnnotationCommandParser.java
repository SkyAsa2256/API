package com.envyful.api.forge.command.parser;

import com.envyful.api.command.CommandFactory;
import com.envyful.api.command.annotate.AnnotationCommandParser;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.command.annotate.permission.PermissionHandler;
import com.envyful.api.command.exception.CommandParseException;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.forge.command.command.ForgePlatformCommand;
import com.envyful.api.forge.player.util.UtilPlayer;
import net.minecraft.commands.CommandSource;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiPredicate;

public class ForgeAnnotationCommandParser extends AnnotationCommandParser<ForgePlatformCommand, CommandSource> {

    public ForgeAnnotationCommandParser(CommandFactory<?, CommandSource> commandFactory) {
        super(commandFactory, CommandSource.class);
    }

    @Override
    protected BiPredicate<CommandSource, List<String>> getPermissionCheck(Object o) {
        Permissible permissible = o.getClass().getAnnotation(Permissible.class);

        if (permissible != null) {
            UtilPlayer.registerPermission(permissible.value());
            return (sender, args) -> this.commandFactory.hasPermission(sender, permissible.value());
        }

        for (Method declaredMethod : o.getClass().getDeclaredMethods()) {
            PermissionHandler annotation = declaredMethod.getAnnotation(PermissionHandler.class);

            if (annotation == null) {
                continue;
            }

            if (!this.isPermissionHandlerMethod(declaredMethod)) {
                throw new CommandParseException("Method " + declaredMethod.getName() + " in class " + o.getClass().getName() + " is not a valid permission handler method");
            }

            return (sender, args) -> {
                try {
                    return (boolean) declaredMethod.invoke(o, sender, args);
                } catch (Exception e) {
                    UtilLogger.logger().ifPresent(logger -> logger.error("Error occurred when performing permission check for command " + o.getClass().getSimpleName(), e));
                }

                return false;
            };
        }

        return null;
    }
}
