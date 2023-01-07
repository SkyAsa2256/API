package com.envyful.api.reforged.pixelmon.transformer;

import com.envyful.api.text.parse.SimplePlaceholder;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;

public class PokemonNameTransformer implements SimplePlaceholder {

    private final String name;

    public static PokemonNameTransformer of(Pokemon pokemon) {
        return of(pokemon.getSpecies());
    }

    public static PokemonNameTransformer of(EnumSpecies species) {
        return new PokemonNameTransformer(species.getLocalizedName());
    }

    private PokemonNameTransformer(String name) {this.name = name;}

    @Override
    public String replace(String name) {
        return name.replace("%pokemon%", this.name + "");
    }
}
