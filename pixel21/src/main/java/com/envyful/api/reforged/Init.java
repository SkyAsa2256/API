package com.envyful.api.reforged;

import com.envyful.api.config.ConfigTypeSerializer;
import com.envyful.api.forge.InitializationTask;
import com.envyful.api.reforged.config.PokemonSpecSerializer;
import com.pixelmonmod.api.pokemon.PokemonSpecification;

/**
 *
 * Task that runs on server startup to register the PokemonSpecSerializer before the configs are loaded
 * by the plugin or mod
 *
 */
public class Init implements InitializationTask {

    @Override
    public void run() {
        ConfigTypeSerializer.register(PokemonSpecSerializer.getInstance(), PokemonSpecification.class);
    }
}
