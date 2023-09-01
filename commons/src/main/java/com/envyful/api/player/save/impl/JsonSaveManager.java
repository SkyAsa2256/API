package com.envyful.api.player.save.impl;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.Attribute;
import com.envyful.api.player.attribute.PlayerAttribute;
import com.envyful.api.player.save.AbstractSaveManager;
import com.envyful.api.player.save.attribute.DataDirectory;
import com.envyful.api.player.save.attribute.TypeAdapter;
import com.google.common.collect.Lists;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class JsonSaveManager<T> extends AbstractSaveManager<T> {

    private static final GsonBuilder GSON_BUILDER = new GsonBuilder()
            .setPrettyPrinting();

    private static Gson gson = null;

    protected final Map<Class<? extends Attribute<?>>, String> attributeDirectories = Maps.newHashMap();

    public JsonSaveManager(PlayerManager<?, ?> playerManager) {
        super(playerManager);
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = GSON_BUILDER.create();
        }

        return gson;
    }

    @Override
    public CompletableFuture<List<Attribute<?>>> loadData(UUID uuid) {
        if (this.registeredAttributes.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        List<Attribute<?>> attributes = Lists.newArrayList();
        List<CompletableFuture<Attribute<?>>> loadTasks = Lists.newArrayList();

        for (Map.Entry<Class<? extends Attribute<?>>, AttributeData<?, ?>> entry : this.registeredAttributes.entrySet()) {
            AttributeData<?, ?> value = entry.getValue();
            Attribute<?> attribute = value.getConstructor().get();

            loadTasks.add(attribute.getId(uuid).thenApply(o -> {
                if (o == null) {
                    return null;
                }

                if (attribute.isShared()) {
                    Attribute<?> sharedAttribute = this.getSharedAttribute(o);

                    if (sharedAttribute == null) {
                        sharedAttribute = this.readData(entry.getKey(), attribute, o);
                        this.addSharedAttribute(o, sharedAttribute);
                    }

                    return sharedAttribute;
                } else {
                    Attribute<?> loaded = this.readData(entry.getKey(), attribute, o);

                    if (loaded instanceof PlayerAttribute) {
                        ((PlayerAttribute) loaded).setParent(this.playerManager.getPlayer(uuid));
                    }

                    return loaded;
                }
            }).whenComplete((loaded, throwable) -> {
                if (loaded != null) {
                    attributes.add(loaded);
                } else if (throwable != null) {
                    throwable.printStackTrace();
                }
            }));
        }

        return CompletableFuture.allOf(loadTasks.toArray(new CompletableFuture[0])).thenApply(unused -> attributes);
    }

    protected Attribute<?> readData(
            Class<? extends Attribute<?>> attributeClass,
            Attribute<?> original,
            Object key
    ) {
        String dataDirectory = this.attributeDirectories.get(attributeClass);
        File file = Paths.get(dataDirectory, key.toString() + ".json").toFile();

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                Files.createFile(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return original;
        }

        try (FileReader fileWriter = new FileReader(file)) {
            return getGson().fromJson(new JsonReader(fileWriter), attributeClass);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return original;
    }

    @Override
    public <A extends Attribute<?>, B> A loadAttribute(Class<? extends A> attributeClass, B id) {
        if (id == null) {
            return null;
        }

        AttributeData<?, A> attributeData = (AttributeData<?, A>) this.registeredAttributes.get(attributeClass);
        A attribute = attributeData.getConstructor().get();

        if (attribute.isShared()) {
           A sharedAttribute = (A) this.getSharedAttribute(id);

            if (sharedAttribute == null) {
                sharedAttribute = (A) this.readData(attributeClass, attribute, id);
                this.addSharedAttribute(id, sharedAttribute);
            }

            return sharedAttribute;
        } else {
            return (A) this.readData(attributeClass, attribute, id);
        }
    }

    @Override
    public void saveData(UUID uuid, Attribute<?> attribute) {
        String dataDirectory = this.attributeDirectories.get(attribute.getClass());
        File file = Paths.get(dataDirectory, uuid.toString() + ".json").toFile();

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
    public void registerAttribute(Class<? extends Attribute<?>> attribute) {
        DataDirectory dataDirectory = attribute.getAnnotation(DataDirectory.class);

        if (dataDirectory == null) {
            return;
        }

        TypeAdapter typeAdapter = attribute.getAnnotation(TypeAdapter.class);

        if (typeAdapter != null) {
            try {
                GSON_BUILDER.registerTypeAdapter(attribute, typeAdapter.value().newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                UtilLogger.logger()
                        .ifPresent(logger -> logger.error(
                                "Error registering type adapter for: " + attribute.getSimpleName(), e));
            }
        }

        this.attributeDirectories.put(attribute, dataDirectory.value());
        super.registerAttribute(attribute);
    }
}
