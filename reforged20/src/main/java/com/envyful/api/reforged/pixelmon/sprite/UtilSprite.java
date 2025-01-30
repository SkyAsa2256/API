package com.envyful.api.reforged.pixelmon.sprite;

import com.envyful.api.forge.items.UtilItemStack;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.reforged.pixelmon.config.SpriteConfig;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.pixelmonmod.api.Flags;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBase;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.pokemon.species.gender.Gender;
import com.pixelmonmod.pixelmon.api.pokemon.species.palette.PaletteProperties;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.pokemon.stats.IVStore;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.LakeTrioStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.MewStats;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.storage.NbtKeys;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        var itemStack = getPixelmonSprite(pokemon);
        var placeholders = getPokemonPlaceholders(pokemon, config, transformers);

        UtilItemStack.setLore(itemStack, getPokemonDesc(pokemon, config, placeholders));
        UtilItemStack.setName(itemStack, PlatformProxy.flatParse(pokemon.isEgg() ? config.getEggName() : config.getName(), placeholders.toArray(new Placeholder[0])));

        return itemStack;
    }

    public static ItemStack getPixelmonSprite(Species pokemon) {
        ItemStack itemStack = new ItemStack(PixelmonItems.pixelmon_sprite);
        var tagCompound = new CompoundTag();
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

    public static List<Component> getPokemonDesc(Pokemon pokemon, SpriteConfig config, List<Placeholder> placeholders) {
        return PlaceholderFactory.handlePlaceholders(pokemon.isEgg() ? config.getEggLore() : config.getLore(), PlatformProxy::parse, placeholders);
    }

    public static List<Placeholder> getPokemonPlaceholders(Pokemon pokemon, SpriteConfig config, Placeholder... otherPlaceholders) {
        var iVs = pokemon.getIVs();
        var ivHP = iVs.getStat(BattleStatsType.HP);
        var ivAtk = iVs.getStat(BattleStatsType.ATTACK);
        var ivDef = iVs.getStat(BattleStatsType.DEFENSE);
        var ivSpeed = iVs.getStat(BattleStatsType.SPEED);
        var ivSAtk = iVs.getStat(BattleStatsType.SPECIAL_ATTACK);
        var ivSDef = iVs.getStat(BattleStatsType.SPECIAL_DEFENSE);
        var percentage = Math.round(((ivHP + ivDef + ivAtk + ivSpeed + ivSAtk + ivSDef) / 186f) * 100);
        var evHP = pokemon.getEVs().getStat(BattleStatsType.HP);
        var evAtk = pokemon.getEVs().getStat(BattleStatsType.ATTACK);
        var evDef = pokemon.getEVs().getStat(BattleStatsType.DEFENSE);
        var evSpeed = pokemon.getEVs().getStat(BattleStatsType.SPEED);
        var evSAtk = pokemon.getEVs().getStat(BattleStatsType.SPECIAL_ATTACK);
        var evSDef = pokemon.getEVs().getStat(BattleStatsType.SPECIAL_DEFENSE);
        var extraStats = pokemon.getExtraStats();

        List<Placeholder> placeholders = new ArrayList<>(Arrays.asList(otherPlaceholders));

        if (pokemon.isEgg()) {
            placeholders.add(Placeholder.simple("%egg_cycles%", pokemon.getEggCycles()));
            placeholders.add(Placeholder.simple("%egg_steps%", pokemon.getEggSteps()));
            placeholders.add(Placeholder.simple("%egg_description%", pokemon.getEggDescription()));
            return placeholders;
        }

        placeholders.add(Placeholder.simple("%species_name%", pokemon.getSpecies().getLocalizedName()));
        placeholders.add(Placeholder.simple("%nickname%", pokemon.getDisplayName()));
        placeholders.add(Placeholder.simple("%held_item%", pokemon.getHeldItem().getHoverName().getString()));
        placeholders.add(Placeholder.simple("%type%", getType(pokemon)));
        placeholders.add(Placeholder.simple("%palette%", pokemon.getPalette().getLocalizedName()));
        placeholders.add(Placeholder.simple("%level%", pokemon.getPokemonLevel()));
        placeholders.add(Placeholder.simple("%gender%", pokemon.getGender() == Gender.MALE ? config.getMaleFormat() : pokemon.getGender() == Gender.NONE ? config.getNoneFormat() : config.getFemaleFormat()));
        placeholders.add(Placeholder.simple("%breedable%", !pokemon.hasFlag(Flags.UNBREEDABLE) ? config.getBreedableTrueFormat() : config.getBreedableFalseFormat()));
        placeholders.add(Placeholder.simple("%nature%", config.getNatureFormat().replace("%nature_name%",
                        pokemon.getMintNature() != null ?
                                pokemon.getBaseNature().getLocalizedName() :
                                pokemon.getNature().getLocalizedName())
                .replace("%mint_nature%", pokemon.getMintNature() != null ?
                        config.getMintNatureFormat().replace("%mint_nature_name%", pokemon.getMintNature().getLocalizedName()) : "")));
        placeholders.add(Placeholder.simple("%ability_name%", pokemon.getAbility().getLocalizedName()));
        placeholders.add(Placeholder.simple("%ability_ha%", pokemon.hasHiddenAbility() ? config.getHaFormat() : config.getNotHaFormat()));
        placeholders.add(Placeholder.simple("%friendship%", pokemon.getFriendship()));
        placeholders.add(Placeholder.simple("%untradeable%", pokemon.isUntradeable() ? config.getUntrdeableTrueFormat() : config.getUntradeableFalseFormat()));
        placeholders.add(Placeholder.simple("%iv_percentage%", percentage));
        placeholders.add(Placeholder.simple("%iv_hp%", getColour(config, iVs, BattleStatsType.HP) + ivHP));
        placeholders.add(Placeholder.simple("%iv_attack%", getColour(config, iVs, BattleStatsType.ATTACK) + ivAtk));
        placeholders.add(Placeholder.simple("%iv_defence%", getColour(config, iVs, BattleStatsType.DEFENSE) + ivDef));
        placeholders.add(Placeholder.simple("%iv_spattack%", getColour(config, iVs, BattleStatsType.SPECIAL_ATTACK) + ivSAtk));
        placeholders.add(Placeholder.simple("%iv_spdefence%", getColour(config, iVs, BattleStatsType.SPECIAL_DEFENSE) + ivSDef));
        placeholders.add(Placeholder.simple("%iv_speed%", getColour(config, iVs, BattleStatsType.SPEED) + ivSpeed));
        placeholders.add(Placeholder.simple("%ev_hp%", evHP));
        placeholders.add(Placeholder.simple("%ev_attack%", evAtk));
        placeholders.add(Placeholder.simple("%ev_defence%", evDef));
        placeholders.add(Placeholder.simple("%ev_spattack%", evSAtk));
        placeholders.add(Placeholder.simple("%ev_spdefence%", evSDef));
        placeholders.add(Placeholder.simple("%ev_speed%", evSpeed));
        placeholders.add(getMovePlaceholder(pokemon, 0));
        placeholders.add(getMovePlaceholder(pokemon, 1));
        placeholders.add(getMovePlaceholder(pokemon, 2));
        placeholders.add(getMovePlaceholder(pokemon, 3));
        placeholders.add(Placeholder.simple("%shiny%", pokemon.isShiny() ? config.getShinyTrueFormat() : config.getShinyFalseFormat()));
        placeholders.add(Placeholder.simple("%form%", pokemon.getForm().getLocalizedName()));
        placeholders.add(Placeholder.simple("%size%", pokemon.getGrowth().getLocalizedName()));
        placeholders.add(Placeholder.simple("%friendship%", pokemon.getFriendship() + ""));
        placeholders.add(Placeholder.simple("%gmaxfactor%", pokemon.hasGigantamaxFactor() ? config.getGmaxFactorTrueFormat() : config.getGmaxFactorFalseFormat()));
        placeholders.add(
                Placeholder.require(() -> pokemon.getOriginalTrainer() != null)
                        .placeholder(Placeholder.simple("%original_trainer%", pokemon.getOriginalTrainer()))
                        .elsePlaceholder(Placeholder.empty("%original_trainer%"))
                        .build()
        );

        placeholders.add(
                Placeholder.require(() -> extraStats instanceof MewStats)
                        .placeholder(Placeholder.simple(s -> s
                                .replace("%mew_cloned%", config.getMewClonedFormat())
                                .replace("%cloned%", ((MewStats) extraStats).numCloned + ""))
                        )
                        .elsePlaceholder(Placeholder.composition(Placeholder.empty("%mew_cloned%"), Placeholder.empty("%cloned%")))
                        .build()
        );

        placeholders.add(
                Placeholder.require(() -> extraStats instanceof LakeTrioStats)
                        .placeholder(Placeholder.simple(s -> s
                                .replace("%trio_gemmed%", config.getGemmedFormat())
                                .replace("%gemmed%", ((LakeTrioStats) extraStats).numEnchanted + ""))
                        )
                        .elsePlaceholder(Placeholder.composition(Placeholder.empty("%trio_gemmed%"), Placeholder.empty("%gemmed%")))
                        .build()
        );

        return placeholders;
    }

    private static String getType(Pokemon pokemon) {
        var types = pokemon.getForm().getTypes();
        var typeInfo = new StringBuilder();

        for (var type : types) {
            typeInfo.append(type.getLocalizedName()).append(" ");
        }

        return typeInfo.toString();
    }

    private static String getColour(SpriteConfig config, IVStore ivStore, BattleStatsType statsType) {
        if (ivStore.isHyperTrained(statsType)) {
            return config.getHyperIvColour();
        }

        return config.getNormalIvColour();
    }

    private static String getMove(Pokemon pokemon, int pos) {
        if (pokemon.getMoveset() == null) {
            return "";
        }

        if (pokemon.getMoveset().attacks.length <= pos) {
            return "";
        }

        if (pokemon.getMoveset().attacks[pos] == null) {
            return "";
        }

        return pokemon.getMoveset().attacks[pos].getActualMove().getLocalizedName();
    }

    private static Placeholder getMovePlaceholder(Pokemon pokemon, int pos) {
        return Placeholder.require(() -> {
                    if (pokemon.getMoveset() == null) {
                        return false;
                    }

                    if (pokemon.getMoveset().attacks.length <= pos) {
                        return false;
                    }

                    return pokemon.getMoveset().attacks[pos] != null;
                }).placeholder(Placeholder.simple("%move_" + (pos + 1) + "%", getMove(pokemon, pos)))
                .elsePlaceholder(Placeholder.empty("%move_" + (pos + 1) + "%"))
                .build();
    }

    public static Pokemon getPokemon(ItemStack stack) {
        var tag = stack.getOrCreateTag();

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
