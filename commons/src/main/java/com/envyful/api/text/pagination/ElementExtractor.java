package com.envyful.api.text.pagination;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 *
 * Used for converting an iterable of the type into a list of Strings
 *
 * @param <T>
 */
public class ElementExtractor<T> {

    private final Iterator<T> elements;
    private final Function<T, String> extractor;

    public ElementExtractor(Iterator<T> elements, Function<T, String> extractor) {
        this.elements = elements;
        this.extractor = extractor;
    }

    public List<String> extract() {
        List<String> elements = new ArrayList<>();

        while (this.elements.hasNext()) {
            elements.add(this.extractor.apply(this.elements.next()));
        }

        return elements;
    }
}
