package com.envyful.api.player.save.impl;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.database.Database;
import com.envyful.api.json.UtilGson;
import com.envyful.api.player.Attribute;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.save.AbstractSaveManager;
import com.envyful.api.player.save.attribute.DataDirectory;
import com.envyful.api.player.save.attribute.TypeAdapter;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class JsonSaveManager<T> extends AbstractSaveManager<T> {

    private static final GsonBuilder GSON_BUILDER = new GsonBuilder()
            .setPrettyPrinting();

    private static Gson gson = null;

    protected final Map<Class<? extends Attribute<?, T>>, String> attributeDirectories = Maps.newHashMap();

    public JsonSaveManager(PlayerManager<?, T> playerManager) {
        super(playerManager);
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = GSON_BUILDER.create();
        }

        return gson;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Attribute<B, T>, B> CompletableFuture<A> loadAttribute(Class<? extends A> attributeClass, B id) {
        Preconditions.checkNotNull(attributeClass, "Cannot load attribute with null class");
        Preconditions.checkNotNull(id, "Cannot load attribute with null id");

        return CompletableFuture.supplyAsync(() -> {
                    var data = (PlayerManager.AttributeData<A, B, T>) this.registeredAttributes.get(attributeClass);

                    if (data.shared()) {
                        A sharedAttribute = (A) this.getSharedAttribute(data.attributeClass(), id);

                        if (sharedAttribute == null) {
                            sharedAttribute = this.readData(data, id);
                            sharedAttribute.load(id);
                            this.addSharedAttribute(id, sharedAttribute);
                        }

                        return sharedAttribute;
                    } else {
                        return this.readData(data, id);
                    }
                }, UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE)
                .exceptionally(throwable -> {
                    UtilLogger.logger().ifPresent(logger -> logger.error("Error when loading attribute data for " + attributeClass.getName(), throwable));
                    return null;
                });
    }

    protected <A extends Attribute<B, T>, B> A readData(PlayerManager.AttributeData<A, B, T> data, B key) {
        String dataDirectory = this.attributeDirectories.get(data.attributeClass());
        File file = Paths.get(dataDirectory, key.toString() + ".json").toFile();

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                Files.createFile(file.toPath());
            } catch (IOException e) {
                UtilLogger.logger().ifPresent(logger -> logger.error("Error loading file for " + data.attributeClass().getName() + " for key " + key, e));
            }
            return data.constructor().get();
        }

        try (FileReader fileWriter = new FileReader(file)) {
            return UtilGson.GSON.fromJson(new JsonReader(fileWriter), data.attributeClass());
        } catch (IOException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Error loading file for " + data.attributeClass().getName() + " for key " + key, e));
        }

        return data.constructor().get();
    }

    @Override
    public <A> void saveData(A id, Attribute<A, T> attribute) {
        String dataDirectory = this.attributeDirectories.get(attribute.getClass());
        File file = Paths.get(dataDirectory, id.toString() + ".json").toFile();

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                Files.createFile(file.toPath());
            } catch (IOException e) {
                UtilLogger.logger().ifPresent(logger -> logger.error("There was an error creating the file", e));
            }
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            getGson().toJson(attribute, attribute.getClass(), new JsonWriter(fileWriter));
        } catch (IOException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("There was an error writing to the file", e));
        }
    }

    @Override
    public <A extends Attribute<B, T>, B> void registerAttribute(PlayerManager.AttributeData<A, B, T> attribute) {
        DataDirectory dataDirectory = attribute.attributeClass().getAnnotation(DataDirectory.class);

        if (dataDirectory == null) {
            return;
        }

        TypeAdapter typeAdapter = attribute.attributeClass().getAnnotation(TypeAdapter.class);

        if (typeAdapter != null) {
            try {
                GSON_BUILDER.registerTypeAdapter(attribute.attributeClass(), typeAdapter.value().newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                UtilLogger.logger()
                        .ifPresent(logger -> logger.error(
                                "Error registering type adapter for: " + attribute.attributeClass().getSimpleName(), e));
            }
        }

        this.attributeDirectories.put(attribute.attributeClass(), dataDirectory.value());

        super.registerAttribute(attribute);
    }

    @Override
    public boolean delete(Database database, String name) {
        return this.delete(name);
    }

    @Override
    public boolean delete(String name) {
        try {
            Files.delete(Paths.get(name));
            return true;
        } catch (IOException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Error deleting file " + name, e));
        }

        return false;
    }
}
