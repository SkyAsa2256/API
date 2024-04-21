package com.envyful.api.reforged.config;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class PokemonSpecSerializer implements TypeSerializer<PokemonSpecification> {

    private static final PokemonSpecSerializer INSTANCE = new PokemonSpecSerializer();

    private PokemonSpecSerializer() {}

    public static PokemonSpecSerializer getInstance() {
        return INSTANCE;
    }

    @Override
    public PokemonSpecification deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return PokemonSpecificationProxy.create(node.getString());
    }

    @Override
    public void serialize(Type type, @Nullable PokemonSpecification obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        node.set(obj.toString());
    }
}
