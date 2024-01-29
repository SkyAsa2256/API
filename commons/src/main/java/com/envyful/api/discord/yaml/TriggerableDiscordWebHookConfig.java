package com.envyful.api.discord.yaml;

import com.google.common.collect.Maps;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ConfigSerializable
public class TriggerableDiscordWebHookConfig extends DiscordWebHookConfig {

    private String trigger;

    public TriggerableDiscordWebHookConfig(Builder builder) {
        this.trigger = builder.trigger;
        this.enabled = builder.enabled;
        this.url = builder.url;
        this.content = builder.content;
        this.username = builder.username;
        this.avatarUrl = builder.avatarUrl;
        this.tts = builder.tts;
        this.embeds = Maps.newHashMap();

        for (DiscordEmbedConfig embed : builder.embeds) {
            this.embeds.put("example-" + this.embeds.size(), embed);
        }
    }

    public TriggerableDiscordWebHookConfig() {
    }

    public String getTrigger() {
        return this.trigger;
    }

    public static Builder triggerBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String trigger;
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

        public Builder trigger(String trigger) {
            this.trigger = trigger;
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

        public TriggerableDiscordWebHookConfig build() {
            return new TriggerableDiscordWebHookConfig(this);
        }
    }
}
