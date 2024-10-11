package com.envyful.api.player.name.impl;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.player.name.NameStore;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 * An implementation of the {@link NameStore} that stores the data in a flat file
 *
 */
public class TextNameStore implements NameStore {

    private final File file;
    private final Map<UUID, String> nameCache = new HashMap<>();
    private boolean saving = false;

    public TextNameStore(File file) {
        this.file = file;

        UtilConcurrency.runAsync(() -> {
            try {
                this.createFiles();
            } catch (IOException e) {
                UtilLogger.logger().ifPresent(logger -> logger.error("Failed to create username cache file", e));
            }

            try (var fileReader = new FileReader(file);
                    var reader = new BufferedReader(fileReader)) {
                String line;

                while ((line = reader.readLine()) != null) {
                    String[] args = line.split("@@##@@");
                    this.nameCache.put(UUID.fromString(args[0]), args[1]);
                }
            } catch (IOException e) {
                UtilLogger.logger().ifPresent(logger -> logger.error("Failed to load username cache", e));
            }
        });

        UtilConcurrency.runRepeatingTask(this::save, 5, 5, TimeUnit.MINUTES);
    }

    private void createFiles() throws IOException {
        if (!this.file.getParentFile().exists()) {
            Files.createDirectories(this.file.getParentFile().toPath());
        }

        if (!this.file.exists()) {
            Files.createFile(this.file.toPath());
        }
    }

    public void save() {
        if (this.saving) {
            return;
        }

        this.saving = true;

        try (var fileWriter = new FileWriter(this.file);
             var writer = new BufferedWriter(fileWriter)) {

            for (var entry : this.nameCache.entrySet()) {
                if (!this.saving) {
                    return;
                }

                writer.write(entry.getValue() + "@@##@@" + entry.getKey().getMostSignificantBits() + "@@##@@" + entry.getKey().getLeastSignificantBits() + System.lineSeparator());
            }

            this.saving = false;
        } catch (IOException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Failed to save username cache", e));
        }
    }

    @Override
    public CompletableFuture<String> getName(UUID uuid) {
        return CompletableFuture.completedFuture(this.nameCache.get(uuid));
    }

    @Override
    public CompletableFuture<UUID> getUUID(String name) {
        return CompletableFuture.supplyAsync(() -> {
            for (var entry : this.nameCache.entrySet()) {
                if (entry.getValue().equalsIgnoreCase(name)) {
                    return entry.getKey();
                }
            }

            return null;
        }, UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE);
    }

    @Override
    public void updateStored(UUID uuid, String name) {
        this.nameCache.put(uuid, name);
    }
}
