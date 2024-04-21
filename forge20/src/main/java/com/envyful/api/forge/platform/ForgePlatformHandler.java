package com.envyful.api.forge.platform;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.forge.InitializationTask;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.api.forge.server.UtilForgeServer;
import com.envyful.api.platform.PlatformHandler;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.PermissionAPI;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

public class ForgePlatformHandler implements PlatformHandler<CommandSource> {

    private static final ForgePlatformHandler INSTANCE = new ForgePlatformHandler();

    private ForgePlatformHandler() {
        ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getClasses)
                .flatMap(Collection::stream)
                .filter(classData -> classData.interfaces().contains(Type.getType(InitializationTask.class)))
                .forEach(classData -> {
                    try {
                        var clazz = Class.forName(classData.clazz().getClassName());
                        var constructor = clazz.getConstructor();
                        var initializationTask = (InitializationTask) constructor.newInstance();

                        initializationTask.run();
                    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                             IllegalAccessException | IllegalArgumentException |
                             InvocationTargetException e) {
                        UtilLogger.logger().ifPresent(logger -> logger.error("Error loading class", e));
                    }
                });
    }

    public static PlatformHandler<CommandSource> getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean hasPermission(CommandSource player, String permission) {
        if (player == null || permission == null) {
            return false;
        }

        return (isOP(player) || PermissionAPI.getPermission((ServerPlayer) player, UtilPlayer.getPermission(permission)));
    }

    private boolean isOP(CommandSource sender) {
        if (!(sender instanceof Player)) {
            return true;
        }

        ServerOpListEntry entry = ServerLifecycleHooks.getCurrentServer().getPlayerList().getOps().get(((Player) sender).getGameProfile());
        return entry != null;
    }

    @Override
    public void broadcastMessage(Collection<String> message, Placeholder... placeholders) {
        for (var parsedMessage : UtilChatColour.colour(message, placeholders)) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastSystemMessage(parsedMessage, false);
        }
    }

    @Override
    public void sendMessage(CommandSource player, Collection<String> message, Placeholder... placeholders) {
        for (var parsedMessage : UtilChatColour.colour(message, placeholders)) {
            player.sendSystemMessage(parsedMessage);
        }
    }

    @Override
    public void runSync(Runnable runnable) {
        ServerLifecycleHooks.getCurrentServer().execute(runnable);
    }

    @Override
    public void runLater(Runnable runnable, int delayTicks) {
        ServerLifecycleHooks.getCurrentServer().tell(new TickTask(
                        ServerLifecycleHooks.getCurrentServer().getTickCount() + delayTicks,
                runnable));
    }

    @Override
    public double getTPS() {
        return ServerLifecycleHooks.getCurrentServer().getAverageTickTime();
    }

    @Override
    public void executeConsoleCommands(List<String> commands, Placeholder... placeholders) {
        for (String command : commands) {
            for (String handlePlaceholder : PlaceholderFactory.handlePlaceholders(command, placeholders)) {
                UtilForgeServer.executeCommand(handlePlaceholder);
            }
        }
    }
}
