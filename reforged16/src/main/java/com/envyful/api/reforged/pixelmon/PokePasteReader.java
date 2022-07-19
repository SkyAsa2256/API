package com.envyful.api.reforged.pixelmon;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.battles.attack.AttackRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.Nature;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBuilder;
import com.pixelmonmod.pixelmon.api.pokemon.ability.AbilityRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.gender.Gender;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.registries.PixelmonForms;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PokePasteReader {

    private final BufferedReader reader;

    private List<PokemonBuilder> pokemon = null;

    private PokePasteReader(BufferedReader reader) {
        this.reader = reader;
    }

    public List<Pokemon> build() {
        if (this.pokemon != null) {
            return this.create();
        }

        this.pokemon = Lists.newArrayList();

        if(this.reader == null) {
            return this.create();
        }

        String line;
        PokemonBuilder builder = null;
        int moveCounter = 0;

        while((line = this.readLine(this.reader)) != null) {
            Species species = this.calculateSpecies(line);

            if (species != null || line.contains("@") || line.contains("(M)") || line.contains("(F)")) {
                if (builder != null) {
                    this.pokemon.add(builder);
                }

                builder = PokemonBuilder.builder();
                line = line.trim();

                if (line.contains("(F)")) {
                    line = line.replace("(F)", "").trim();
                    builder.gender(Gender.FEMALE);
                } else if (line.contains("(M)")) {
                    line = line.replace("(M)", "").trim();
                    builder.gender(Gender.MALE);
                }

                String[] split = line.split("@");
                String pokemon = split[0].trim().replace(":", "").replace(" ", "");

                if (species == null) {
                    species = PixelmonSpecies.fromNameOrDex(pokemon).orElse(PixelmonSpecies.MISSINGNO.getValueUnsafe());
                }

                if (pokemon.contains("-")) {
                    final String[] formSplit = pokemon.split("-");
                    pokemon = formSplit[0].trim();
                    species = PixelmonSpecies.fromNameOrDex(pokemon).orElse(PixelmonSpecies.MISSINGNO.getValueUnsafe());

                    if(species == null) {
                        species = PixelmonSpecies.fromNameOrDex(split[0].trim().replace(":", "").replace(" ", "").replace("Æ", "")).orElse(PixelmonSpecies.MISSINGNO.getValueUnsafe());
                    }

                    if(species != null) {
                        builder.species(species);
                    } else {
                        builder.species(PixelmonSpecies.FARFETCHD.getValueUnsafe().getDex());
                        species = PixelmonSpecies.FARFETCHD.getValueUnsafe();
                    }

                    builder.form(formSplit[1].replace("Alola", "Alolan").replace("Alolann", "Alolan"));
                }

                builder.species(species);

                if (split.length != 2) {
                    continue;
                }

                String item = split[1].trim().replace("-", "_").replace(" ", "_").toLowerCase();
                builder.heldItem(new ItemStack(ForgeRegistries.ITEMS.getValue(ResourceLocationHelper.of(item))));
            } else if (line.contains("Ability:")) {
                if (line.contains("Battle Bond")) {
                    builder.form(PixelmonForms.BATTLEBOND);
                }else {
                    builder.ability(AbilityRegistry.getAbility(line.replace("Ability: ", "").replace(" ", "").trim()).orElse(null));
                }
            } else if(line.contains("Level: ")) {
                builder.level(Integer.parseInt(line.split(": ")[1].trim()));
            } else if (line.contains("EVs:")) {
                String evsStr = line.replace("EVs: ", "");
                String[] evs = evsStr.split(" / ");
                int[] parsedEVs = {0, 0, 0, 0, 0, 0};

                for (String ev : evs) {
                    String[] pair = ev.split(" ");
                    String value = pair[0].trim();
                    String type = pair[1].trim();
                    BattleStatsType statType = UtilBattleStatType.convert(type).orElse(BattleStatsType.HP);
                    parsedEVs[statType.getStatIndex()] = Integer.parseInt(value);
                }

                builder.evs(parsedEVs);
            } else if (line.contains("IVs:")) {
                String ivsStr = line.replace("IVs: ", "");
                String[] ivs = ivsStr.split(" / ");
                int[] parsedIVs = {31, 31, 31, 31, 31, 31};

                for (String iv : ivs) {
                    String[] pair = iv.split(" ");
                    String value = pair[0].trim();
                    String type = pair[1].trim();
                    BattleStatsType statType = UtilBattleStatType.convert(type).orElse(BattleStatsType.HP);
                    parsedIVs[statType.getStatIndex()] = Integer.parseInt(value);
                }

                builder.ivs(parsedIVs);
            } else if (line.contains("Nature") && !line.contains("-")) {
                final String[] split = line.split(" ");
                builder.nature(Nature.valueOf(split[0].trim().toUpperCase()));
            } else if (line.startsWith("- ")) {
                String move = line.replace("- ", "").trim();
                if (move.contains("[")) {
                    move = move.substring(0, move.indexOf("[") - 1).trim();
                }
                move = move.replace(" ", "_");
                builder.move(moveCounter++, AttackRegistry.getAttackBase(move).orElse(null));
            } else {
                if (!line.contains("Shiny: Yes")) {
                    continue;
                }
                builder.shiny();
            }
        }

        if (builder != null) {
            this.pokemon.add(builder);
        }

        this.closeReader(this.reader);
        return this.create();
    }

    private List<Pokemon> create() {
        List<Pokemon> pokes = Lists.newArrayList();

        for (PokemonBuilder builder : this.pokemon) {
            pokes.add(builder.build());
        }

        return pokes;
    }

    private void closeReader(BufferedReader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readLine(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Species calculateSpecies(String line) {
        return PixelmonSpecies.fromNameOrDex(line.trim()).orElse(PixelmonSpecies.MISSINGNO.getValueUnsafe());
    }

    public static PokePasteReader from(String paste) {
        URL url = getPokePasteURL(paste);

        if(url == null) {
            return null;
        }

        InputStream inputStream = getConnectionStream(url);

        if(inputStream == null) {
            return null;
        }

        return new PokePasteReader(new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
    }

    private static URL getPokePasteURL(String paste) {
        try {
            return new URL(paste);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static InputStream getConnectionStream(URL url) {
        try {
            return url.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static PokePasteReader from(File file) {
        if(file == null) {
            return null;
        }

        InputStream inputStream = getFileStream(file);

        if(inputStream == null) {
            return null;
        }

        return new PokePasteReader(new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
    }

    private static InputStream getFileStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}