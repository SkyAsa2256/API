package com.envyful.api.reforged.pixelmon;

import com.envyful.api.text.parse.SimplePlaceholder;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

public class PokemonPlaceholder implements SimplePlaceholder {

    private final Pokemon pokemon;

    private PokemonPlaceholder(Pokemon pokemon) {
        this.pokemon = pokemon;
    }

    public static PokemonPlaceholder of(Pokemon pokemon) {
        return new PokemonPlaceholder(pokemon);
    }

    @Override
    public String replace(String text) {
        if (text == null) {
            return "";
        }

        if (pokemon == null) {
            return text.replace("%species%", "0")
                    .replace("%form%", "")
                    .replace("%gender%", "0")
                    .replace("%palette%", "none")
                    .replace("%egg%", "-1");
        }

        return text
                .replace("%pokemon%", pokemon.getLocalizedName())
                .replace("%species%", pokemon.getSpecies().getDex() + "")
                .replace("%form%", pokemon.getForm().getName())
                .replace("%gender%", pokemon.getGender().ordinal() + "")
                .replace("%palette%", pokemon.getPalette().getName())
                .replace("%egg%", pokemon.isEgg() ? (pokemon.getEggCycles()  + "") : "-1");
    }
}
