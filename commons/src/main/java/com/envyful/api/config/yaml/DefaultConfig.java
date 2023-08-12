package com.envyful.api.config.yaml;

public class DefaultConfig<T extends AbstractYamlConfig> {

    private final String fileName;
    private final T instance;

    private DefaultConfig(String fileName, T instance) {
        this.fileName = fileName;
        this.instance = instance;
    }

    public String getFileName() {
        return this.fileName;
    }

    public T getInstance() {
        return this.instance;
    }

    public static <T extends AbstractYamlConfig> DefaultConfig<T> of(String fileName, T instance) {
        return new DefaultConfig<>(fileName, instance);
    }
}
