package com.envyful.api.reforged.pixelmon.transformer;

import com.envyful.api.text.parse.SimplePlaceholder;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

public class PokemonFormTransformer implements SimplePlaceholder {

    private final String name;

    public static PokemonFormTransformer of(Pokemon pokemon) {
        return new PokemonFormTransformer(pokemon.getForm().getLocalizedName());
    }

    private PokemonFormTransformer(String name) {this.name = name;}

    @Override
    public String replace(String name) {
        return name.replace("%form%", this.name + "");
    }
}
