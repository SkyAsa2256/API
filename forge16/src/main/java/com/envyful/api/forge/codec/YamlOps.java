//package com.envyful.api.forge.codec;
//
//import com.mojang.datafixers.util.Pair;
//import com.mojang.serialization.DataResult;
//import com.mojang.serialization.DynamicOps;
//import org.spongepowered.configurate.BasicConfigurationNode;
//import org.spongepowered.configurate.ConfigurationNode;
//import org.spongepowered.configurate.ConfigurationOptions;
//
//import java.util.stream.Stream;
//
//public class YamlOps implements DynamicOps<ConfigurationNode> {
//
//    private final ConfigurationOptions options;
//
//    public YamlOps(ConfigurationOptions options) {
//        this.options = options;
//    }
//
//    @Override
//    public ConfigurationNode empty() {
//        return BasicConfigurationNode.root(this.options);
//    }
//
//    @Override
//    public <U> U convertTo(DynamicOps<U> dynamicOps, ConfigurationNode configurationNode) {
//        return null;
//    }
//
//    @Override
//    public DataResult<Number> getNumberValue(ConfigurationNode configurationNode) {
//        return null;
//    }
//
//    @Override
//    public ConfigurationNode createNumeric(Number number) {
//        return null;
//    }
//
//    @Override
//    public DataResult<String> getStringValue(ConfigurationNode configurationNode) {
//        return null;
//    }
//
//    @Override
//    public ConfigurationNode createString(String s) {
//        return
//    }
//
//    @Override
//    public DataResult<ConfigurationNode> mergeToList(ConfigurationNode configurationNode, ConfigurationNode t1) {
//        return null;
//    }
//
//    @Override
//    public DataResult<ConfigurationNode> mergeToMap(ConfigurationNode configurationNode, ConfigurationNode t1, ConfigurationNode t2) {
//        return null;
//    }
//
//    @Override
//    public DataResult<Stream<Pair<ConfigurationNode, ConfigurationNode>>> getMapValues(ConfigurationNode configurationNode) {
//        return null;
//    }
//
//    @Override
//    public ConfigurationNode createMap(Stream<Pair<ConfigurationNode, ConfigurationNode>> stream) {
//        return null;
//    }
//
//    @Override
//    public DataResult<Stream<ConfigurationNode>> getStream(ConfigurationNode configurationNode) {
//        return null;
//    }
//
//    @Override
//    public ConfigurationNode createList(Stream<ConfigurationNode> stream) {
//        return null;
//    }
//
//    @Override
//    public ConfigurationNode remove(ConfigurationNode configurationNode, String s) {
//        return null;
//    }
//}
