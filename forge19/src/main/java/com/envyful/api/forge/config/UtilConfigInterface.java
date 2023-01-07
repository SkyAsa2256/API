package com.envyful.api.forge.config;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.text.Placeholder;

public class UtilConfigInterface {

    public static void fillBackground(Pane pane, ConfigInterface settings, Placeholder... transformers) {
        for (ConfigItem fillerItem : settings.getFillerItems()) {
            if (!fillerItem.isEnabled()) {
                continue;
            }

            pane.add(GuiFactory.displayable(UtilConfigItem.fromConfigItem(fillerItem, transformers)));
        }
    }

}
