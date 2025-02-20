package com.envyful.api.config;

import com.envyful.api.text.Placeholder;
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

    /**
     *
     * Creates a progress bar based on the percentage
     *
     * @param percentage The percentage
     * @return The progress bar
     */
    public String createProgressBar(double percentage) {
        int filled = (int) Math.round(percentage * this.progressBarLength);
        int empty = this.progressBarLength - filled;

        return this.progressBar
                .replace("%filled%", this.filledProgressBar.repeat(Math.max(0, filled)))
                .replace("%empty%", this.emptyProgressBar.repeat(Math.max(0, empty)));
    }

    /**
     *
     * Creates a progress bar placeholder based on the progress and total
     *
     * @param progress The progress
     * @param total The total
     * @return The placeholder
     */
    public Placeholder placeholder(double progress, double total) {
        return Placeholder.simple("%progress_bar%", this.createProgressBar(progress / total));
    }

    /**
     *
     * Creates a progress bar placeholder based on the percentage
     *
     * @param percentage The percentage
     * @return The placeholder
     */
    public Placeholder placeholder(double percentage) {
        return Placeholder.simple("%progress_bar%", this.createProgressBar(percentage));
    }
}
