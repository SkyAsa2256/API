package com.envyful.api.config.serializer;

import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class PatternSerializer extends ScalarSerializer<Pattern> {

    public PatternSerializer() {
        super(Pattern.class);
    }

    @Override
    public Pattern deserialize(Type type, Object obj)
            throws SerializationException {
        return Pattern.compile((String) obj);
    }

    @Override
    protected Object serialize(
            Pattern item, Predicate<Class<?>> typeSupported) {
        return item.pattern();
    }
}
