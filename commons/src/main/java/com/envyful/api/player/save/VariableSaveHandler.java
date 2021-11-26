package com.envyful.api.player.save;

/**
 *
 * Used for custom save handling of specific variables
 *
 * @param <A> The initial type
 * @param <B> The resultant type (typically a JSONified string)
 */
public interface VariableSaveHandler<A, B> {

    /**
     *
     * Used for converting from {@link A} to {@link B}
     *
     * @param t The data being converted
     * @return The converted data
     */
    B convert(A t);

    /**
     *
     * Used for reverting the conversion process from {@link B} to {@link A}
     *
     * @param b The data being reverted
     * @return The inverted data
     */
    A invert(B b);

}
