package com.envyful.api.player;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.json.UtilGson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 *
 * Utility class for manipulating, or getting, player's UUIDs
 *
 */
public class UtilUuid {

    /**
     *
     * Gets the player's UUID from their name
     * <br>
     * This will return null if there is an error in fetching the UUID
     * from Mojang's API.
     * <br>
     * All errors are logged using {@link UtilLogger}
     *
     * @param name The player's name
     * @return The UUID fetched from Mojang's API
     */
    public static UUID getUUIDFromName(String name) {
        PlayerProfile result = getProfile(name);

        if (result == null) {
            return null;
        }

        return formatUuid(result.getId());
    }

    private static PlayerProfile getProfile(String name) {
        try {
            var connection = (HttpURLConnection) new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoInput(true);

            var response = new StringBuffer();

            try (var inputStream = connection.getInputStream();
                 var inputStreamReader = new InputStreamReader(inputStream);
                 var reader = new BufferedReader(inputStreamReader)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
            }

            return UtilGson.GSON.fromJson(response.toString(), PlayerProfile.class);
        } catch (IOException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Failed to get UUID from name: " + name, e));
        }

        return null;
    }

    public static UUID formatUuid(String uuid) {
        if(uuid == null) {
            throw new IllegalArgumentException();
        }

        return uuid.length() == 32 ? convertUnDashedUuid(uuid.replaceAll("-", "")) : UUID.fromString(uuid);
    }

    public static UUID convertUnDashedUuid(String uuid) {
        if (uuid == null || uuid.length() != 32) {
            throw new IllegalArgumentException();
        }

        StringBuilder builder = new StringBuilder(uuid.trim());
        builder.insert(20, "-");
        builder.insert(16, "-");
        builder.insert(12, "-");
        builder.insert(8, "-");
        return UUID.fromString(builder.toString());
    }

    private static class PlayerProfile {

        private String name;
        private String id;

        public String getId() {
            return id;
        }

        public String getName() {
            return this.name;
        }
    }
}
