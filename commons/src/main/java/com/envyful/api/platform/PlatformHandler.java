package com.envyful.api.platform;

import com.envyful.api.text.Placeholder;

import java.util.Collection;

public interface PlatformHandler {

    void broadcastMessage(Collection<String> message, Placeholder... placeholders);

    void runSync(Runnable runnable);

    void runLater(Runnable runnable, int delayTicks);

    double getTPS();

}
