package com.envyful.api.text;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 *
 * A class containing utility methods for the {@link String} class
 *
 */
public class UtilString {

    private UtilString() {
        throw new UnsupportedOperationException("Static utility");
    }

    /**
     *
     * Applies the mapper given to each element converting it to a string,
     * and then joins them all into a single string with each element
     * being separated by the delimiter given.
     *
     * @param iterable The elements being converted to a String
     * @param delimiter The separater between the elements in the String
     * @param mapper The converter from T to String
     * @return The joined String
     * @param <T> The type being converted
     */
    public static <T> String join(
            Iterable<T> iterable,
            String delimiter,
            Function<T, String> mapper) {
        StringBuilder builder = new StringBuilder();

        for (T t : iterable) {
            builder.append(delimiter).append(mapper.apply(t));
        }

        return builder.substring(delimiter.length());
    }

    /**
     *
     * Takes the original String and shuffles the characters
     * creating a randomly ordered new String
     *
     * @param originalString The original String
     * @return The shuffled String
     */
    public static String shuffle(String originalString) {
        char[] chars = originalString.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int j = ThreadLocalRandom.current().nextInt(chars.length);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

       return new String(chars);
    }
}
