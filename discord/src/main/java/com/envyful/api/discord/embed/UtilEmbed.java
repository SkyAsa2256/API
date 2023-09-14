package com.envyful.api.discord.embed;

import com.envyful.api.discord.DiscordEmbed;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 *
 * Static utility for handling discord {@link MessageEmbed}s
 *
 */
public class UtilEmbed {

    /**
     *
     * Static utility method for converting from the JSON API object to the actual Discord {@link MessageEmbed} object
     *
     * @param object The original JSON object
     * @param replacers Replacers for handling any configurable placeholders
     * @return The original embed
     */
    public static MessageEmbed fromAPI(DiscordEmbed object, Placeholder... replacers) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(PlaceholderFactory.handlePlaceholders(object.getTitle(), replacers).get(0))
                .setColor(object.getColor());

        if (object.getImage() != null) {
            builder.setImage(PlaceholderFactory.handlePlaceholders(object.getImage().getUrl(), replacers).get(0));
        }

        if (object.getAuthor() != null) {
            builder.setAuthor(
                    PlaceholderFactory.handlePlaceholders(object.getAuthor().getName(), replacers).get(0),
                    PlaceholderFactory.handlePlaceholders(object.getAuthor().getUrl(), replacers).get(0),
                    PlaceholderFactory.handlePlaceholders(object.getAuthor().getIconUrl(), replacers).get(0));
        }

        if (object.getDescription() != null) {
            builder.setDescription(PlaceholderFactory.handlePlaceholders(object.getDescription(), replacers).get(0));
        }

        if (object.getThumbnail() != null) {
            builder.setThumbnail(PlaceholderFactory.handlePlaceholders(object.getThumbnail().getUrl(), replacers).get(0));
        }

        if (object.getFooter() != null) {
            builder.setFooter(PlaceholderFactory.handlePlaceholders(object.getFooter().getText(), replacers).get(0), PlaceholderFactory.handlePlaceholders(object.getFooter().getIconUrl(), replacers).get(0));
        }

        for (DiscordEmbed.Field field : object.getFields()) {
            builder.addField(PlaceholderFactory.handlePlaceholders(field.getName(), replacers).get(0),
                    PlaceholderFactory.handlePlaceholders(field.getValue(), replacers).get(0),
                    field.isInline());
        }

        return builder.build();
    }
}
