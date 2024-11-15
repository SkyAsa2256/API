package com.envyful.api.config.database;

import com.envyful.api.config.ConfigTypeSerializer;
import com.envyful.api.config.type.RedisDatabaseDetails;
import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.registry.Registry;
import com.envyful.api.registry.config.KeySerializer;

/**
 *
 * Registry for the different types of database details
 *
 */
public class DatabaseDetailsRegistry {

    private static final Registry<String, Class<DatabaseDetailsConfig>> REGISTRY = Registry.classBased(KeySerializer.identity());

    public static void init() {
        register(SQLDatabaseDetails.ID, SQLDatabaseDetails.class);
        register(RedisDatabaseDetails.ID, RedisDatabaseDetails.class);

        ConfigTypeSerializer.register(REGISTRY.getTypeSerializer(), DatabaseDetailsConfig.class);
    }

    public static Registry<String, Class<DatabaseDetailsConfig>> getRegistry() {
        return REGISTRY;
    }

    public static void register(String id, Class<? extends DatabaseDetailsConfig> configClass) {
        REGISTRY.register(id, (Class<DatabaseDetailsConfig>) configClass);
    }
}
