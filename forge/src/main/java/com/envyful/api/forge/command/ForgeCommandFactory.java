package com.envyful.api.forge.command;

import com.envyful.api.command.CommandFactory;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.exception.CommandLoadException;
import com.envyful.api.command.injector.ArgumentInjector;
import com.envyful.api.forge.command.command.ForgeCommand;
import com.envyful.api.forge.command.command.ForgeSenderType;
import com.envyful.api.forge.command.command.executor.CommandExecutor;
import com.envyful.api.forge.command.injector.ForgeFunctionInjector;
import com.google.common.collect.Lists;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 *
 * Forge implementation of the {@link CommandFactory} interface
 *
 */
public class ForgeCommandFactory implements CommandFactory<MinecraftServer, ICommandSender> {

    private final List<ArgumentInjector<?, ICommandSender>> registeredInjectors = Lists.newArrayList();

    public ForgeCommandFactory() {
        this.registerInjector(EntityPlayerMP.class, (sender, args) -> sender.getServer().getPlayerList().getPlayerByUsername(args[0]));
        this.registerInjector(int.class, (iCommandSender, args) -> Integer.parseInt(args[0]));
        this.registerInjector(String.class, (iCommandSender, args) -> args[0]);
        this.registerInjector(double.class, ((iCommandSender, args) -> Double.parseDouble(args[0])));
        this.registerInjector(long.class, ((iCommandSender, args) -> Long.parseLong(args[0])));
    }

    @Override
    public boolean registerCommand(MinecraftServer server, Object o) throws CommandLoadException {
        ForgeCommand command = this.createCommand(o.getClass(), o);
        ((CommandHandler) server.getCommandManager()).registerCommand(command);
        return true;
    }

    private ForgeCommand createCommand(Class<?> clazz) throws CommandLoadException {
        return this.createCommand(clazz, null);
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    private ForgeCommand createCommand(Class<?> clazz, Object instance) throws CommandLoadException {
        List<ForgeCommand> subCommands = this.getSubCommands(clazz);
        Command commandData = clazz.getAnnotation(Command.class);

        if (commandData == null) {
            throw new CommandLoadException(clazz.getSimpleName(), "missing @Command annotation on class!");
        }

        String defaultPermission = this.getDefaultPermission(clazz);

        if (instance  == null) {
            instance = this.createInstance(clazz);

            if (instance == null) {
                throw new CommandLoadException(clazz.getSimpleName(), "cannot instantiate sub-command as there's no public constructor");
            }
        }

        List<CommandExecutor> subExecutors = Lists.newArrayList();

        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            CommandProcessor processorData = declaredMethod.getAnnotation(CommandProcessor.class);

            if (processorData == null) {
                continue;
            }

            String requiredPermission = this.getPermission(declaredMethod);
            List<ArgumentInjector<?, ICommandSender>> arguments = Lists.newArrayList();
            Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
            Annotation[][] annotations = declaredMethod.getParameterAnnotations();
            ForgeSenderType senderType = null;
            int senderPosition = -1;
            int justArgsPos = -1;

            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == String[].class) {
                    arguments.add(null);
                    justArgsPos = i;
                    continue;
                }

                if (annotations.length <= i) {
                    continue;
                }

                if (annotations[i][0] instanceof Sender) {
                    senderType = ForgeSenderType.get(parameterTypes[i]);
                    senderPosition = i;
                    arguments.add(null);
                } else {
                    arguments.add(this.getInjectorFor(parameterTypes[i]));
                }
            }

            if (senderType == null) {
                throw new CommandLoadException(clazz.getSimpleName(), "Command must have a sender!");
            }

            subExecutors.add(new CommandExecutor(processorData.value(), senderType, senderPosition, instance, declaredMethod,
                    processorData.executeAsync(), justArgsPos, requiredPermission,
                    arguments.toArray(new ForgeFunctionInjector<?>[0])));
        }

        return new ForgeCommand(this, commandData.value(), commandData.description(), defaultPermission,
                Arrays.asList(commandData.aliases()), subExecutors, subCommands);
    }

    private String getPermission(Method method) {
        Permissible permissible = method.getAnnotation(Permissible.class);

        if (permissible == null) {
            return "";
        }

        return permissible.value();
    }

    private Object createInstance(Class<?> clazz) {
        if (clazz.getConstructors().length == 0) {
            try {
                return clazz.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }

        for (Constructor<?> constructor : clazz.getConstructors()) {
            List<Object> objects = Lists.newArrayList();

            for (Class<?> parameterType : constructor.getParameterTypes()) {
                Object o = this.getInjectorFor(parameterType).instantiateClass(null);

                if (o == null) {
                    break;
                }

                objects.add(o);
            }

            if (objects.size() != constructor.getParameterTypes().length) {
                continue;
            }

            try {
                return constructor.newInstance(objects.toArray(new Object[0]));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private String getDefaultPermission(Class<?> clazz) {
        Permissible permissible = clazz.getAnnotation(Permissible.class);

        if (permissible == null) {
            return "";
        }

        return permissible.value();
    }

    private List<ForgeCommand> getSubCommands(Class<?> clazz) {
        SubCommands subCommands = clazz.getAnnotation(SubCommands.class);

        if (subCommands == null) {
            return Collections.emptyList();
        }

        List<ForgeCommand> commands = Lists.newArrayList();

        for (Class<?> subClazz : subCommands.value()) {
            commands.add(this.createCommand(subClazz));
        }

        return commands;
    }

    private ArgumentInjector<?, ICommandSender> getInjectorFor(Class<?> clazz) {
        for (ArgumentInjector<?, ICommandSender> registeredInjector : this.registeredInjectors) {
            if (registeredInjector.getConvertedClass().isAssignableFrom(clazz)) {
                return registeredInjector;
            }
        }

        return null;
    }

    @Override
    public boolean unregisterCommand(MinecraftServer server, Object o) {
        return false;
    }

    @Override
    public void registerInjector(Class<?> parentClass, boolean multipleArgs, BiFunction<ICommandSender, String[], ?> function) {
        this.registeredInjectors.add(new ForgeFunctionInjector(parentClass, multipleArgs, function));
    }

    @Override
    public void unregisterInjector(Class<?> parentClass) {
        this.registeredInjectors.removeIf(next -> Objects.equals(parentClass, next.getConvertedClass()));
    }
}
