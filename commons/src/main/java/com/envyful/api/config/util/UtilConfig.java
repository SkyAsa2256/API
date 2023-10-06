package com.envyful.api.config.util;

import com.envyful.api.concurrency.UtilLogger;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * Static utility class for handling configuration methods (such as getting lists)
 *
 */
public class UtilConfig {

    public static <T> List<T> getList(ConfigurationNode node, Class<T> type, Object... path) {
        try {
            return node.node(path).getList(type);
        } catch (SerializationException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Error reading type as a list for " + Arrays.toString(path), e));
        }

        return Collections.emptyList();
    }

}
