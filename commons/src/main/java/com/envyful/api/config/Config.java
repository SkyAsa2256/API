package com.envyful.api.config;

import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 *
 * Interface that represents a config class
 *
 */
public interface Config {

    /**
     *
     * Gets the main configuration node of the config
     *
     * @return The main node
     */
    ConfigurationNode getNode();

    /**
     *
     * Gets the path of the config
     *
     * @return The path
     */
    Path path();

    /**
     *
     * Copies the config to a new path
     *
     * @param path The path to copy to
     * @return The new config file
     * @throws IOException If an error occurs during saving
     */
    File copyTo(Path path) throws IOException;

    /**
     *
     * Saves the {@link Config#getNode()} to the file
     *
     */
    void save();

}
