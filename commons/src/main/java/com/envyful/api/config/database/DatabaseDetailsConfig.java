package com.envyful.api.config.database;

import com.envyful.api.database.Database;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public interface DatabaseDetailsConfig {

    String id();

    Database createDatabase();

}
