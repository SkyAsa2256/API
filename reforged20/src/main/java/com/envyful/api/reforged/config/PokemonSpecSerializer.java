package com.envyful.api.reforged.config;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class PokemonSpecSerializer extends ScalarSerializer<PokemonSpecification> {

    public PokemonSpecSerializer() {
        super(PokemonSpecification.class);
    }

    @Override
    public PokemonSpecification deserialize(Type type, Object obj) throws SerializationException {
        return PokemonSpecificationProxy.create(obj.toString()).get();
    }

    @Override
    protected Object serialize(PokemonSpecification item, Predicate<Class<?>> typeSupported) {
        return item.toString();
    }
}
