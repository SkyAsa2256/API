package com.envyful.api.text.pagination;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class PaginatorConfig {

    private String title;
    private List<String> header;
    private List<String> footer;
    private String nextPageCommand;
    private String previousPageCommand;

    public PaginatorConfig(String title, List<String> header, List<String> footer, String nextPageCommand, String previousPageCommand) {
        this.title = title;
        this.header = header;
        this.footer = footer;
        this.nextPageCommand = nextPageCommand;
        this.previousPageCommand = previousPageCommand;
    }

    public PaginatorConfig() {
    }

    public String getTitle() {
        return this.title;
    }

    public List<String> getHeader() {
        return this.header;
    }

    public List<String> getFooter() {
        return this.footer;
    }

    public String getNextPageCommand() {
        return this.nextPageCommand;
    }

    public String getPreviousPageCommand() {
        return this.previousPageCommand;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public void setFooter(List<String> footer) {
        this.footer = footer;
    }

    public void setNextPageCommand(String nextPageCommand) {
        this.nextPageCommand = nextPageCommand;
    }

    public void setPreviousPageCommand(String previousPageCommand) {
        this.previousPageCommand = previousPageCommand;
    }

    public PaginatorConfig copy() {
        return new PaginatorConfig(
                this.title, this.header, this.footer, this.nextPageCommand, this.previousPageCommand
        );
    }
}
