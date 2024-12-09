package com.envyful.api.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 *
 * Configuration class for creating a progress bar
 *
 */
@ConfigSerializable
public class ProgressBar {

    private String progressBar = "&7[&a%filled%&7%empty%&7]";
    private String filledProgressBar = "&a|";
    private String emptyProgressBar = "&c|";
    private int progressBarLength = 10;

    public ProgressBar() {
    }

    public ProgressBar(String progressBar, String filledProgressBar, String emptyProgressBar, int progressBarLength) {
        this.progressBar = progressBar;
        this.filledProgressBar = filledProgressBar;
        this.emptyProgressBar = emptyProgressBar;
        this.progressBarLength = progressBarLength;
    }

    public String createProgressBar(double percentage) {
        int filled = (int) Math.round(percentage * this.progressBarLength);
        int empty = this.progressBarLength - filled;

        return this.progressBar
                .replace("%filled%", this.filledProgressBar.repeat(filled))
                .replace("%empty%", this.emptyProgressBar.repeat(empty));
    }
}
