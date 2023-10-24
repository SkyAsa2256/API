package com.envyful.api.platform;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.text.Placeholder;

import java.util.Collection;

public class PlatformProxy {

    private static PlatformHandler handler;

    public static void setHandler(PlatformHandler handler) {
        PlatformProxy.handler = handler;
    }

    public static void broadcastMessage(Collection<String> message, Placeholder... placeholders) {
        if (handler == null) {
            UtilLogger.logger().ifPresent(logger -> logger.error("No platform handler set but broadcastMessage was called"));
            return;
        }

        handler.broadcastMessage(message, placeholders);
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
