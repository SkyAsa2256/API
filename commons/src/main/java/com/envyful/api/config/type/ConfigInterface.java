package com.envyful.api.config.type;

import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.text.Placeholder;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import uk.co.envyware.helios.RequiredMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * A serializable object that can be used to represent basic settings for a GUI
 *
 */
@ConfigSerializable
public class ConfigInterface {

    protected String title = "";
    protected int height = 6;
    protected String fillType = FillType.BLOCK.name();
    protected Map<String, ConfigItem> fillerItems = Map.of("one", new ConfigItem());
    protected Map<String, ExtendedConfigItem> displayItems = new HashMap<>();

    public ConfigInterface() {}

    protected ConfigInterface(Builder builder) {
        this.title = builder.title;
        this.height = builder.height;
        this.fillType = builder.fillType.name();
        this.fillerItems = builder.fillerItems;
    }

    /**
     *
     * Gets a default config interface with the given title
     *
     * @param title The title
     * @return The config interface
     */
    public static ConfigInterface defaultInterface(String title) {
        return defaultInterface(title, 6);
    }

    /**
     *
     * Gets a default config interface with the given title and height
     *
     * @param title The title
     * @param height The height
     * @return The config interface
     */
    public static ConfigInterface defaultInterface(String title, int height) {
        return builder()
                .title(title)
                .height(height)
                .fillType(FillType.BLOCK)
                .fillerItem(ConfigItem.builder()
                        .type("minecraft:black_stained_glass_pane")
                        .amount(1)
                        .name(" ")
                        .build())
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTitle() {
        return this.title;
    }

    public int getHeight() {
        return this.height;
    }

    public FillType getFillType() {
        return FillType.valueOf(this.fillType);
    }

    public List<ConfigItem> getFillerItems() {
        return this.getFillType().convert(
                List.copyOf(this.fillerItems.values()),
                this.getHeight());
    }

    public List<ExtendedConfigItem> getDisplayItems() {
        return List.copyOf(this.displayItems.values());
    }

    public Pane.Builder toPaneBuilder() {
        return GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(this.height);
    }

    public Pane toPane(Placeholder... placeholders) {
        var pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(this.height)
                .build();

        this.fillPane(pane);

        return pane;
    }

    public void fillPane(Pane pane, Placeholder... placeholders) {
        for (var fillerItem : this.getFillerItems()) {
            if (!fillerItem.isEnabled()) {
                continue;
            }

            pane.add(fillerItem.toDisplayable(placeholders));
        }
    }

    public enum FillType {

        BLOCK() {
            @Override
            public List<ConfigItem> convert(List<ConfigItem> conversion,
                                            int height) {
                List<ConfigItem> configItems = new ArrayList<>();
                ConfigItem primary = conversion.get(0);

                for(int y = 0; y < height; y++) {
                    for (int x = 0; x < 9; x++) {
                        configItems.add(primary);
                    }
                }

                return configItems;
            }
        },
        CUSTOM() {
            @Override
            public List<ConfigItem> convert(List<ConfigItem> conversion,
                                            int height) {
                return conversion;
            }
        },
        ALTERNATING() {
            @Override
            public List<ConfigItem> convert(List<ConfigItem> conversion,
                                            int height) {
                List<ConfigItem> configItems = new ArrayList<>();
                ConfigItem primary = conversion.get(0);
                ConfigItem secondary = conversion.get(1);

                for(int y = 0; y < height; y++) {
                    for (int x = 0; x < 9; x++) {
                        if (x % 2 == 0) {
                            configItems.add(primary);
                        } else {
                            configItems.add(secondary);
                        }
                    }
                }

                return configItems;
            }
        },
        CHECKERED() {
            @Override
            public List<ConfigItem> convert(List<ConfigItem> conversion,
                                            int height) {
                List<ConfigItem> configItems = new ArrayList<>();
                ConfigItem primary = conversion.get(0);
                ConfigItem secondary = conversion.get(1);

                for(int y = 0; y < height; y++) {
                    for (int x = 0; x < 9; x++) {
                        if ((x + (y  % 2)) % 2 == 0) {
                            configItems.add(primary);
                        } else {
                            configItems.add(secondary);
                        }
                    }
                }

                return configItems;
            }
        }

        ;

        public abstract List<ConfigItem> convert(List<ConfigItem> conversion,
                                                 int height);
    }

    public static class Builder {

        protected String title;
        protected int height;
        protected FillType fillType;
        protected Map<String, ConfigItem> fillerItems = new HashMap<>();

        private Builder() {}

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder fillType(FillType fillType) {
            this.fillType = fillType;
            return this;
        }

        public Builder fillerItem(ConfigItem configItem) {
            this.fillerItems.put(String.valueOf(this.fillerItems.size() + 1), configItem);
            return this;
        }

        @RequiredMethod({
                "height",
                "title",
                "fillType"
        })
        public ConfigInterface build() {
            return new ConfigInterface(this);
        }
    }
}
