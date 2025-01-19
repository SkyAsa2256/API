package com.envyful.api.config.type;

import com.envyful.api.config.database.DatabaseDetailsConfig;
import com.envyful.api.database.Database;
import com.envyful.api.database.impl.SimpleLettuceDatabase;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 *
 * A class representing the details of a Redis database
 *
 */
@ConfigSerializable
public class RedisDatabaseDetails implements DatabaseDetailsConfig {

    public static final String ID = "redis";

    private String ip;
    private int port;
    private String password;

    public RedisDatabaseDetails() {
    }

    public RedisDatabaseDetails(String ip, int port, String password) {
        this.ip = ip;
        this.port = port;
        this.password = password;
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public Database createDatabase() {
        return new SimpleLettuceDatabase(this);
    }
}
