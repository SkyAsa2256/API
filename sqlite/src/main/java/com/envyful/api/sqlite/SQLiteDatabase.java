package com.envyful.api.sqlite;

import com.envyful.api.database.Database;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 *
 * SQLite implementation of the {@link Database} interface
 *
 */
public class SQLiteDatabase implements Database {

    private final HikariDataSource hikari;

    public SQLiteDatabase(String filePath) {
        var config = new HikariConfig();

        config.setMaximumPoolSize(1);
        config.setPoolName("SQLITE");
        config.setJdbcUrl("jdbc:sqlite:" + filePath);
        config.addDataSourceProperty("databaseName", "envyware");
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("cacheCallableStmts", true);
        config.addDataSourceProperty("alwaysSendSetIsolation", false);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("useLocalSessionState", true);
        config.addDataSourceProperty("characterEncoding","utf8");
        config.addDataSourceProperty("useUnicode","true");
        config.addDataSourceProperty("maxLifetime", TimeUnit.SECONDS.toMillis(30));
        config.setMaxLifetime(TimeUnit.SECONDS.toMillis(30));
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(30));
        config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(60));
        config.setConnectionTestQuery("/* Ping */ SELECT 1");

        this.hikari = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.hikari.getConnection();
    }

    @Override
    public void close() {
        this.hikari.close();
    }
}
