package com.envyful.api.reforged.pixelmon;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.spawning.SpawnInfo;
import com.pixelmonmod.pixelmon.api.spawning.SpawnSet;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.entities.pokemon.SpawnInfoPokemon;
import com.pixelmonmod.pixelmon.api.spawning.util.SetLoader;
import com.pixelmonmod.pixelmon.api.world.WorldTime;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.BaseStats;
import net.minecraft.world.biome.Biome;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class UtilPokemonInfo {

    private static Field nameField;

    static {
        try {
            nameField = Biome.class.getField("field_76791_y");
            nameField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public List<String> getSpawnBiomes(BaseStats pokemon) {
        List<String> names = Lists.newArrayList();

        for (SpawnSet next : SetLoader.<SpawnSet>getAllSets()) {
            for (SpawnInfo spawnInfo : next.spawnInfos) {
                if (!(spawnInfo instanceof SpawnInfoPokemon)) {
                    continue;
                }

                SpawnInfoPokemon spawnInfoPokemon = (SpawnInfoPokemon) spawnInfo;

                if (!spawnInfoPokemon.getSpecies().equals(pokemon.getSpecies()) || spawnInfoPokemon.spawnSpecificBossRate != null) {
                    continue;
                }

                for (Biome biome : spawnInfoPokemon.condition.biomes) {
                    try {
                        names.add((String) nameField.get(biome));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return names;
    }

    public List<String> getSpawnTimes(BaseStats pokemon) {
        List<String> names = Lists.newArrayList();

        for (SpawnSet next : SetLoader.<SpawnSet>getAllSets()) {
            for (SpawnInfo spawnInfo : next.spawnInfos) {
                if (!(spawnInfo instanceof SpawnInfoPokemon)) {
                    continue;
                }

                SpawnInfoPokemon spawnInfoPokemon = (SpawnInfoPokemon) spawnInfo;

                if (!spawnInfoPokemon.getSpecies().equals(pokemon.getSpecies()) || spawnInfoPokemon.condition.times == null) {
                    continue;
                }

                for (WorldTime time : spawnInfoPokemon.condition.times) {
                    names.add(time.getLocalizedName());
                }
            }
        }

        return names;
    }


    public List<String> getCatchRate(BaseStats pokemon) {
        double males = pokemon.getMalePercent();
        if (males == (double) -1) {
            return Collections.singletonList("§7Base rate: " + String.format("%.2f", pokemon.getCatchRate() / 255.0D * 100.0D) + "%");
        } else {
            return Lists.newArrayList(
                    "§b♂ Male: " + String.format("%.2f", pokemon.getMalePercent()) + "%",
                    "§d♀ Female: " + String.format("%.2f", (100 - pokemon.getMalePercent())) + "%"
            );
        }
    }
}
