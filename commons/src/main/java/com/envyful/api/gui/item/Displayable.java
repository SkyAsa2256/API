package com.envyful.api.gui.item;

import com.envyful.api.player.EnvyPlayer;

/**
 *
 * An interface that represents a displayable item in a page / container that can be clicked, and periodically updated.
 *
 */
public interface Displayable {

    /**
     *
     * Called when the displayable entity has been clicked by a player
     *
     * @param player The player that clicked the object
     * @param clickType The type of click that occurred
     * @return If the event should be cancelled
     */
    boolean onClick(EnvyPlayer<?> player, ClickType clickType);

    /**
     *
     * Called periodically by the parent page / container to update the item on display (if it's a changing item)
     *
     * @param viewer The player viewing the current page / container for which the update should occur
     */
    void update(EnvyPlayer<?> viewer);

    /**
     *
     * An enum representing the type of click coming from the user
     *
     */
    enum ClickType {

        LEFT,
        MIDDLE,
        RIGHT,

        ;

    }
}
