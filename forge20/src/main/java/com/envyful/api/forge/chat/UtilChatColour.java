package com.envyful.api.forge.chat;

import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Static utility methods relating to colour codes
 *
 */
public class UtilChatColour {

    public static final Pattern COLOUR_PATTERN = Pattern.compile("&(#\\w{6}|[\\da-zA-Z])");
    public static final Pattern STRIP_PATTERN = Pattern.compile("(?i)&([0-9A-FK-ORX]|#([A-F0-9]{6}|[A-F0-9]{3}))");

    public static List<Component> colour(List<String> text, Placeholder... placeholders) {
        List<Component> components = Lists.newArrayList();

        for (String line : text) {
            components.add(colour(line, placeholders));
        }

        return components;
    }

    /**
     *
     * Parses the string to a {@link Component} with the correctly formatted colour codes and hex codes
     *
     * @param text The unformatted text
     * @return The newly formatted text
     */
    public static Component colour(String text, Placeholder... placeholders) {
        if (text.contains("{")) {
            try {
                return Component.Serializer.fromJson(text);
            } catch (Exception ignored) {}
        }

        Matcher matcher = COLOUR_PATTERN.matcher(text);
        MutableComponent textComponent = Component.literal("");
        ChatFormatting nextApply = null;
        int lastEnd = 0;
        TextColor lastColor = null;

        while (matcher.find()) {
            var start = matcher.start();
            var segment = text.substring(lastEnd, start);
            var iFormattableTextComponent = attemptAppend(textComponent, segment, lastColor, placeholders);

            if (nextApply != null && iFormattableTextComponent != null) {
                iFormattableTextComponent.withStyle(nextApply);
            }

            lastEnd = matcher.end();
            String colourCode = matcher.group(1);
            var colour = parseColour(colourCode);

            if (colour.isPresent()) {
                lastColor = colour.get();
                nextApply = null;
            } else {
                var byCode = ChatFormatting.getByCode(colourCode.toCharArray()[0]);

                if (byCode != null) {
                    nextApply = byCode;
                } else {
                    textComponent.append(Component.literal("&" + colourCode));
                }
            }
        }

        var segment = text.substring(lastEnd);
        var iFormattableTextComponent = attemptAppend(textComponent, segment, lastColor, placeholders);

        if (nextApply != null && iFormattableTextComponent != null) {
            iFormattableTextComponent.withStyle(nextApply);
        }

        return textComponent;
    }

    /**
     *
     * Attempts to append the segment to the {@link Component} with the given (nullable) colour
     *
     * @param textComponent The text component
     * @param segment The segment
     * @param lastColour The colour
     */
    public static MutableComponent attemptAppend(MutableComponent textComponent, String segment, TextColor lastColour, Placeholder... placeholders) {
        if (segment.isEmpty()) {
            return null;
        }

        var appended = Component.empty();

        for (var text : PlaceholderFactory.handlePlaceholders(segment, placeholders)) {
            var literalText = Component.literal(text);

            if (lastColour != null) {
                literalText.setStyle(Style.EMPTY.withColor(lastColour));
            }

            appended.append(literalText);
        }

        textComponent.append(appended);
        return appended;
    }

    /**
     *
     * Attempts to parse the colour code firstly as a hex, then as a legacy
     *
     * @param colourCode The colour code
     * @return The potential equivalent colour
     */
    public static Optional<TextColor> parseColour(String colourCode) {
        TextColor colour = TextColor.parseColor(colourCode);

        if (colour != null) {
            return Optional.of(colour);
        }

        if (colourCode.length() > 1) {
            return Optional.empty();
        }

        ChatFormatting byCode = getByCode(colourCode.toCharArray()[0]);

        if (byCode == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(TextColor.fromLegacyFormat(byCode));
    }

    public static ChatFormatting getByCode(char p_211165_0_) {
        char c0 = Character.toString(p_211165_0_).toLowerCase(Locale.ROOT).charAt(0);

        switch (c0) {
            case '0': return ChatFormatting.BLACK;
            case '1': return ChatFormatting.DARK_BLUE;
            case '2': return ChatFormatting.DARK_GREEN;
            case '3': return ChatFormatting.DARK_AQUA;
            case '4': return ChatFormatting.DARK_RED;
            case '5': return ChatFormatting.DARK_PURPLE;
            case '6': return ChatFormatting.GOLD;
            case '7': return ChatFormatting.GRAY;
            case '8': return ChatFormatting.DARK_GRAY;
            case '9': return ChatFormatting.BLUE;
            case 'a': return ChatFormatting.GREEN;
            case 'b': return ChatFormatting.AQUA;
            case 'c': return ChatFormatting.RED;
            case 'd': return ChatFormatting.LIGHT_PURPLE;
            case 'e': return ChatFormatting.YELLOW;
            case 'f': return ChatFormatting.WHITE;
            case 'k': return ChatFormatting.OBFUSCATED;
            case 'l': return ChatFormatting.BOLD;
            case 'm': return ChatFormatting.STRIKETHROUGH;
            case 'n': return ChatFormatting.UNDERLINE;
            case 'o': return ChatFormatting.ITALIC;
            case 'r': return ChatFormatting.RESET;
        }

        return null;
    }

    /**
     *
     * Removes the colour codes in a message
     *
     * @param input The original message
     * @return The stripped message
     */
    public static String stripColor(@Nullable String input) {
        if (input == null) {
            return null;
        }

        return STRIP_PATTERN.matcher(input).replaceAll("");
    }
}
