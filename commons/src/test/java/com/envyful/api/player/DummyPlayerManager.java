package com.envyful.api.player;

import com.envyful.api.player.manager.AbstractPlayerManager;

import java.util.UUID;

public class DummyPlayerManager extends AbstractPlayerManager<TestDummyEnvyPlayer, Void> {

    public static final UUID NIL_UUID = new UUID(-1, -1);

    public DummyPlayerManager() {
        super(___ -> NIL_UUID);
    }
}
