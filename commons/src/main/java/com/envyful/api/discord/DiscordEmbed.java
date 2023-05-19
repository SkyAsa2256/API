package com.envyful.api.discord;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class DiscordEmbed {

    private String title;
    private String description;
    private String url;
    private Color color;
    private Footer footer;
    private Thumbnail thumbnail;
    private Image image;
    private Author author;

    private final List<Field> fields = Lists.newArrayList();

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public Color getColor() {
        return color;
    }

    public Footer getFooter() {
        return footer;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public Image getImage() {
        return image;
    }

    public Author getAuthor() {
        return author;
    }

    public List<Field> getFields() {
        return fields;
    }

    public DiscordEmbed setTitle(String title) {
        this.title = title;
        return this;
    }

    public DiscordEmbed setDescription(String description) {
        this.description = description;
        return this;
    }

    public DiscordEmbed setUrl(String url) {
        this.url = url;
        return this;
    }

    public DiscordEmbed setColor(Color color) {
        this.color = color;
        return this;
    }

    public DiscordEmbed setFooter(String text, String icon) {
        this.footer = new Footer(text, icon);
        return this;
    }

    public DiscordEmbed setFooter(Footer footer) {
        this.footer = footer;
        return this;
    }

    public DiscordEmbed setThumbnail(String url) {
        this.thumbnail = new Thumbnail(url);
        return this;
    }

    public DiscordEmbed setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    public DiscordEmbed setImage(String url) {
        this.image = new Image(url);
        return this;
    }

    public DiscordEmbed setImage(Image image) {
        this.image = image;
        return this;
    }

    public DiscordEmbed setAuthor(String name, String url, String icon) {
        this.author = new Author(name, url, icon);
        return this;
    }

    public DiscordEmbed setAuthor(Author author) {
        this.author = author;
        return this;
    }

    public DiscordEmbed addField(String name, String value, boolean inline) {
        this.fields.add(new Field(name, value, inline));
        return this;
    }

    public DiscordEmbed addField(Field field) {
        this.fields.add(field);
        return this;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     *
     * Converts the {@link DiscordEmbed} to a {@link JSONObject}
     *
     * @return the JSON version of the embed
     */
    public JSONObject toJson() {
        JSONObject jsonEmbed = new JSONObject();

        jsonEmbed.put("title", this.getTitle());
        jsonEmbed.put("description", this.getDescription());
        jsonEmbed.put("url", this.getUrl());

        DiscordEmbed.Footer writtenFooter = this.getFooter();
        DiscordEmbed.Image writtenImage = this.getImage();
        DiscordEmbed.Thumbnail writtenThumbnail = this.getThumbnail();
        DiscordEmbed.Author writtenAuthor = this.getAuthor();
        List<DiscordEmbed.Field> writtenFields = this.getFields();
        Color writtenColor = this.getColor();

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
        for (DiscordEmbed.Field field : writtenFields) {
            JSONObject jsonField = new JSONObject();

            jsonField.put("name", field.getName());
            jsonField.put("value", field.getValue());
            jsonField.put("inline", field.isInline());

            jsonFields.add(jsonField);
        }

        jsonEmbed.put("fields", jsonFields.toArray());
        return jsonEmbed;
    }

    /**
     *
     * Converts a JSON string to an Embed
     *
     * @param json The json being converted
     * @return The new embed
     */
    public static DiscordEmbed fromJson(String json) {
        JsonObject jsonElement = JsonParser.parseString(json).getAsJsonObject();
        DiscordEmbed discordEmbed = new DiscordEmbed();

        checkExistsMapThenApply(jsonElement, "title",
                JsonElement::getAsString, discordEmbed::setTitle);
        checkExistsMapThenApply(jsonElement, "description",
                JsonElement::getAsString, discordEmbed::setDescription);
        checkExistsMapThenApply(jsonElement, "url",
                JsonElement::getAsString, discordEmbed::setUrl);

        if (jsonElement.has("color")) {
            int rgb = jsonElement.get("color").getAsInt();
            int red = (rgb >> 16) & 0xFF;
            int green = (rgb >> 8) & 0xFF;
            int blue = rgb & 0xFF;
            discordEmbed.setColor(new Color(red, green, blue));
        }

        if (jsonElement.has("footer")) {
            JsonObject footer = jsonElement.get("footer").getAsJsonObject();
            discordEmbed.setFooter(
                    footer.get("text").getAsString(),
                    footer.get("icon_url").getAsString());
        }

        if (jsonElement.has("image")) {
            JsonObject image = jsonElement.get("image").getAsJsonObject();
            discordEmbed.setImage(image.get("url").getAsString());
        }

        if (jsonElement.has("thumbnail")) {
            JsonObject thumbnail = jsonElement.get("thumbnail")
                    .getAsJsonObject();
            discordEmbed.setThumbnail(thumbnail.get("url").getAsString());
        }

        if (jsonElement.has("author")) {
            JsonObject author = jsonElement.get("author").getAsJsonObject();
            discordEmbed.setAuthor(
                    author.get("name").getAsString(),
                    author.get("url").getAsString(),
                    author.get("icon_url").getAsString());
        }

        if (jsonElement.has("fields")) {
            JsonArray fields = jsonElement.get("fields").getAsJsonArray();

            for (JsonElement field : fields) {
                JsonObject fieldObject = field.getAsJsonObject();
                discordEmbed.addField(
                        fieldObject.get("name").getAsString(),
                        fieldObject.get("value").getAsString(),
                        fieldObject.get("inline").getAsBoolean());
            }
        }

        return discordEmbed;
    }

    private static <T> void checkExistsMapThenApply(JsonObject json, String key,
                                             Function<JsonElement, T> mapper,
                                                    Consumer<T> application) {
        if (!json.has(key)) {
            return;
        }

        T apply = mapper.apply(json.get(key));
        application.accept(apply);
    }

    public static class Footer {
        private String text;
        private String iconUrl;

        public Footer(String text, String iconUrl) {
            this.text = text;
            this.iconUrl = iconUrl;
        }

        public String getText() {
            return text;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setText(String text) {
            this.text = text;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }
    }

    public static class Thumbnail {
        private String url;

        public Thumbnail(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Image {
        private String url;

        public Image(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Author {
        private String name;
        private String url;
        private String iconUrl;

        public Author(String name, String url, String iconUrl) {
            this.name = name;
            this.url = url;
            this.iconUrl = iconUrl;
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

        public void setName(String name) {
            this.name = name;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }
    }

    public static class Field {
        private String name;
        private String value;
        private boolean inline;

        public Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
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

        public void setName(String name) {
            this.name = name;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setInline(boolean inline) {
            this.inline = inline;
        }
    }

    public static class Builder {

        private String title;
        private String description;
        private String url;
        private Color color;
        private Footer footer;
        private Thumbnail thumbnail;
        private Image image;
        private Author author;

        private final List<Field> fields = Lists.newArrayList();

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

        public Builder color(Color color) {
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

        public DiscordEmbed build() {
            DiscordEmbed discordEmbed = new DiscordEmbed();

            discordEmbed.setTitle(this.title);
            discordEmbed.setDescription(this.description);
            discordEmbed.setUrl(this.url);

            if (this.color != null) {
                discordEmbed.setColor(this.color);
            }

            if (this.footer != null) {
                discordEmbed.setFooter(this.footer);
            }

            if (this.thumbnail != null) {
                discordEmbed.setThumbnail(this.thumbnail);
            }

            if (this.image != null) {
                discordEmbed.setImage(this.image);
            }

            if (this.author != null) {
                discordEmbed.setAuthor(this.author);
            }

            if (!this.fields.isEmpty()) {
                for (Field field : this.fields) {
                    discordEmbed.addField(field);
                }
            }

            return discordEmbed;
        }
    }
}
