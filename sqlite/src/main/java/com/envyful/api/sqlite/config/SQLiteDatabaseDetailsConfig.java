package com.envyful.api.sqlite.config;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.database.DatabaseDetailsConfig;
import com.envyful.api.config.database.DatabaseDetailsRegistry;
import com.envyful.api.database.Database;
import com.envyful.api.sqlite.SQLiteDatabase;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.io.File;
import java.io.IOException;

@ConfigSerializable
public class SQLiteDatabaseDetailsConfig implements DatabaseDetailsConfig {

    public static final String ID = "sqlite";

    private String filePath;

    public SQLiteDatabaseDetailsConfig(String filePath) {
        this.filePath = filePath;
    }

    public SQLiteDatabaseDetailsConfig() {

    }

    public static void register() {
        DatabaseDetailsRegistry.register(ID, SQLiteDatabaseDetailsConfig.class);
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
            return new SQLiteDatabase(this.filePath);
        } catch (Exception e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Failed to create SQLite database", e));
        }

        return null;
    }
}
