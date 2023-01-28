package com.envyful.api.text.pagination;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Function;

/**
 *
 *
 *
 */
public class Paginator {

    private int pageSize;
    private PaginatorConfig config = new PaginatorConfig();
    private List<String> elements = Lists.newArrayList();

    private Paginator() {}

    public Paginator config(PaginatorConfig config) {
        this.config = config.copy();
        return this;
    }

    public Paginator pageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Paginator title(String title) {
        this.config.setTitle(title);
        return this;
    }

    public Paginator header(String... header) {
        this.config.setHeader(Lists.newArrayList(header));
        return this;
    }

    public Paginator footer(String... footer) {
        this.config.setFooter(Lists.newArrayList(footer));
        return this;
    }

    public Paginator nextPageCommand(String nextPageCommand) {
        this.config.setNextPageCommand(nextPageCommand);
        return this;
    }

    public Paginator previousPageCommand(String previousPageCommand) {
        this.config.setPreviousPageCommand(previousPageCommand);
        return this;
    }

    public Paginator extractor(ElementExtractor<?> extractor) {
        this.elements.addAll(extractor.extract());
        return this;
    }

    public Paginator elements(String... elements) {
        this.elements.addAll(Lists.newArrayList(elements));
        return this;
    }

    public Paginator elements(List<String> elements) {
        this.elements.addAll(elements);
        return this;
    }

    public List<String> getPage(int page) {
        List<String> pageText = Lists.newArrayList();

        for (String s : this.config.getHeader()) {
            pageText.add(s.replace("%title%", this.config.getTitle())
                    .replace("%page%", String.valueOf(page))
                    .replace("%max_page%", String.valueOf((int) Math.max(1, Math.ceil((this.elements.size() + 0.0) / this.pageSize)))));
        }

        for (int i = (this.pageSize * (page - 1)); i < ((this.pageSize * (page - 1)) + this.pageSize); i++) {
            if (this.elements.size() <= i) {
                continue;
            }

            pageText.add(this.elements.get(i));
        }

        for (String s : this.config.getFooter()) {
            pageText.add(s.replace("%title%", this.config.getTitle())
                    .replace("%page%", String.valueOf(page))
                    .replace("%max_page%", String.valueOf((int) Math.max(1, Math.ceil((this.elements.size() + 0.0) / this.pageSize)))));
        }

        return pageText;
    }

    public <T> List<T> getPage(int page, Function<String, T> conversion) {
        List<T> pageText = Lists.newArrayList();

        for (String s : this.config.getHeader()) {
            pageText.add(conversion.apply(s.replace("%title%", this.config.getTitle())
                    .replace("%page%", String.valueOf(page))
                    .replace("%max_page%", String.valueOf((int) Math.max(1, Math.ceil((this.elements.size() + 0.0) / this.pageSize))))));
        }

        for (int i = (this.pageSize * (page - 1)); i < ((this.pageSize * (page - 1)) + this.pageSize); i++) {
            if (this.elements.size() <= i) {
                continue;
            }

            pageText.add(conversion.apply(this.elements.get(i)));
        }

        for (String s : this.config.getFooter()) {
            pageText.add(conversion.apply(s.replace("%title%", this.config.getTitle())
                    .replace("%page%", String.valueOf(page))
                    .replace("%max_page%", String.valueOf((int) Math.max(1, Math.ceil((this.elements.size() + 0.0) / this.pageSize))))));
        }

        return pageText;
    }

    public static Paginator builder() {
        return new Paginator();
    }
}
