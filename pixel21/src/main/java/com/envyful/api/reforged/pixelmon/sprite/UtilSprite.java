package com.envyful.api.reforged.pixelmon.sprite;

import com.envyful.api.reforged.pixelmon.config.SpriteConfig;
import com.envyful.api.text.Placeholder;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.gender.Gender;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import com.pixelmonmod.pixelmon.init.registry.ItemRegistration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class UtilSprite {

    private UtilSprite() {
        throw new UnsupportedOperationException("Static utility class");
    }

    @Deprecated
    public static ItemStack getPokemonElement(Pokemon pokemon) {
        return getPokemonElement(pokemon, SpriteConfig.DEFAULT);
    }

    @Deprecated
    public static ItemStack getPokemonElement(Pokemon pokemon, SpriteConfig config, Placeholder... transformers) {
        return config.fromPokemon(pokemon, transformers);
    }

    public static ItemStack getPixelmonSprite(Species pokemon) {
        ItemStack itemStack = new ItemStack(ItemRegistration.PIXELMON_SPRITE);
        var tagCompound = new CompoundTag();
        tagCompound.putShort("ndex", (short)pokemon.getDex());
        tagCompound.putString("form", pokemon.getDefaultForm().getName());
        tagCompound.putByte("gender", (byte)Gender.MALE.ordinal());
        tagCompound.putString("palette", pokemon.getDefaultForm().getDefaultGenderProperties().getDefaultPalette().getName());
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tagCompound));
        return itemStack;
    }

    public static ItemStack getPixelmonSprite(Pokemon pokemon) {
        return SpriteItemHelper.getPhoto(pokemon);
    }
}
