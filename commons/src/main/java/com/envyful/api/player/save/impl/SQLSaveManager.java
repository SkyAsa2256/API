package com.envyful.api.player.save.impl;

import com.envyful.api.database.Database;
import com.envyful.api.database.sql.UtilSql;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;

import java.util.function.BiConsumer;

public class SQLSaveManager<T> extends EmptySaveManager<T> {

    private final Database database;

    public SQLSaveManager(PlayerManager<?, T> playerManager, Database database) {
        super(playerManager);

        this.database = database;
    }

    public SQLSaveManager(PlayerManager<?, T> playerManager, Database database, BiConsumer<EnvyPlayer<T>, Throwable> errorHandler) {
        super(playerManager, errorHandler);

        this.database = database;
    }

    @Override
    public boolean delete(Database database, String name) {
        UtilSql.update(database).query("DELETE FROM " + name + ";").executeAsync();
        return true;
    }

    @Override
    public boolean delete(String name) {
        UtilSql.update(this.database).query("DELETE FROM " + name + ";").executeAsync();
        return true;
    }
}