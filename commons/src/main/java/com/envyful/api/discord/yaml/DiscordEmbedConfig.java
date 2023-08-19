package com.envyful.api.discord.yaml;

import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.discord.DiscordEmbed;
import com.envyful.api.discord.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class DiscordEmbedConfig extends AbstractYamlConfig {

    private String title;
    private String description;
    private String url;
    private DiscordColor color;
    private Footer footer;
    private Thumbnail thumbnail;
    private Image image;
    private Author author;
    private Map<String, Field> fields;

    public DiscordEmbedConfig() {
    }

    public DiscordEmbedConfig(Builder builder) {
        this.title = builder.title;
        this.description = builder.description;
        this.url = builder.url;
        this.color = builder.color;
        this.footer = builder.footer;
        this.thumbnail = builder.thumbnail;
        this.image = builder.image;
        this.author = builder.author;
        this.fields = Maps.newHashMap();

        for (Field field : builder.fields) {
            fields.put("exmaple-" + builder.fields.size(), field);
        }
    }

    /**
     *
     * Converts the {@link DiscordEmbed} to a {@link JSONObject}
     *
     * @return the JSON version of the embed
     */
    public JSONObject toJson() {
        JSONObject jsonEmbed = new JSONObject();

        jsonEmbed.put("title", this.title);
        jsonEmbed.put("description", this.description);
        jsonEmbed.put("url", this.url);

        Footer writtenFooter = this.footer;
        Image writtenImage = this.image;
        Thumbnail writtenThumbnail = this.thumbnail;
        Author writtenAuthor = this.author;
        List<Field> writtenFields = Lists.newArrayList(this.fields.values());
        Color writtenColor = this.color.getColor();

        if (writtenColor != null) {
            int rgb = writtenColor.getRed();
            rgb = (rgb << 8) + writtenColor.getGreen();
            rgb = (rgb << 8) + writtenColor.getBlue();

            jsonEmbed.put("color", rgb);
        }

        if (writtenFooter != null) {
            JSONObject jsonFooter = new JSONObject();

            jsonFooter.put("text", writtenFooter.getText());
            jsonFooter.put("icon_url", writtenFooter.getIconUrl());
            jsonEmbed.put("footer", jsonFooter);
        }

        if (writtenImage != null) {
            JSONObject jsonImage = new JSONObject();

            jsonImage.put("url", writtenImage.getUrl());
            jsonEmbed.put("image", jsonImage);
        }

        if (writtenThumbnail != null) {
            JSONObject jsonThumbnail = new JSONObject();

            jsonThumbnail.put("url", writtenThumbnail.getUrl());
            jsonEmbed.put("thumbnail", jsonThumbnail);
        }

        if (writtenAuthor != null) {
            JSONObject jsonAuthor = new JSONObject();

            jsonAuthor.put("name", writtenAuthor.getName());
            jsonAuthor.put("url", writtenAuthor.getUrl());
            jsonAuthor.put("icon_url", writtenAuthor.getIconUrl());
            jsonEmbed.put("author", jsonAuthor);
        }

        List<JSONObject> jsonFields = new ArrayList<>();
        for (Field field : writtenFields) {
            JSONObject jsonField = new JSONObject();

            jsonField.put("name", field.getName());
            jsonField.put("value", field.getValue());
            jsonField.put("inline", field.isInline());

            jsonFields.add(jsonField);
        }

        jsonEmbed.put("fields", jsonFields.toArray());
        return jsonEmbed;
    }

    public static Builder builder() {
        return new Builder();
    }

    @ConfigSerializable
    public static class DiscordColor {

        private transient Color cachedColor;
        private int red;
        private int green;
        private int blue;
        private int alpha;

        public DiscordColor(int red, int green, int blue, int alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        public DiscordColor() {
        }

        public Color getColor() {
            if (this.cachedColor == null) {
                this.cachedColor = new Color(this.red, this.green, this.blue, this.alpha);
            }

            return this.cachedColor;
        }
    }

    @ConfigSerializable
    public static class Footer {

        private String text;
        private String iconUrl;

        public Footer(String text, String iconUrl) {
            this.text = text;
            this.iconUrl = iconUrl;
        }

        public Footer() {
        }

        public String getText() {
            return text;
        }

        public String getIconUrl() {
            return iconUrl;
        }

    }

    @ConfigSerializable
    public static class Thumbnail {

        private String url;

        public Thumbnail(String url) {
            this.url = url;
        }

        public Thumbnail() {
        }

        public String getUrl() {
            return url;
        }

    }

    @ConfigSerializable
    public static class Image {

        private String url;

        public Image(String url) {
            this.url = url;
        }

        public Image() {
        }

        public String getUrl() {
            return url;
        }

    }

    @ConfigSerializable
    public static class Author {
        private String name;
        private String url;
        private String iconUrl;

        public Author(String name, String url, String iconUrl) {
            this.name = name;
            this.url = url;
            this.iconUrl = iconUrl;
        }

        public Author() {
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public String getIconUrl() {
            return iconUrl;
        }

    }

    @ConfigSerializable
    public static class Field {
        private String name;
        private String value;
        private boolean inline;

        public Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }

        public Field() {
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public boolean isInline() {
            return inline;
        }
    }

    public static class Builder {

        private String title;
        private String description;
        private String url;
        private DiscordColor color;
        private Footer footer;
        private Thumbnail thumbnail;
        private Image image;
        private Author author;
        private List<Field> fields = Lists.newArrayList();

        protected Builder() {
            // Reduce access to static method
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder color(DiscordColor color) {
            this.color = color;
            return this;
        }

        public Builder footer(Footer footer) {
            this.footer = footer;
            return this;
        }

        public Builder thumbnail(Thumbnail thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public Builder image(Image image) {
            this.image = image;
            return this;
        }

        public Builder author(Author author) {
            this.author = author;
            return this;
        }

        public Builder fields(Field... fields) {
            this.fields.addAll(Arrays.asList(fields));
            return this;
        }

        public DiscordEmbedConfig build() {
            return new DiscordEmbedConfig(this);
        }
    }
}
