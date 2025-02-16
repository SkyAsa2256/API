package com.envyful.api.config.type;

import com.envyful.api.config.database.DatabaseDetailsConfig;
import com.envyful.api.database.Database;
import com.envyful.api.database.impl.SimpleHikariDatabase;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public  class SQLDatabaseDetails implements DatabaseDetailsConfig {

    public static final String ID = "sql";

    public static final SQLDatabaseDetails DEFAULT = new SQLDatabaseDetails(
            "pool-name", "0.0.0.0", 3306, "username", "password", "database"
    );

    private String poolName;
    private String ip;
    private int port;
    private String username;
    private String password;
    private String database;
    private int maxPoolSize = 30;
    private String connectionUrl = null;
    private long maxLifeTimeSeconds = 30;
    private boolean disableSSL = false;

    public SQLDatabaseDetails() {
    }

    public SQLDatabaseDetails(String poolName, String ip, int port, String username, String password, String database) {
        this(poolName, ip, port, username, password, database, 30, 30);
    }

    public SQLDatabaseDetails(String poolName, String ip, int port, String username, String password, String database, int maxPoolSize, long maxLifeTimeSeconds) {
        this.poolName = poolName;
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
        this.maxPoolSize = maxPoolSize;
        this.maxLifeTimeSeconds = maxLifeTimeSeconds;
    }

    @Override
    public String id() {
        return ID;
    }

    public String getPoolName() {
        return this.poolName;
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getDatabase() {
        return this.database;
    }

    public int getMaxPoolSize() {
        return this.maxPoolSize;
    }

    public String getConnectionUrl() {
        return this.connectionUrl;
    }

    public long getMaxLifeTimeSeconds() {
        return this.maxLifeTimeSeconds;
    }

    public boolean isDisableSSL() {
        return this.disableSSL;
    }

    @Override
    public Database createDatabase() {
        return new SimpleHikariDatabase(this);
    }
}
