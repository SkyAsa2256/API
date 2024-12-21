package com.envyful.api.player;

import com.envyful.api.player.attribute.ManagedAttribute;
import org.junit.jupiter.api.Test;

public class PlayerAttributeTests {

    @Test
    void testAttributesSet() {
        var player = new TestDummyEnvyPlayer();
        player.setAttribute();


    }

    class TestAttributeOne extends ManagedAttribute<DummyPlayerManager> {

        private int number = 0;

        protected TestAttributeOne(DummyPlayerManager manager, int number) {
            super(DummyPlayerManager.NIL_UUID, manager);
        }
    }

}
