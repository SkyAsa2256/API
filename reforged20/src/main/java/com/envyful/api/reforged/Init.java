package com.envyful.api.reforged;

import com.envyful.api.config.ConfigTypeSerializer;
import com.envyful.api.forge.InitializationTask;
import com.envyful.api.reforged.config.PokemonSpecSerializer;
import com.pixelmonmod.api.pokemon.PokemonSpecification;

public class Init implements InitializationTask {

    @Override
    public void run() {
        ConfigTypeSerializer.register(PokemonSpecSerializer.getInstance(), PokemonSpecification.class);
    }
}
