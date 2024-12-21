package com.envyful.api.player;

import com.envyful.api.config.ConfigLocation;
import com.envyful.api.text.Placeholder;

import java.util.UUID;

public class TestDummyEnvyPlayer extends AbstractEnvyPlayer<Void> {

    public TestDummyEnvyPlayer() {
        super();
    }

    @Override
    public void actionBar(String message, Placeholder... placeholders) {

    }

    @Override
    public void actionBar(Object message) {

    }

    @Override
    public void executeCommand(String command) {

    }

    @Override
    public void playSound(String sound, float volume, float pitch) {

    }

    @Override
    public void playSound(Object sound, float volume, float pitch) {

    }

    @Override
    public void executeCommands(String... commands) {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public void teleport(ConfigLocation location) {

    }

    @Override
    public void message(Object... messages) {

    }

    @Override
    public UUID getUniqueId() {
        return DummyPlayerManager.NIL_UUID;
    }

    @Override
    public String getName() {
        return "Dummy";
    }
}
