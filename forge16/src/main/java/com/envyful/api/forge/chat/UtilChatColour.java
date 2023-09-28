package com.envyful.api.forge.chat;

import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.google.common.collect.Lists;
import net.minecraft.util.text.*;

import javax.annotation.Nullable;
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

    public static List<ITextComponent> colour(List<String> text, Placeholder... placeholders) {
        List<ITextComponent> components = Lists.newArrayList();

        for (String line : text) {
            components.add(colour(line, placeholders));
        }

        return components;
    }

    /**
     *
     * Parses the string to a {@link ITextComponent} with the correctly formatted colour codes and hex codes
     *
     * @param text The unformatted text
     * @return The newly formatted text
     */
    public static ITextComponent colour(String text, Placeholder... placeholders) {
        if (text.contains("{")) {
            try {
                return ITextComponent.Serializer.fromJson(text);
            } catch (Exception ignored) {}
        }

        Matcher matcher = COLOUR_PATTERN.matcher(text);
        IFormattableTextComponent textComponent = new StringTextComponent("");
        TextFormatting nextApply = null;
        int lastEnd = 0;
        Color lastColor = null;

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
                var byCode = getByCode(colourCode.toCharArray()[0]);

                if (byCode != null) {
                    nextApply = byCode;
                } else {
                    textComponent.append(new StringTextComponent("&" + colourCode));
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
     * Attempts to append the segment to the {@link IFormattableTextComponent} with the given (nullable) colour
     *
     * @param textComponent The text component
     * @param segment The segment
     * @param lastColour The colour
     */
    public static IFormattableTextComponent attemptAppend(IFormattableTextComponent textComponent, String segment, Color lastColour, Placeholder... placeholders) {
        if (segment.isEmpty()) {
            return null;
        }

        var appended = new StringTextComponent("");

        for (var text : PlaceholderFactory.handlePlaceholders(segment, placeholders)) {
            var literalText = new StringTextComponent(text);

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
    public static Optional<Color> parseColour(String colourCode) {
        Color colour = Color.parseColor(colourCode);

        if (colour != null) {
            return Optional.of(colour);
        }

        if (colourCode.length() > 1) {
            return Optional.empty();
        }

        TextFormatting byCode = getByCode(colourCode.toCharArray()[0]);

        if (byCode == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(Color.fromLegacyFormat(byCode));
    }

    public static TextFormatting getByCode(char p_211165_0_) {
        char c0 = Character.toString(p_211165_0_).toLowerCase(Locale.ROOT).charAt(0);

        switch (c0) {
            case '0': return TextFormatting.BLACK;
            case '1': return TextFormatting.DARK_BLUE;
            case '2': return TextFormatting.DARK_GREEN;
            case '3': return TextFormatting.DARK_AQUA;
            case '4': return TextFormatting.DARK_RED;
            case '5': return TextFormatting.DARK_PURPLE;
            case '6': return TextFormatting.GOLD;
            case '7': return TextFormatting.GRAY;
            case '8': return TextFormatting.DARK_GRAY;
            case '9': return TextFormatting.BLUE;
            case 'a': return TextFormatting.GREEN;
            case 'b': return TextFormatting.AQUA;
            case 'c': return TextFormatting.RED;
            case 'd': return TextFormatting.LIGHT_PURPLE;
            case 'e': return TextFormatting.YELLOW;
            case 'f': return TextFormatting.WHITE;
            case 'k': return TextFormatting.OBFUSCATED;
            case 'l': return TextFormatting.BOLD;
            case 'm': return TextFormatting.STRIKETHROUGH;
            case 'n': return TextFormatting.UNDERLINE;
            case 'o': return TextFormatting.ITALIC;
            case 'r': return TextFormatting.RESET;
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
