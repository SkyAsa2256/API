package com.envyful.api.config.type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ConfigSerializable
public class PaginatedConfigInterface extends ConfigInterface {

    protected List<Integer> positions = Lists.newArrayList(
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 36,
            37, 38, 39, 40, 41, 42, 43, 44, 45
    );

    protected ExtendedConfigItem nextPageButton;
    protected ExtendedConfigItem previousPageButton;

    public PaginatedConfigInterface() {
    }

    public List<Integer> getPositions() {
        return this.positions;
    }

    public ExtendedConfigItem getNextPageButton() {
        return this.nextPageButton;
    }

    public ExtendedConfigItem getPreviousPageButton() {
        return this.previousPageButton;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String title;
        private int height = 6;
        private FillType fillType = FillType.BLOCK;
        private Map<String, ConfigItem> fillerItems = Maps.newHashMap(ImmutableMap.of("one", ConfigItem.builder().type("minecraft:black_stained_glass_pane").amount(1).name(" ").build()));
        private List<Integer> positions = Lists.newArrayList(
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 10, 11, 12, 13, 14, 15, 16, 17,
                18, 19, 20, 21, 22, 23, 24, 25, 26,
                27, 28, 29, 30, 31, 32, 33, 34, 36,
                37, 38, 39, 40, 41, 42, 43, 44, 45
        );
        protected ExtendedConfigItem nextPageButton;
        protected ExtendedConfigItem previousPageButton;

        private Builder() {
            // Internal constructor for the static factory method
        }

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

        public Builder fillerItems(Map<String, ConfigItem> fillerItems) {
            this.fillerItems = fillerItems;
            return this;
        }

        public Builder positions(int... positions) {
            this.positions = Arrays.stream(positions).boxed().collect(Collectors.toList());
            return this;
        }

        public Builder nextPageButton(ExtendedConfigItem nextPageButton) {
            this.nextPageButton = nextPageButton;
            return this;
        }

        public Builder previousPageButton(ExtendedConfigItem previousPageButton) {
            this.previousPageButton = previousPageButton;
            return this;
        }

        public PaginatedConfigInterface build() {
            PaginatedConfigInterface configInterface = new PaginatedConfigInterface();
            configInterface.title = this.title;
            configInterface.height = this.height;
            configInterface.fillType = this.fillType.name();
            configInterface.fillerItems = this.fillerItems;
            configInterface.positions = this.positions;
            configInterface.nextPageButton = this.nextPageButton;
            configInterface.previousPageButton = this.previousPageButton;
            return configInterface;
        }
    }
}
