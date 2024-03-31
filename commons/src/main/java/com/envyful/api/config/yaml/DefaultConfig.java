package com.envyful.api.config.yaml;

/**
 *
 * Represents a default instance of the config
 *
 * @param <T> The config type
 */
public class DefaultConfig<T extends AbstractYamlConfig> {

    private final String fileName;
    private final T instance;
    private final boolean replaceExisting;

    private DefaultConfig(String fileName, T instance, boolean replaceExisting) {
        this.fileName = fileName;
        this.instance = instance;
        this.replaceExisting = replaceExisting;
    }

    /**
     *
     * Gets the file name
     *
     * @return The name of the default file
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     *
     * Gets the default file instance
     *
     * @return The default file instance
     */
    public T getInstance() {
        return this.instance;
    }

    /**
     *
     * Get the boolean for if the existing file should be overwritten
     *
     * @return true if overwriting
     */
    public boolean shouldReplaceExisting() {
        return this.replaceExisting;
    }

    /**
     *
     * Creates a default config instance that will overwrite any file
     * that is already at that location
     * <br>
     * If you do not wish to overwrite existing files use {@link DefaultConfig#onlyNew(String, AbstractYamlConfig)}
     *
     * @param fileName The file name of the default config
     * @param instance The instance values of the default config
     * @return The default config
     * @param <T> The type of the default config
     */
    public static <T extends AbstractYamlConfig> DefaultConfig<T> overwriting(String fileName, T instance) {
        return new DefaultConfig<>(fileName, instance, true);
    }

    /**
     *
     * Creates a default config instance that will only appear if there isn't
     * already a file at the specified location
     * <br>
     * If you need to ensure any user edited files are overwritten use
     * {@link DefaultConfig#overwriting(String, AbstractYamlConfig)}
     *
     * @param fileName The file name of the default config
     * @param instance The instance values of the default config
     * @return The default config
     * @param <T> The type of the default config
     */
    public static <T extends AbstractYamlConfig> DefaultConfig<T> onlyNew(String fileName, T instance) {
        return new DefaultConfig<>(fileName, instance, false);
    }
}
