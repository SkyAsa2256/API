package com.envyful.api.sqlite.config;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.database.DatabaseDetailsConfig;
import com.envyful.api.config.database.DatabaseDetailsRegistry;
import com.envyful.api.database.Database;
import com.envyful.api.sqlite.H2Database;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.io.File;
import java.io.IOException;

@ConfigSerializable
public class H2DatabaseDetailsConfig implements DatabaseDetailsConfig {

    public static final String ID = "h2";

    private String filePath;

    public H2DatabaseDetailsConfig(String filePath) {
        this.filePath = filePath;
    }

    public H2DatabaseDetailsConfig() {

    }

    public static void register() {
        DatabaseDetailsRegistry.register(ID, H2DatabaseDetailsConfig.class);
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public Database createDatabase() {
        File file = new File(this.filePath);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                UtilLogger.logger().ifPresent(logger -> logger.error("Failed to create SQLite database file", e));
                return null;
            }
        }

        try {
            return new H2Database(this.filePath);
        } catch (Exception e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Failed to create SQLite database", e));
        }

        return null;
    }
}
