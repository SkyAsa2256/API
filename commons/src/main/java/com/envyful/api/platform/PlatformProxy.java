package com.envyful.api.platform;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.text.Placeholder;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

public class PlatformProxy {

    private static PlatformHandler handler;
    private static PlayerManager<?, ?> playerManager;

    public static void setHandler(PlatformHandler handler) {
        PlatformProxy.handler = handler;
    }

    public static void setPlayerManager(PlayerManager<?, ?> playerManager) {
        PlatformProxy.playerManager = playerManager;
    }

    public static PlayerManager<?, ?> getPlayerManager() {
        return playerManager;
    }

    public static void executeConsoleCommands(String... commands) {
        executeConsoleCommands(Lists.newArrayList(commands));
    }

    public static void executeConsoleCommands(List<String> commands, Placeholder... placeholders) {
        if (handler == null) {
            UtilLogger.logger().ifPresent(logger -> logger.error("No platform handler set but executeConsoleCommands was called"));
            return;
        }

        handler.executeConsoleCommands(commands, placeholders);
    }

    public static boolean hasPermission(EnvyPlayer<?> player, String permission) {
        if (handler == null) {
            UtilLogger.logger().ifPresent(logger -> logger.error("No platform handler set but broadcastMessage was called"));
            return false;
        }

        return handler.hasPermission(player.getParent(), permission);
    }

    public static void broadcastMessage(Collection<String> message, Placeholder... placeholders) {
        if (handler == null) {
            UtilLogger.logger().ifPresent(logger -> logger.error("No platform handler set but broadcastMessage was called"));
            return;
        }

        handler.broadcastMessage(message, placeholders);
    }

    public static void sendMessage(EnvyPlayer<?> player, String... message) {
        sendMessage(player.getParent(), Lists.newArrayList(message));
    }

    public static void sendMessage(Object player, String... message) {
        sendMessage(player, Lists.newArrayList(message));
    }

    public static void sendMessage(EnvyPlayer<?> player, String message, Placeholder... placeholders) {
        sendMessage(player.getParent(), message, placeholders);
    }

    public static void sendMessage(Object player, String message, Placeholder... placeholders) {
        sendMessage(player, Lists.newArrayList(message), placeholders);
    }

    public static void sendMessage(EnvyPlayer<?> player, Collection<String> message, Placeholder... placeholders) {
        sendMessage(player.getParent(), message, placeholders);
    }

    public static void sendMessage(Object player, Collection<String> message, Placeholder... placeholders) {
        if (handler == null) {
            UtilLogger.logger().ifPresent(logger -> logger.error("No platform handler set but sendMessage was called"));
            return;
        }

        handler.sendMessage(player, message, placeholders);
    }

    public static void runSync(Runnable runnable) {
        if (handler == null) {
            UtilLogger.logger().ifPresent(logger -> logger.error("No platform handler set but runSync was called"));
            return;
        }

        handler.runSync(runnable);
    }

    public static void runLater(Runnable runnable, int delayTicks) {
        if (handler == null) {
            UtilLogger.logger().ifPresent(logger -> logger.error("No platform handler set but runLater was called"));
            return;
        }

        handler.runLater(runnable, delayTicks);
    }

    public static double getTPS() {
        if (handler == null) {
            UtilLogger.logger().ifPresent(logger -> logger.error("No platform handler set but getTPS was called"));
            return 0.0;
        }

        return handler.getTPS();
    }
}
