package com.envyful.api.reforged.pixelmon.transformer;

import com.envyful.api.text.parse.SimplePlaceholder;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

public class PokemonSpriteTransformer implements SimplePlaceholder {

    private final String spritePath;

    public static PokemonSpriteTransformer of(Pokemon pokemon) {
        return new PokemonSpriteTransformer(pokemon.getPalette().getSprite().toString());
    }

    private PokemonSpriteTransformer(String spritePath) {this.spritePath = spritePath;}

    @Override
    public String replace(String name) {
        return name.replace("%sprite%", this.spritePath);
    }
}
