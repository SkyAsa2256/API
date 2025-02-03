package com.envyful.api.reforged.pixelmon.sprite;

import com.envyful.api.reforged.pixelmon.config.SpriteConfig;
import com.envyful.api.text.Placeholder;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBase;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.pokemon.species.gender.Gender;
import com.pixelmonmod.pixelmon.api.pokemon.species.palette.PaletteProperties;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.storage.NbtKeys;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class UtilSprite {

    private UtilSprite() {
        throw new UnsupportedOperationException("Static utility class");
    }

    @Deprecated(forRemoval = true)
    public static ItemStack getPokemonElement(Pokemon pokemon) {
        return getPokemonElement(pokemon, SpriteConfig.DEFAULT);
    }

    @Deprecated(forRemoval = true)
    public static ItemStack getPokemonElement(Pokemon pokemon, SpriteConfig config, Placeholder... transformers) {
        return config.fromPokemon(pokemon, transformers);
    }

    public static ItemStack getPixelmonSprite(Species pokemon) {
        ItemStack itemStack = new ItemStack(PixelmonItems.pixelmon_sprite);
        CompoundNBT tagCompound = new CompoundNBT();
        itemStack.setTag(tagCompound);
        tagCompound.putShort("ndex", (short)pokemon.getDex());
        tagCompound.putString("form", pokemon.getDefaultForm().getName());
        tagCompound.putByte("gender", (byte)Gender.MALE.ordinal());
        tagCompound.putString("palette", pokemon.getDefaultForm().getDefaultGenderProperties().getDefaultPalette().getName());

        return itemStack;
    }

    public static ItemStack getPixelmonSprite(Pokemon pokemon) {
        return SpriteItemHelper.getPhoto(pokemon);
    }

    public static Pokemon getPokemon(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();

        if (!tag.contains(SpriteItemHelper.NDEX)) {
            return null;
        }

        boolean isEgg = tag.contains(NbtKeys.EGG_CYCLES);
        int eggCycles = isEgg ? tag.getInt(NbtKeys.EGG_CYCLES) : -1;
        Species species = PixelmonSpecies.fromNationalDex((int) tag.getShort(SpriteItemHelper.NDEX));

        if(species == null) {
            return null;
        }

        Stats form = species.getForm(tag.getString(SpriteItemHelper.FORM));
        Gender gender = Gender.values()[tag.getByte(SpriteItemHelper.GENDER)];

        if(form == null || gender == null) {
            return null;
        }

        PaletteProperties palette = form.getGenderProperties(gender).getPalette(tag.getString(SpriteItemHelper.PALETTE));

        if(palette == null) {
            return null;
        }

        PokemonBase base = new PokemonBase(species, form, palette, gender);

        if(isEgg) {
            base.setEggCycles(eggCycles);
        }

        return base.toPokemon();
    }
}
