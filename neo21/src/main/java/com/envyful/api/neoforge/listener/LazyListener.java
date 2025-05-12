package com.envyful.api.neoforge.listener;

import net.neoforged.neoforge.common.NeoForge;

/**
 *
 * Simple abstract class for registering the listener as soon as it's instantiated
 *
 */
public abstract class LazyListener {

    protected LazyListener() {
        NeoForge.EVENT_BUS.register(this);
    }
}
