package com.envyful.api.player.name.impl;

import com.envyful.api.database.Database;
import com.envyful.api.database.sql.SqlType;
import com.envyful.api.player.name.NameStore;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 *
 * An implementation of the {@link NameStore} that stores the data in a SQL database
 *
 */
public class SQLNameStore implements NameStore {

    private final Database database;

    public SQLNameStore(Database database) {
        this.database = database;

        this.database.update("CREATE TABLE IF NOT EXISTS `envy_api_player_names`(" +
                "id         INT         UNSIGNED        NOT NULL    AUTO_INCREMENT, " +
                "name       VARCHAR(16) NOT NULL, " +
                "uuid       VARCHAR(36) NOT NULL, " +
                "UNIQUE(uuid), " +
                "PRIMARY KEY(id)" +
                ");");
    }

    @Override
    public CompletableFuture<String> getName(UUID uuid) {
        return this.database.query("SELECT name FROM `envy_api_player_names` WHERE uuid = ?;")
                .data(SqlType.text(uuid.toString()))
                .converter(resultSet -> resultSet.getString("name"))
                .executeAsyncWithConverter().thenApply(names -> {
                    if (names.isEmpty()) {
                        return null;
                    }

                    return names.get(0);
                });
    }

    @Override
    public CompletableFuture<UUID> getUUID(String name) {
        return this.database.query("SELECT uuid FROM `envy_api_player_names` WHERE name = ?;")
                .data(SqlType.text(name))
                .converter(resultSet -> UUID.fromString(resultSet.getString("uuid")))
                .executeAsyncWithConverter().thenApply(uuids -> {
                    if (uuids.isEmpty()) {
                        return null;
                    }

                    return uuids.get(0);
                });
    }

    @Override
    public void updateStored(UUID uuid, String name) {
        this.database.update("INSERT INTO `envy_api_player_names` (uuid, name) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE name = VALUES(`name`);")
                .data(SqlType.text(uuid.toString()), SqlType.text(name))
                .executeAsync();
    }
}
