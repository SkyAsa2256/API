package com.envyful.api.platform;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.ConfigToast;
import com.envyful.api.config.database.DatabaseDetailsRegistry;
import com.envyful.api.platform.text.TextFormatter;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.AttributeHolder;
import com.envyful.api.player.attribute.AttributeTrigger;
import com.envyful.api.text.Placeholder;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * Static utility class for handling platform specific code
 * and abstracting it from the rest of the plugin
 * <br>
 * This prevents breaking changes from Mojang and other platforms from affecting the rest of the code
 *
 */
public class PlatformProxy {

    static {
        DatabaseDetailsRegistry.init();
    }

    private static PlatformHandler handler;
    private static PlayerManager<?, ?> playerManager;
    private static TextFormatter<?> textFormatter;

    /**
     *
     * Sets the platform handler
     *
     * @param handler The handler
     */
    public static void setHandler(PlatformHandler handler) {
        PlatformProxy.handler = handler;
    }

    /**
     *
     * Sets the player manager
     *
     * @param playerManager The player manager
     */
    public static void setPlayerManager(PlayerManager<?, ?> playerManager) {
        PlatformProxy.playerManager = playerManager;
    }

    public static void setTextFormatter(TextFormatter<?> textFormatter) {
        PlatformProxy.textFormatter = textFormatter;
    }

    /**
     *
     * Gets the player manager
     *
     * @return The player manager
     */
    public static PlayerManager<?, ?> getPlayerManager() {
        return playerManager;
    }

    /**
     *
     * Executes a console command
     *
     * @param commands The commands to execute
     */
    public static void executeConsoleCommands(String... commands) {
        executeConsoleCommands(List.of(commands));
    }

    /**
     *
     * Executes a console command
     *
     * @param commands The commands to execute
     */
    public static void executeConsoleCommands(List<String> commands, Placeholder... placeholders) {
        if (handler == null) {
            UtilLogger.logger().ifPresent(logger -> logger.error("No platform handler set but executeConsoleCommands was called"));
            return;
        }

        handler.executeConsoleCommands(commands, placeholders);
    }

    /**
     *
     * Checks if a player has a permission
     *
     * @param player The player
     * @param permission The permission
     * @return If the player has the permission
     */
    public static boolean hasPermission(Object player, String permission) {
        if (handler == null) {
            UtilLogger.logger().ifPresent(logger -> logger.error("No platform handler set but hasPermission was called"));
            return false;
        }

        return handler.hasPermission(player, permission);
    }

    /**
     *
     * Checks if a player has a permission
     *
     * @param player The player
     * @param permission The permission
     * @return If the player has the permission
     */
    public static boolean hasPermission(EnvyPlayer<?> player, String permission) {
        if (handler == null) {
            UtilLogger.logger().ifPresent(logger -> logger.error("No platform handler set but hasPermission was called"));
            return false;
        }

        return handler.hasPermission(player.getParent(), permission);
    }

    /**
     *
     * Checks if a player is an operator
     *
     * @param player The player
     * @return If the player is an operator
     */
    public static boolean isOP(EnvyPlayer<?> player) {
        if (handler == null) {
            UtilLogger.logger().ifPresent(logger -> logger.error("No platform handler set but isOP was called"));
            return false;
        }

        return handler.isOP(player.getParent());
    }

    /**
     *
     * Sends a toast to the player
     *
     * @param player The player to send the toast to
     * @param configToast The toast to send
     */
    public static void sendToast(Object player, ConfigToast configToast) {
        if (handler == null) {
            UtilLogger.logger().ifPresent(logger -> logger.error("No platform handler set but sendToast was called"));
            return;
        }

        handler.sendToast(player, configToast);
    }

    /**
     *
     * Sends a toast to the player
     *
     * @param player The player to send the toast to
     * @param configToast The toast to send
     */
    public static void sendToast(EnvyPlayer<?> player, ConfigToast configToast) {
        if (handler == null) {
            UtilLogger.logger().ifPresent(logger -> logger.error("No platform handler set but sendToast was called"));
            return;
        }

        handler.sendToast(player, configToast);
    }

    public static void broadcastMessage(Collection<String> message, Placeholder... placeholders) {
        if (handler == null) {
            UtilLogger.logger().ifPresent(logger -> logger.error("No platform handler set but broadcastMessage was called"));
            return;
        }

        handler.broadcastMessage(message, placeholders);
    }

    public static void sendMessage(Messageable<?> player, String... message) {
        sendMessage(player.getParent(), List.of(message));
    }

    public static void sendMessage(Object player, String... message) {
        sendMessage(player, List.of(message));
    }

    public static void sendMessage(Messageable<?> player, String message, Placeholder... placeholders) {
        sendMessage(player.getParent(), message, placeholders);
    }

    public static void sendMessage(Object player, String message, Placeholder... placeholders) {
        sendMessage(player, List.of(message), placeholders);
    }

    public static void sendMessage(Messageable<?> player, Collection<String> message, Placeholder... placeholders) {
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

    public static <T> T parse(String text) {
        return (T) textFormatter.parse(text);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> parse(String text, Placeholder... placeholders) {
        return (List<T>) textFormatter.parse(text, placeholders);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> parse(Collection<String> text, Placeholder... placeholders) {
        return (List<T>) textFormatter.parse(text, placeholders);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> parse(List<String> text, Placeholder... placeholders) {
        return (List<T>) textFormatter.parse(text, placeholders);
    }

    public static <T> T flatParse(String text, Placeholder... placeholders) {
        List<T> parsed = parse(text, placeholders);

        if (parsed.isEmpty() || parsed.size() > 1) {
            return null;
        }

        return parsed.get(0);
    }

    public static <T> T flatParse(String text, Consumer<String> errorHandler, Placeholder... placeholders) {
        List<T> parsed = parse(text, placeholders);

        if (parsed.isEmpty() || parsed.size() > 1) {
            errorHandler.accept(text);
            return null;
        }

        return parsed.get(0);
    }

    @SuppressWarnings("unchecked")
    public static <T> String unresolve(T object) {
        return unresolve((TextFormatter<T>) textFormatter, object);
    }

    private static <T> String unresolve(TextFormatter<T> textFormatter, T object) {
        return textFormatter.unresolve(object);
    }

    public static String strip(String text) {
        return textFormatter.strip(text);
    }

    public static <Y extends AttributeTrigger<X>, X extends AttributeHolder> void registerAttributeTrigger(Class<Y> triggerClass, Supplier<Y> constructor) {
        handler.registerAttributeTrigger(triggerClass, constructor);
    }

    public static <X extends AttributeHolder, Y> AttributeTrigger<X> getAttributeTriggerInstance(Class<Y> eventClass, Class<? extends AttributeTrigger<X>> triggerClass, Function<Y, List<X>> converter) {
        return handler.getAttributeTriggerInstance(eventClass, triggerClass, converter);
    }
}
