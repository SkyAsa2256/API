package com.envyful.api.discord.yaml;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@ConfigSerializable
public class DiscordWebHookConfig extends AbstractYamlConfig {

    protected boolean enabled;
    protected String url;
    protected String content;
    protected String username;
    protected String avatarUrl;
    protected boolean tts;
    protected Map<String, DiscordEmbedConfig> embeds;

    public DiscordWebHookConfig(Builder builder) {
        this.enabled = builder.enabled;
        this.url = builder.url;
        this.content = builder.content;
        this.username = builder.username;
        this.avatarUrl = builder.avatarUrl;
        this.tts = builder.tts;
        this.embeds = new HashMap<>();

        for (DiscordEmbedConfig embed : builder.embeds) {
            this.embeds.put("example-" + this.embeds.size(), embed);
        }
    }

    public DiscordWebHookConfig() {
    }

    /**
     *
     * Sends the message to the web hook URL asynchronously
     *
     * @param placeholders The placeholders to replace in the message
     * @return The future of the task
     */
    public CompletableFuture<Void> executeAsync(Placeholder... placeholders) {
        return this.executeAsync(UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE, placeholders);
    }

    /**
     *
     * Sends the message to the web hook URL asynchronously
     *
     * @param executorService The executor service to run the task on
     * @param placeholders The placeholders to replace in the message
     * @return The future of the task
     */
    public CompletableFuture<Void> executeAsync(ExecutorService executorService, Placeholder... placeholders) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                this.execute(placeholders);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return null;
        }, executorService);
    }

    /**
     *
     * Executes the message and sends it to the web hook URL
     *
     * @throws IOException Exception when something is incorrect or goes wrong
     */
    public void execute(Placeholder... placeholders) throws IOException {
        if (!this.enabled) {
            return;
        }

        if (this.content == null && this.embeds.isEmpty()) {
            throw new IllegalArgumentException(
                    "Set content or add at least one EmbedObject"
            );
        }

        var json = this.toJson();
        var text = PlaceholderFactory.handlePlaceholders(Collections.singletonList(json.toString()), placeholders).get(0);
        var url = new URL(this.url);
        var connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "EnvyWare");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        try (var outputStream = connection.getOutputStream()) {
            outputStream.write(text.getBytes(StandardCharsets.UTF_8));
            connection.getInputStream().close();
            connection.disconnect();
        } catch (IOException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Error executing Discord WebHook: '" + this.url + "' JSON: '" + text + "'", e));
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("url", this.url);
        json.addProperty("content", this.content);
        json.addProperty("username", this.username);
        json.addProperty("avatar_url", this.avatarUrl);
        json.addProperty("tts", this.tts);

        if (!this.embeds.isEmpty()) {
            JsonArray embedObjects = new JsonArray();

            for (DiscordEmbedConfig embed : this.embeds.values()) {
                embedObjects.add(embed.toJson());
            }

            json.add("embeds", embedObjects);
        }

        return json;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private boolean enabled = true;
        private String url;
        private String content;
        private String username;
        private String avatarUrl;
        private boolean tts;
        private List<DiscordEmbedConfig> embeds = new ArrayList<>();

        Builder() {}

        public Builder enabled() {
            this.enabled = true;
            return this;
        }

        public Builder disabled() {
            this.enabled = false;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public Builder tts(boolean tts) {
            this.tts = tts;
            return this;
        }

        public Builder embeds(DiscordEmbedConfig... embeds) {
            this.embeds.addAll(Arrays.asList(embeds));
            return this;
        }

        public DiscordWebHookConfig build() {
            return new DiscordWebHookConfig(this);
        }
    }
}
