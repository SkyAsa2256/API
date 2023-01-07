package com.envyful.api.reforged.pixelmon.transformer;

import com.envyful.api.text.parse.SimplePlaceholder;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;

public class PokemonDexTransformer implements SimplePlaceholder {

    private final int ndex;

    public static PokemonDexTransformer of(Pokemon pokemon) {
        return of(pokemon.getSpecies());
    }

    public static PokemonDexTransformer of(EnumSpecies species) {
        return new PokemonDexTransformer(species.getNationalPokedexInteger());
    }

    private PokemonDexTransformer(int ndex) {this.ndex = ndex;}

    @Override
    public String replace(String name) {
        return name.replace("%pokedex%", this.ndex + "");
    }
}
