package com.envyful.api.config.yaml;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.data.YamlConfigStyle;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.configurate.reference.WatchServiceListener;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * Static factory used for loading the YAML configs from their classes.
 *
 */
public class YamlConfigFactory {

    /**
     *
     * Gets the instance of the given config from the class using Sponge's Configurate
     *
     * @param clazz The class that represents a config file
     * @param <T> The type of the class
     * @return The config instance
     * @throws IOException If an error occurs whilst loading the file
     */
    public static <T extends AbstractYamlConfig> T getInstance(Class<T> clazz) throws IOException {
        ConfigPath annotation = clazz.getAnnotation(ConfigPath.class);

        if (annotation == null) {
            throw new IOException("Cannot load config " + clazz.getSimpleName() + " as it's missing @ConfigPath annotation");
        }

        NodeStyle style = getNodeStyle(clazz);
        Path configFile = Paths.get(annotation.value());

        if (!configFile.toFile().exists()) {
            configFile.getParent().toFile().mkdirs();
            configFile.toFile().createNewFile();
        }

        WatchServiceListener listener = WatchServiceListener.builder().build();
        ConfigurationReference<CommentedConfigurationNode> base = listenToConfig(listener, configFile, style);

        if (base == null) {
            throw new IOException("Error config loaded as null");
        }

        ValueReference<T, CommentedConfigurationNode> reference = base.referenceTo(clazz);
        T instance = reference.get();

        if (instance == null) {
            throw new IOException("Error config loaded as null");
        }

        instance.base = base;
        instance.config = reference;
        instance.save();

        return instance;
    }

    private static NodeStyle getNodeStyle(Class<?> clazz) {
        YamlConfigStyle annotation = clazz.getAnnotation(YamlConfigStyle.class);

        if (annotation == null) {
            return NodeStyle.BLOCK;
        }

        return annotation.value();
    }

    private static ConfigurationReference<CommentedConfigurationNode> listenToConfig(WatchServiceListener listener, Path configFile,
                                                                                     NodeStyle style) {
        try {
            return listener.listenToConfiguration(file -> YamlConfigurationLoader.builder()
                    .nodeStyle(style)
                    .defaultOptions(opts -> opts.shouldCopyDefaults(true))
                    .path(file).build(), configFile);
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }

        return null;
    }
}
