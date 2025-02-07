package com.envyful.api.reforged.pixelmon.config;

import com.envyful.api.forge.items.UtilItemStack;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.reforged.pixelmon.sprite.SpriteBuilder;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.google.common.collect.Lists;
import com.pixelmonmod.api.Flags;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.pokemon.species.gender.Gender;
import com.pixelmonmod.pixelmon.api.pokemon.species.palette.PaletteProperties;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.pokemon.stats.IVStore;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.LakeTrioStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.MewStats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 *
 * Class for handling the configuration of the sprite item
 *
 */
@ConfigSerializable
public class SpriteConfig {

    public static final SpriteConfig DEFAULT = new SpriteConfig();

    private String name = "&b%species_name% %nickname%";
    private String eggName = "Egg";

    private List<String> lore = Lists.newArrayList(
            "&7Level: &b%level%",
            "&7Shiny: &b%shiny%",
            "&7Palette: &b%palette%",
            "&7Gender: %gender%",
            "&7Breedable: %breedable%",
            "&7Friendship: %friendship%",
            "&7Nature: &b%nature%",
            "&7Form: &b%form%",
            "&7Growth: &b%size%",
            "&7Ability: &b%ability_name%%ability_ha%",
            "&7Friendship: &b%friendship%",
            "&7Untradeable: &b%untradeable%",
            " ",
            "&7IVs (&b%iv_percentage%%&7):",
            "    §7HP: %iv_hp% §d| §7Atk: %iv_attack% §d| §7Def: %iv_defence%",
            "    §7SAtk: %iv_spattack% §d| §7SDef: %iv_spdefence% §d| §7Spd: %iv_speed%",
            " ",
            "&7EVs:",
            "    §7HP: &b%ev_hp% §d| §7Atk: &b%ev_attack% §d| §7Def: &b%ev_defence%",
            "    §7SAtk: &b%ev_spattack% §d| §7SDef: &b%ev_spdefence% §d| §7Spd: &b%ev_speed%",
            " ",
            "&7Moves:",
            "    &b%move_1%",
            "    &b%move_2%",
            "    &b%move_3%",
            "    &b%move_4%",
            " ",
            "%mew_cloned%",
            "%trio_gemmed%"
    );

    private List<String> eggLore = Lists.newArrayList(
            "&aEgg Cycles: %egg_cycles%",
            "&aEgg Steps: %egg_steps%",
            "&aEgg Description: %egg_description%"
    );

    private String untradeableTrueFormat = "&aTRUE";
    private String untradeableFalseFormat = "&cFALSE";
    private String haFormat = " &7(&c&lHA&7)";
    private String notHaFormat = "";
    private String maleFormat = "&bMale";
    private String femaleFormat = "&dFemale";
    private String noneFormat = "&fNONE";
    private String shinyTrueFormat = "&aTRUE";
    private String shinyFalseFormat = "&cFALSE";
    private String breedableTrueFormat = "&aTRUE";
    private String breedableFalseFormat = "&cFALSE";
    private String mewClonedFormat = "&7Times Cloned: %cloned%";
    private String gemmedFormat = "&7Gemmed: %gemmed%";
    private String natureFormat = "%nature_name% %mint_nature%";
    private String mintNatureFormat = "&7(%mint_nature_name%&7)";
    private String normalIvColour = "&b";
    private String hyperIvColour = "&e";
    private String gmaxFactorTrueFormat = "&aTRUE";
    private String gmaxFactorFalseFormat = "&cFALSE";
    private String emptyMoveSlot = "&7Empty";
    private boolean removeEmptyMoveSlots = true;

    public SpriteConfig() {}

    public ItemStack fromPokemon(Species species, Stats form, Gender gender, PaletteProperties palette) {
        var itemStack = new SpriteBuilder().species(species).form(form).gender(gender).palette(palette.getName()).build();
        var placeholders = this.getPokemonPlaceholders(species, form, gender, palette);
        List<ITextComponent> lore = PlaceholderFactory.handlePlaceholders(this.lore, (Function<String, ITextComponent>) PlatformProxy::parse, placeholders);

        UtilItemStack.setLore(itemStack, lore);
        UtilItemStack.setName(itemStack, PlatformProxy.flatParse(this.name, placeholders));

        return itemStack;
    }

    public ItemStack fromPokemon(Pokemon pokemon, Placeholder... additionalPlaceholders) {
        var itemStack = UtilSprite.getPixelmonSprite(pokemon);
        var placeholders = this.getPokemonPlaceholders(pokemon, additionalPlaceholders);

        UtilItemStack.setLore(itemStack, this.getLore(pokemon, placeholders));
        UtilItemStack.setName(itemStack, PlatformProxy.flatParse(pokemon.isEgg() ? this.eggName : this.name, placeholders));

        return itemStack;
    }

    public List<ITextComponent> getLore(Pokemon pokemon) {
        var placeholders = this.getPokemonPlaceholders(pokemon);
        return this.getLore(pokemon, placeholders);
    }

    protected List<ITextComponent> getLore(Pokemon pokemon, Placeholder... placeholders) {
        if (pokemon.isEgg()) {
            return PlaceholderFactory.handlePlaceholders(this.eggLore, PlatformProxy::parse, placeholders);
        }

        return PlaceholderFactory.handlePlaceholders(this.lore, PlatformProxy::parse, placeholders);
    }

    public Placeholder getPokemonPlaceholders(Species species, Stats form, Gender gender, PaletteProperties palette, Placeholder... additionalPlaceholders) {
        List<Placeholder> placeholders = new ArrayList<>(Arrays.asList(additionalPlaceholders));
        placeholders.add(Placeholder.simple("%species_name%", species.getLocalizedName()));
        placeholders.add(Placeholder.simple("%form%", form.getLocalizedName()));
        placeholders.add(Placeholder.simple("%shiny%", palette.getName().equals("shiny") ? this.shinyTrueFormat : this.shinyFalseFormat));
        placeholders.add(Placeholder.simple("%palette%", palette.getLocalizedName()));
        placeholders.add(this.getGenderPlaceholder(gender));
        return Placeholder.composition(placeholders);
    }

    public Placeholder getPokemonPlaceholders(Pokemon pokemon, Placeholder... otherPlaceholders) {
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
            return Placeholder.composition(placeholders);
        }

        placeholders.add(Placeholder.simple("%species_name%", pokemon.getSpecies().getLocalizedName()));
        placeholders.add(Placeholder.simple("%nickname%", pokemon.getDisplayName()));
        placeholders.add(Placeholder.simple("%held_item%", pokemon.getHeldItem().getHoverName().getString()));
        placeholders.add(Placeholder.simple("%type%", getType(pokemon)));
        placeholders.add(Placeholder.simple("%palette%", pokemon.getPalette().getLocalizedName()));
        placeholders.add(Placeholder.simple("%level%", pokemon.getPokemonLevel()));
        placeholders.add(this.getGenderPlaceholder(pokemon));
        placeholders.add(Placeholder.simple("%breedable%", !pokemon.hasFlag(Flags.UNBREEDABLE) ? this.breedableTrueFormat : this.breedableFalseFormat));
        placeholders.add(Placeholder.simple("%nature%", this.natureFormat.replace("%nature_name%",
                        pokemon.getMintNature() != null ?
                                pokemon.getBaseNature().getLocalizedName() :
                                pokemon.getNature().getLocalizedName())
                .replace("%mint_nature%", pokemon.getMintNature() != null ?
                        this.mintNatureFormat.replace("%mint_nature_name%", pokemon.getMintNature().getLocalizedName()) : "")));
        placeholders.add(Placeholder.simple("%ability_name%", pokemon.getAbility().getLocalizedName()));
        placeholders.add(Placeholder.simple("%ability_ha%", pokemon.hasHiddenAbility() ? this.haFormat : this.notHaFormat));
        placeholders.add(Placeholder.simple("%friendship%", pokemon.getFriendship()));
        placeholders.add(Placeholder.simple("%untradeable%", pokemon.isUntradeable() ? this.untradeableTrueFormat : this.untradeableFalseFormat));
        placeholders.add(Placeholder.simple("%iv_percentage%", percentage));
        placeholders.add(Placeholder.simple("%iv_hp%", getColour(iVs, BattleStatsType.HP) + ivHP));
        placeholders.add(Placeholder.simple("%iv_attack%", getColour(iVs, BattleStatsType.ATTACK) + ivAtk));
        placeholders.add(Placeholder.simple("%iv_defence%", getColour(iVs, BattleStatsType.DEFENSE) + ivDef));
        placeholders.add(Placeholder.simple("%iv_spattack%", getColour(iVs, BattleStatsType.SPECIAL_ATTACK) + ivSAtk));
        placeholders.add(Placeholder.simple("%iv_spdefence%", getColour(iVs, BattleStatsType.SPECIAL_DEFENSE) + ivSDef));
        placeholders.add(Placeholder.simple("%iv_speed%", getColour(iVs, BattleStatsType.SPEED) + ivSpeed));
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
        placeholders.add(Placeholder.simple("%shiny%", pokemon.isShiny() ? this.shinyTrueFormat : this.shinyFalseFormat));
        placeholders.add(Placeholder.simple("%form%", pokemon.getForm().getLocalizedName()));
        placeholders.add(Placeholder.simple("%size%", pokemon.getGrowth().getLocalizedName()));
        placeholders.add(Placeholder.simple("%friendship%", pokemon.getFriendship() + ""));
        placeholders.add(Placeholder.simple("%gmaxfactor%", pokemon.hasGigantamaxFactor() ? this.gmaxFactorTrueFormat : this.gmaxFactorFalseFormat));
        placeholders.add(
                Placeholder.require(() -> pokemon.getOriginalTrainer() != null)
                        .placeholder(Placeholder.simple("%original_trainer%", pokemon.getOriginalTrainer()))
                        .elsePlaceholder(Placeholder.empty("%original_trainer%"))
                        .build()
        );

        placeholders.add(
                Placeholder.require(() -> extraStats instanceof MewStats)
                        .placeholder(Placeholder.simple(s -> s
                                .replace("%mew_cloned%", this.mewClonedFormat)
                                .replace("%cloned%", ((MewStats) extraStats).numCloned + ""))
                        )
                        .elsePlaceholder(Placeholder.composition(Placeholder.empty("%mew_cloned%"), Placeholder.empty("%cloned%")))
                        .build()
        );

        placeholders.add(
                Placeholder.require(() -> extraStats instanceof LakeTrioStats)
                        .placeholder(Placeholder.simple(s -> s
                                .replace("%trio_gemmed%", this.gemmedFormat)
                                .replace("%gemmed%", ((LakeTrioStats) extraStats).numEnchanted + ""))
                        )
                        .elsePlaceholder(Placeholder.composition(Placeholder.empty("%trio_gemmed%"), Placeholder.empty("%gemmed%")))
                        .build()
        );

        return Placeholder.composition(placeholders);
    }

    public Placeholder getGenderPlaceholder(Pokemon pokemon) {
        if (pokemon == null) {
            return Placeholder.empty("%gender%");
        }

        return getGenderPlaceholder(pokemon.getGender());
    }

    public Placeholder getGenderPlaceholder(Gender gender) {
        if (gender == null) {
            return Placeholder.empty("%gender%");
        }

        if (gender == Gender.MALE) {
            return Placeholder.simple("%gender%", this.maleFormat);
        }

        if (gender == Gender.FEMALE) {
            return Placeholder.simple("%gender%", this.femaleFormat);
        }

        return Placeholder.simple("%gender%", this.noneFormat);
    }

    private String getType(Pokemon pokemon) {
        var types = pokemon.getForm().getTypes();
        var typeInfo = new StringBuilder();

        for (var type : types) {
            typeInfo.append(type.getLocalizedName()).append(" ");
        }

        return typeInfo.toString();
    }

    private String getColour(IVStore ivStore, BattleStatsType statsType) {
        if (ivStore.isHyperTrained(statsType)) {
            return this.hyperIvColour;
        }

        return this.normalIvColour;
    }

    private String getMove(Pokemon pokemon, int pos) {
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

    private Placeholder getMovePlaceholder(Pokemon pokemon, int pos) {
        return Placeholder.require(() -> {
                    if (pokemon.getMoveset() == null) {
                        return false;
                    }

                    if (pokemon.getMoveset().attacks.length <= pos) {
                        return false;
                    }

                    return pokemon.getMoveset().attacks[pos] != null;
                }).placeholder(Placeholder.simple("%move_" + (pos + 1) + "%", getMove(pokemon, pos)))
                .elsePlaceholder(this.removeEmptyMoveSlots ? Placeholder.empty("%move_" + (pos + 1) + "%") : Placeholder.simple("%move_" + (pos + 1) + "%", this.emptyMoveSlot))
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private SpriteConfig config = new SpriteConfig();

        public Builder name(String name) {
            this.config.name = name;
            return this;
        }

        public Builder lore(List<String> lore) {
            this.config.lore = lore;
            return this;
        }

        public Builder lore(String... lore) {
            this.config.lore = Lists.newArrayList(lore);
            return this;
        }

        public Builder addLore(String... lore) {
            this.config.lore.addAll(List.of(lore));
            return this;
        }

        public Builder untrdeableTrueFormat(String untrdeableTrueFormat) {
            this.config.untradeableTrueFormat = untrdeableTrueFormat;
            return this;
        }

        public Builder untradeableFalseFormat(String untradeableFalseFormat) {
            this.config.untradeableFalseFormat = untradeableFalseFormat;
            return this;
        }

        public Builder haFormat(String haFormat) {
            this.config.haFormat = haFormat;
            return this;
        }

        public Builder maleFormat(String maleFormat) {
            this.config.maleFormat = maleFormat;
            return this;
        }

        public Builder femaleFormat(String femaleFormat) {
            this.config.femaleFormat = femaleFormat;
            return this;
        }

        public Builder noneFormat(String noneFormat) {
            this.config.noneFormat = noneFormat;
            return this;
        }

        public Builder shinyTrueFormat(String shinyTrueFormat) {
            this.config.shinyTrueFormat = shinyTrueFormat;
            return this;
        }

        public Builder shinyFalseFormat(String shinyFalseFormat) {
            this.config.shinyFalseFormat = shinyFalseFormat;
            return this;
        }

        public Builder unbreedableTrueFormat(String unbreedableTrueFormat) {
            this.config.breedableTrueFormat = unbreedableTrueFormat;
            return this;
        }

        public Builder unbreedableFalseFormat(String unbreedableFalseFormat) {
            this.config.breedableFalseFormat = unbreedableFalseFormat;
            return this;
        }

        public Builder mewClonedFormat(String mewClonedFormat) {
            this.config.mewClonedFormat = mewClonedFormat;
            return this;
        }

        public Builder gemmedFormat(String gemmedFormat) {
            this.config.gemmedFormat = gemmedFormat;
            return this;
        }

        public Builder natureFormat(String natureFormat) {
            this.config.natureFormat = natureFormat;
            return this;
        }

        public Builder mintNatureFormat(String mintNatureFormat) {
            this.config.mintNatureFormat = mintNatureFormat;
            return this;
        }

        public Builder normalIvColour(String normalIvColour) {
            this.config.normalIvColour = normalIvColour;
            return this;
        }

        public Builder hyperIvColour(String hyperIvColour) {
            this.config.hyperIvColour = hyperIvColour;
            return this;
        }

        public Builder gmaxFactorTrueFormat(String gmaxFactorTrueFormat) {
            this.config.gmaxFactorTrueFormat = gmaxFactorTrueFormat;
            return this;
        }

        public Builder gmaxFactorFalseFormat(String gmaxFactorFalseFormat) {
            this.config.gmaxFactorFalseFormat = gmaxFactorFalseFormat;
            return this;
        }

        public SpriteConfig build() {
            return this.config;
        }
    }
}
