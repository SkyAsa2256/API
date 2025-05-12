package com.envyful.api.reforged.pixelmon.config;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class UtilPokemonPrice {

    public static double getMinPricePermissible(ServerPlayer player, Pokemon pokemon, double defaultPrice,
                                                List<PokeSpecPricing> modifiers) {
        double currentPrice = defaultPrice;

        modifiers.sort(PokeSpecPricing::compareTo);

        for (PokeSpecPricing minPriceModifier : modifiers) {
            if (!minPriceModifier.hasPermission(player)) {
                continue;
            }

            if (minPriceModifier.getSpec().matches(pokemon)) {
                currentPrice = minPriceModifier.apply(currentPrice);
            }
        }

        return currentPrice;
    }

    public static double getMinPrice(Pokemon pokemon, double defaultPrice, List<PokeSpecPricing> modifiers) {
        double currentPrice = defaultPrice;

        modifiers.sort(PokeSpecPricing::compareTo);

        for (PokeSpecPricing minPriceModifier : modifiers) {
            if (minPriceModifier.getSpec().matches(pokemon)) {
                currentPrice = minPriceModifier.apply(currentPrice);
            }
        }

        return currentPrice;
    }
}
