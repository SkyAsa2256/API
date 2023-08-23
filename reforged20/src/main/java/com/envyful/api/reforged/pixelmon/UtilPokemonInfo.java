package com.envyful.api.reforged.pixelmon;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.spawning.SpawnInfo;
import com.pixelmonmod.pixelmon.api.spawning.SpawnSet;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.entities.pokemon.SpawnInfoPokemon;
import com.pixelmonmod.pixelmon.api.world.WorldTime;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import net.minecraft.tags.TagEntry;

import java.util.Collections;
import java.util.List;

public class UtilPokemonInfo {

    public static List<String> getSpawnBiomes(Stats pokemon) {
        List<String> names = Lists.newArrayList();

        for (List<SpawnSet> spawnSet : PixelmonSpawning.getAll().values()) {
            for (SpawnSet set : spawnSet) {
                for (SpawnInfo spawnInfo : set.spawnInfos) {
                    if (!(spawnInfo instanceof SpawnInfoPokemon)) {
                        continue;
                    }

                    SpawnInfoPokemon spawnInfoPokemon = (SpawnInfoPokemon)spawnInfo;

                    if (!spawnInfoPokemon.getSpecies().equals(pokemon.getParentSpecies()) || spawnInfoPokemon.spawnSpecificBossRate != null) {
                        continue;
                    }

                    for (TagEntry biome : spawnInfoPokemon.condition.biomes) {
                        String name = biome.toString();

                        if (!names.contains(name)) {
                            names.add(name);
                        }
                    }
                }
            }
        }

        return names;
    }

    public static List<String> getSpawnTimes(Stats pokemon) {
        List<String> names = Lists.newArrayList();

        for (List<SpawnSet> spawnSet : PixelmonSpawning.getAll().values()) {
            for (SpawnSet set : spawnSet) {
                for (SpawnInfo spawnInfo : set.spawnInfos) {
                    if (!(spawnInfo instanceof SpawnInfoPokemon)) {
                        continue;
                    }

                    SpawnInfoPokemon spawnInfoPokemon = (SpawnInfoPokemon)spawnInfo;

                    if (!spawnInfoPokemon.getSpecies().equals(pokemon.getParentSpecies()) || spawnInfoPokemon.condition.times == null) {
                        continue;
                    }

                    for (WorldTime time : spawnInfoPokemon.condition.times) {
                        if (time == null || names.contains(time.getLocalizedName())) {
                            continue;
                        }

                        String name = time.getLocalizedName();

                        if (!names.contains(name)) {
                            names.add(name);
                        }
                    }
                }
            }
        }

        return names;
    }


    public static List<String> getCatchRate(Stats pokemon) {
        double males = pokemon.getMalePercentage();
        if (males == (double) -1) {
            return Collections.singletonList("§7Base rate: " + String.format("%.2f", pokemon.getCatchRate() / 255.0D * 100.0D) + "%");
        } else {
            return Lists.newArrayList(
                    "§b♂ Male: " + String.format("%.2f", pokemon.getMalePercentage()) + "%",
                    "§d♀ Female: " + String.format("%.2f", (100 - pokemon.getMalePercentage())) + "%"
            );
        }
    }
}
