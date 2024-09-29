package com.envyful.api.jexl.config;

import com.envyful.api.jexl.UtilJexl;
import org.apache.commons.jexl3.JexlExpression;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 *
 * Type serializer for JEXL expressions
 *
 */
public class JexlExpressionTypeSerializer implements TypeSerializer<JexlExpression> {
    @Override
    public JexlExpression deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return UtilJexl.getEngine().createExpression(node.getString());
    }

    @Override
    public void serialize(Type type, @Nullable JexlExpression obj, ConfigurationNode node) throws SerializationException {
        node.set(obj.getSourceText());
    }
}
