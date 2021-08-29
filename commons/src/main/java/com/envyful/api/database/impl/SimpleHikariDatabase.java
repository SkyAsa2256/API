package com.envyful.api.database.impl;

import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.database.Database;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLData;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 *
 * Hikari SQL implementation of the {@link Database} interface
 *
 */
public class SimpleHikariDatabase implements Database {

    private HikariDataSource hikari;

    public SimpleHikariDatabase(SQLDatabaseDetails details) {
        this(details.getPoolName(), details.getIp(), details.getPort(), details.getUsername(),
                details.getPassword(), details.getDatabase());
    }

    public SimpleHikariDatabase(String name, String ip, int port, String username, String password, String database) {
        HikariConfig config = new HikariConfig();

        config.setMaximumPoolSize(30);
        config.setPoolName(name);
        config.setJdbcUrl("jdbc:mysql://" + ip + ":" + port + "/" + database);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.addDataSourceProperty("serverName", ip);
        config.addDataSourceProperty("port", port);
        config.addDataSourceProperty("databaseName", database);
        config.addDataSourceProperty("user", username);
        config.addDataSourceProperty("password", password);
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
        config.addDataSourceProperty("maxLifetime", TimeUnit.MINUTES.toMillis(5));
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
